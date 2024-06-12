package com.rest.api.review.service.impl;

import com.rest.api.FCM.dto.FCMAlertDto;
import com.rest.api.FCM.service.FCMService;
import com.rest.api.aws.S3Uploader;
import com.rest.api.review.model.domain.Review;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.repository.ReviewRepository;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.exception.auth.customer.NoUserPresentsException;
import com.zupzup.untact.exception.store.StoreException;
import com.zupzup.untact.exception.store.order.NoSuchException;
import com.zupzup.untact.model.domain.auth.user.User;
import com.zupzup.untact.model.domain.order.Order;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.model.enums.OrderSpecific;
import com.zupzup.untact.repository.OrderRepository;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.repository.UserRepository;
import com.zupzup.untact.social.utils.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.zupzup.untact.exception.store.StoreExceptionType.NO_MATCH_STORE;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final S3Uploader s3Uploader;
    private final FCMService fcmService;
    private final AuthUtils authUtils;
    private final ModelMapper modelMapper;

    private static final int PAGE_SIZE = 20;

    /**
     * 리뷰 저장
     */
    @Override
    @Transactional
    public Long save(ReviewRequest reviewRequest, MultipartFile reviewImage, String accessToken) throws Exception {

        User userEntity = authUtils.getUserEntity(accessToken);

        // 이미지 있을 시 이미지 저장, 없을 시 빈 문자열 저장
        String imageURL = "";
        String nickname = userRepository.findById(userEntity.getId())
                .orElseThrow(NoUserPresentsException::new)
                .getNickName();

        if(reviewImage != null) {
            imageURL = s3Uploader.upload(reviewImage, nickname);
        }

        // 주문 내역 통해서 StoreID, 가게 메뉴 가져오기
        Order order = orderRepository.findById(reviewRequest.getOrderID())
                .orElseThrow(() -> new NoSuchException("해당 주문을 찾을 수 없습니다."));

        Long storeID = order.getStoreId();
        String menuList = menuList(order);

        // review 엔티티 빌더
        Review review = Review.builder()
                .starRate(reviewRequest.getStarRate())
                .content(reviewRequest.getContent())
                .imageURL(imageURL)
                .order(order)
                .userID(userEntity.getId())
                .createdAt(timeSetter())
                .build();

        reviewRepository.save(review);

        // 가게 리뷰 개수 추가
        Store store = storeRepository.findById(storeID)
                .orElseThrow(() -> new StoreException(NO_MATCH_STORE));
        store.addReviewCount();

        // 가게 별점 업데이트
        store.calculateStarRate(store.getReviewCount(), review.getStarRate());

        // 사장님한테 푸시 알림 전송
        sendMessage(storeID, "리뷰 작성", menuList + "에 대한 리뷰가 작성되었습니다!");

        return review.getId();
    }

    /**
     * 자신이 쓴 리뷰 전체보기
     */
    @Override
    @Transactional(readOnly = true)
    public List<ReviewListResponse> findAll(int pageNo,
                                            String accessToken) {

        User userEntity = authUtils.getUserEntity(accessToken);

        // 자신이 쓴 리뷰만 모아보기
        Pageable pageable = PageRequest.of(pageNo, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Review> reviewList = reviewRepository.findAllByUserID(userEntity.getId(), pageable);

        List<ReviewListResponse> reviewResponse = new ArrayList<>();

        for (Review review : reviewList) {
            ReviewListResponse reviewListResponse = modelMapper.map(review, ReviewListResponse.class);

            // 닉네임 추가
            reviewListResponse.setNickname(userRepository.findById(review.getUserID())
                    .orElseThrow(NoUserPresentsException::new)
                    .getNickName());

            // 메뉴 추가
            Order order = review.getOrder();
            reviewListResponse.setMenu(menuList(order));

            reviewResponse.add(reviewListResponse);
        }

        return reviewResponse;
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public Long delete(Long reviewID, String accessToken) {

        User userEntity = authUtils.getUserEntity(accessToken);

        // 유저가 존재하지 않으면 에러 발생
        if (userEntity == null) {
            throw new NoUserPresentsException();
        }

        Review review = reviewRepository.findById(reviewID)
                .orElseThrow(() -> new NoSuchException("해당 리뷰를 찾을 수 없습니다."));

        // 주문 내역 통해서 StoreID, 가게 메뉴 가져오기
        Order order = review.getOrder();
        Long storeID = order.getStoreId();
        String menuList = menuList(order);

        // 리뷰 삭제
        reviewRepository.delete(review);

        // 가게 리뷰 개수 감소
        Store store = storeRepository.findById(storeID)
                .orElseThrow(() -> new StoreException(NO_MATCH_STORE));
        store.subtractReviewCount();

        // 가게 별점 업데이트
        store.calculateStarRate(store.getReviewCount(), 0);

        // 사장님한테 푸시 알림 전송
        sendMessage(storeID, "리뷰 삭제", menuList + "에 대한 리뷰가 삭제되었습니다!");

        return reviewID;
    }

    //<--------- incidental method ---------->

    /**
     * 사장님에게 알림 보내기
     */
    public void sendMessage(Long storeId, String title, String message) {
        Store storeEntity = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(NO_MATCH_STORE));

        List<String> deviceTokens = new ArrayList<>(storeEntity.getDeviceTokens());

        for (String deviceToken : deviceTokens) {
            FCMAlertDto fcmAlertDto = new FCMAlertDto(deviceToken, title, message);
            String result = fcmService.sendMessage(fcmAlertDto);
            System.out.println("사장님 앱에 보낸 알림, 디바이스 토큰 " + deviceToken + "'s result : " + result);
        }
    }

    /**
     * 리뷰에 보일 메뉴 리스트화
     */
    public String menuList(Order order) {

        StringBuilder menuList = new StringBuilder();
        List<OrderSpecific> orderList = order.getOrderList();

        for (OrderSpecific o : orderList) {
            menuList.append(o.getItemName()).append(", ");
        }

        return menuList.toString();
    }

    /**
     * 시간 포매팅
     */
    private String timeSetter() {

        ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return nowTime.format(formatter);
    }
}
