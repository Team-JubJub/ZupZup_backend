package com.rest.api.info.service;

import com.zupzup.untact.exception.auth.customer.AlreadySignUppedException;
import com.zupzup.untact.exception.auth.customer.NoUserPresentsException;
import com.zupzup.untact.model.enums.auth.Role;
import com.zupzup.untact.model.enums.auth.Provider;
import com.zupzup.untact.model.domain.auth.user.User;
import com.zupzup.untact.model.domain.order.Order;
import com.zupzup.untact.model.enums.OrderStatus;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.dto.MessageDto;
import com.zupzup.untact.dto.auth.customer.UserDto;
import com.zupzup.untact.dto.auth.customer.request.AccountRecoveryDto;
import com.zupzup.untact.dto.auth.customer.request.UserSignInDto;
import com.zupzup.untact.dto.auth.customer.request.UserSignUpDto;
import com.zupzup.untact.dto.auth.token.customer.CustomerRefreshResultDto;
import com.zupzup.untact.dto.auth.token.customer.CustomerTokenInfoDto;
import com.zupzup.untact.dto.store.StoreDto;
import com.zupzup.untact.repository.OrderRepository;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.repository.UserRepository;
import com.zupzup.untact.social.jwt.SocialJwtTokenProvider;
import com.zupzup.untact.social.redis.SocialRedisService;
import com.zupzup.untact.social.utils.AuthUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Log
@Transactional
public class MobileOAuthService {

    @Autowired
    ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final SocialJwtTokenProvider jwtTokenProvider;
    private final SocialRedisService redisService;
    private final AuthUtils authUtils;

    final static public String NO_USER_FOUND = "No user found";

    // <-------------------- Sign-up part -------------------->
    public CustomerTokenInfoDto signUp(String provider, UserSignUpDto userSignUpDto) {
        checkIsSignUpped(userSignUpDto.getPhoneNumber());
        UserDto userDto = userSignUpDtoToUserDto(provider, userSignUpDto);

        User userEntity = User.builder()
                .providerUserId(userDto.getProviderUserId())
                .provider(userDto.getProvider())
                .userName(userDto.getUserName())
                .nickName(userDto.getNickName())
                .gender(userDto.getGender())
                .phoneNumber(userDto.getPhoneNumber())
                .starredStores(userDto.getStarredStores())
                .alertStores(userDto.getAlertStores())
                .essentialTerms(userDto.getEssentialTerms())
                .optionalTerm1(userDto.getOptionalTerm1())
                .registerTime(registerTimeSetter())
                .deviceToken(userDto.getDeviceToken())
                .role(userDto.getRole())
                .build();
        userRepository.save(userEntity);
        userDto.setUserId(userEntity.getId());  // user id와 registertime은 user entity 생성 시점에 만들어지므로 다시 dto에 set
        userDto.setRegisterTime(userEntity.getRegisterTime());
        CustomerTokenInfoDto customerTokenInfoDto = generateTokens(userDto, "Create user success");

        return customerTokenInfoDto;
    }

    public String getAppleRefreshToken(String authCode) {
        String appleClientSecret = jwtTokenProvider.generateAppleClientSecret();
        String appleRefreshToken = jwtTokenProvider.getAppleRefreshToken(appleClientSecret, authCode);

        return appleRefreshToken;
    }

    public MessageDto deleteUser(String accessToken, String refreshToken) {
        Long remainExpiration = jwtTokenProvider.remainExpiration(accessToken); // 남은 expiration을 계산함.
        MessageDto deleteUserMessageDto = new MessageDto(null);
        if (remainExpiration >= 1) {   // 만료 직전 혹은 만료된 토큰이 아니라면
            deleteUserMessageDto.setMessage(jwtTokenProvider.SUCCESS_STRING);
            String providerUserId = jwtTokenProvider.getProviderUserId(accessToken);
            User userEntity = userRepository.findByProviderUserId(providerUserId).get();    // delete()와 deleteById() 모두 findBy로 유저 엔티티 찾는 과정은 거침. 예외 처리를 직접 하는 것이냐 아니냐의 차이인데, 일단 이렇게 적용하고 delete()가 더 나을지 고민해볼 것.
            userRepository.deleteById(userEntity.getId());  // RDB에서 유저 삭제
            redisService.deleteKey(refreshToken); // refreshToken을 key로 하는 데이터 redis에서 삭제
            redisService.setStringValue(accessToken, "deleted-user", remainExpiration); // access token 저장(key: acc_token, value: "deleted-user")
            changeOrderStatusWithdrew(userEntity.getId());  // 탈퇴하는 유저의 주문 상태를 변경
            deleteUserStarAlertAtStore(userEntity); // 탈퇴하는 유저가 찜한 가게의 찜, 알림 설정 유저 목록에서 해당 유저의 id를 뺌

            return deleteUserMessageDto;
        }
        deleteUserMessageDto.setMessage(jwtTokenProvider.EXPIRED_ACCESS_TOKEN);

        return deleteUserMessageDto;   // 만료된 access token인 경우
    }

    public MessageDto deleteAppleUser (String refreshToken) {
        String clientSecret = jwtTokenProvider.generateAppleClientSecret();
        jwtTokenProvider.withDrawApple(clientSecret, refreshToken); // 애플로 회원의 연결끊기 요청(이 함수 내부에서 애플에서 400을 주면 예외처리해놨음)

        MessageDto deleteUserMessageDto = new MessageDto(null);
        deleteUserMessageDto.setMessage(jwtTokenProvider.SUCCESS_STRING);

        return deleteUserMessageDto;
    }

    public Boolean nickNameCheck(String nickName) {
        Optional<User> userEntity = userRepository.findByNickName(nickName);
        if(userEntity.isPresent()) {
            return true;
        }

        return false;
    }

    // <-------------------- Sign-in part -------------------->
    public CustomerRefreshResultDto signInWithRefreshToken(String refreshToken) {
        CustomerRefreshResultDto refreshResult = jwtTokenProvider.validateRefreshToken(refreshToken);   // refresh token 유효성 검증
        if (refreshResult.getResult().equals(jwtTokenProvider.SUCCESS_STRING)) {    // Refresh token 유효성 검증 성공 시 헤더에 액세스 토큰, 바디에 result, message, id, 토큰 전달
            redisService.deleteKey(refreshToken);   // 기존 리프레시 토큰 삭제
            redisService.setStringValue(refreshResult.getRefreshToken(), refreshResult.getProviderUserId(), SocialJwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);   // 새 리프레시 토큰 저장
        }

        return refreshResult;
    }

    public CustomerTokenInfoDto signInWithProviderUserId(String provider, UserSignInDto userSignInDto) {
        String userUniqueId = userSignInDto.getUserUniqueId();
        String deviceToken = userSignInDto.getDeviceToken();
        CustomerTokenInfoDto customerTokenInfoDto = null;
        try {
            User userEntity = userRepository.findByProviderUserId(provider.toUpperCase() + "_" + userUniqueId).get();
            UserDto userDto = modelMapper.map(userEntity, UserDto.class);
            userDto.setDeviceToken(deviceToken);    // 로그인 시마다 FCM device token 받아와서 수정
            userEntity.updateDeviceToken(userDto);
            userRepository.save(userEntity);

            customerTokenInfoDto = generateTokens(userDto, "Token refreshed");
        } catch (NoSuchElementException e) {
            throw new NoUserPresentsException();
        }

        return customerTokenInfoDto;
    }

    // < -------------- Sign-out part -------------- >
    public String signOut(String accessToken, String refreshToken) {
        User userEntity = authUtils.getUserEntity(accessToken);
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        Long remainExpiration = jwtTokenProvider.remainExpiration(accessToken); // 남은 expiration을 계산함.
        if (remainExpiration >= 1) {
            userDto.setDeviceToken(null);   // 로그아웃 시 알림을 안보내기 위해 device token 삭제
            userEntity.updateDeviceToken(userDto);
            userRepository.save(userEntity);
            redisService.deleteKey(refreshToken); // refreshToken을 key로 하는 데이터 redis에서 삭제
            redisService.setStringValue(accessToken, "sign-out", remainExpiration); // access token 저장(key: acc_token, value: "sign-out")

            return "success";
        }

        return "fail";
    }

    // <-------------------- Account recovery part -------------------->
    public String accountRecovery(AccountRecoveryDto accountRecoveryDto) {
        String phoneNumber = accountRecoveryDto.getPhoneNumber();
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isEmpty()) {  // 가입된 회원이 없으면
            return NO_USER_FOUND;
        }
        String provider = userEntity.get().getProvider().getProvider();

        return provider;
    }


    // <-------------------- Common methods part -------------------->
    // <--- Methods for error handling --->
    private void checkIsSignUpped(String phoneNumber) {
        Optional<User> userEntity = userRepository.findByPhoneNumber(phoneNumber);
        if(userEntity.isPresent()) {
            throw new AlreadySignUppedException(userEntity.get().getProvider()); // "User already sign-upped.(Platform with: {provider})
        }
    }

    // <--- Methods for readability --->
    private String registerTimeSetter() {
        ZonedDateTime nowDateTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");   // 09:43 AM, 04:57 PM
        String formattedRegisterTime = nowDateTime.format(formatter);

        return formattedRegisterTime;
    }

    private UserDto userSignUpDtoToUserDto(String provider, UserSignUpDto userSignUpDto) {
        UserDto userDto = new UserDto();
        if((provider.toUpperCase()).equals(Provider.NAVER.getProvider())) {
            userDto.setProvider(Provider.NAVER);
        }
        else if((provider.toUpperCase()).equals(Provider.KAKAO.getProvider())) {
            userDto.setProvider(Provider.KAKAO);
        }
        else if((provider.toUpperCase()).equals(Provider.APPLE.getProvider())) {
            userDto.setProvider(Provider.APPLE);
        }
        else if((provider.toUpperCase()).equals(Provider.GOOGLE.getProvider())) {
            userDto.setProvider(Provider.GOOGLE);
        }
        userDto.setProviderUserId(provider.toUpperCase() + "_" + userSignUpDto.getUserUniqueId());
        userDto.setUserName(userSignUpDto.getUserName());
        userDto.setNickName(userSignUpDto.getNickName());
        userDto.setGender(userSignUpDto.getGender());
        userDto.setPhoneNumber(userSignUpDto.getPhoneNumber());
        userDto.setStarredStores(new HashSet<>());
        userDto.setAlertStores(new HashSet<>());
        userDto.setEssentialTerms(userSignUpDto.getEssentialTerms());
        userDto.setOptionalTerm1(userSignUpDto.getOptionalTerm1());
        userDto.setDeviceToken(userSignUpDto.getDeviceToken());
        userDto.setRole(Role.ROLE_USER);

        return userDto;
    }

    private CustomerTokenInfoDto generateTokens(UserDto userDto, String message) {
        List<String> roles = Arrays.asList(userDto.getRole().getRole());
        String accessToken = jwtTokenProvider.generateAccessToken(userDto.getProviderUserId(), roles);
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        redisService.setStringValue(refreshToken, userDto.getProviderUserId(), SocialJwtTokenProvider.REFRESH_TOKEN_VALIDITY_IN_MILLISECONDS);
        CustomerTokenInfoDto customerTokenInfoDto = new CustomerTokenInfoDto("success", message, accessToken, refreshToken, userDto);

        return customerTokenInfoDto;
    }

    private void changeOrderStatusWithdrew(Long userId) {  // 사용자 회원탈퇴 시 해당 사용자의 주문 상태 변경하는 함수
        List<Order> userOrderList = orderRepository.findByUserId(userId);
        for (int i = 0; i < userOrderList.size(); i++) {
            Order userOrder = userOrderList.get(i);
            userOrder.updateOrder(OrderStatus.WITHDREW);    // 주문 상태 변경
            orderRepository.save(userOrder);
        }
    }

    private void modifyStoreStarAlert(Long userId, Long starredStoreId) {   // 회원탈퇴를 진행하는 유저가 찜한 가게에 대해 수행
        Store storeEntity = storeRepository.findById(starredStoreId).get();
        StoreDto storeDto = modelMapper.map(storeEntity, StoreDto.class);

        Set<Long> starredUserList = storeDto.getStarredUsers();
        Set<Long> alertUserList = storeDto.getAlertUsers();
        if (starredUserList.contains(userId)) starredUserList.remove(Long.valueOf(userId)); // 해당 유저의 아이디를 가지고 있으면 삭제
        if (alertUserList.contains(userId)) alertUserList.remove(Long.valueOf(userId));
        storeDto.setStarredUsers(starredUserList);  // dto에 바뀐 set 저장
        storeDto.setAlertUsers(alertUserList);

        storeEntity.updateStarredUserList(storeDto);
        storeRepository.save(storeEntity);
    }

    private void deleteUserStarAlertAtStore(User userEntity) {
        Long userId = userEntity.getId();
        Set<Long> starredStoresSet = userEntity.getStarredStores();
        if (starredStoresSet != null) { // 찜한 가게가 null이 아닐 때만 수행, 혹시 모르는 null 체크
            List<Long> starredStores = new ArrayList<>(starredStoresSet);
            for (int i = 0; i < starredStores.size(); i++) {    // 유저가 찜한 가게에 대해 수행
                Long starredStoreId = starredStores.get(i);
                modifyStoreStarAlert(userId, starredStoreId);
            }
        }
    }

    // <-------------------- Test part -------------------->
    public UserDto signInTestToken(String accessToken, String refreshToken) {
        List<String> findInfo = redisService.getListValue(refreshToken);
        String providerUserIdRefresh = findInfo.get(0);
        System.out.println(providerUserIdRefresh);

        String providerUserIdAccess = jwtTokenProvider.getProviderUserId(accessToken);
        User userEntity = userRepository.findByProviderUserId(providerUserIdAccess).get();
        UserDto userDto = modelMapper.map(userEntity, UserDto.class);

        return userDto;
    }

}
