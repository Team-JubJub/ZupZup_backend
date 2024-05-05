package com.rest.api.review.service.impl;

import com.rest.api.AWS.S3Uploader;
import com.rest.api.FCM.dto.FCMAlertDto;
import com.rest.api.FCM.service.FCMService;
import com.rest.api.review.model.domain.Review;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.model.dto.ReviewResponse;
import com.rest.api.review.repository.ReviewRepository;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.exception.exception.NoSuchException;
import com.zupzup.untact.model.domain.order.Order;
import com.zupzup.untact.model.domain.order.type.OrderSpecific;
import com.zupzup.untact.model.domain.store.Store;
import com.zupzup.untact.repository.OrderRepository;
import com.zupzup.untact.repository.StoreRepository;
import com.zupzup.untact.repository.UserRepository;
import com.zupzup.untact.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl extends BaseServiceImpl<Review, ReviewRequest, ReviewResponse, ReviewRepository> implements ReviewService {

    public ReviewServiceImpl(ReviewRepository repository, ReviewRepository reviewRepository, UserRepository userRepository, StoreRepository storeRepository, OrderRepository orderRepository, S3Uploader s3Uploader, FCMService fcmService) {
        super(repository);
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.storeRepository = storeRepository;
        this.orderRepository = orderRepository;
        this.s3Uploader = s3Uploader;
        this.fcmService = fcmService;
    }

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final S3Uploader s3Uploader;
    private final FCMService fcmService;

    @Autowired
    ModelMapper modelMapper;

    /**
     * 리뷰 저장
     */
    public ReviewResponse save(ReviewRequest reviewRequest, MultipartFile reviewImage, String providerUserID) throws Exception {

        // 이미지 있을 시 이미지 저장, 없을 시 빈 문자열 저장
        String imageURL = "";
        String nickname = userRepository.findByProviderUserId(providerUserID)
                .orElseThrow(() -> new NoSuchException("해당 ID를 가진 유저가 존재하지 않습니다."))
                .getNickName();

        if(reviewImage != null) {
            imageURL = s3Uploader.upload(reviewImage, nickname);
        }

        // 주문 내역 통해서 StoreID, 가게 메뉴 가져오기
        Order order = orderRepository.findById(reviewRequest.getOrderID())
                .orElseThrow(() -> new NoSuchException("해당 ID를 가진 주문이 존재하지 않습니다."));

        Long storeID = order.getStoreId();
        String menuList = "";
        List<OrderSpecific> orderList = order.getOrderList();

        for (OrderSpecific o : orderList) {
            menuList += (o.getItemName() + ", ");
        }

        // review 엔티티 빌더
        Review review = Review.builder()
                .nickname(nickname)
                .starRate(reviewRequest.getStarRate())
                .content(reviewRequest.getContent())
                .imageURL(imageURL)
                .menu(menuList)
                .orderID(reviewRequest.getOrderID())
                .providerUserID(providerUserID)
                .created_at(timeSetter())
                .build();

        reviewRepository.save(review);

        // 사장님한테 푸시 알림 전송
        sendMessage(storeID, "리뷰 작성", menuList + "에 대한 리뷰가 작성되었습니다!");

        return modelMapper.map(review, ReviewResponse.class);
    }

    @Override
    public List<ReviewListResponse> findAll(String providerUserID) throws Exception {

        // 자신이 쓴 리뷰만 모아보기
        List<Review> reviewList = reviewRepository.findAllByProviderUserID(providerUserID);

        return reviewList.stream()
                .map(entity -> modelMapper.map(entity, ReviewListResponse.class))
                .collect(Collectors.toList());
    }

    //<--------- alert method ---------->

    /**
     * 사장님에게 알림 보내기
     */
    public void sendMessage(Long storeId, String title, String message) {
        Store storeEntity = storeRepository.findById(storeId).get();
        List<String> deviceTokens = new ArrayList<>(storeEntity.getDeviceTokens());
        for (int i = 0; i < deviceTokens.size(); i++) {
            String deviceToken = deviceTokens.get(i);
            FCMAlertDto fcmAlertDto = new FCMAlertDto(deviceToken, title, message);
            String result = fcmService.sendMessage(fcmAlertDto);
            System.out.println("사장님 앱에 보낸 알림, 디바이스 토큰 " + deviceToken + "'s result : " + result);
        }
    }

    /**
     * 시간 포매팅
     */
    private String timeSetter() {

        ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedOrderTime = nowTime.format(formatter);

        return formattedOrderTime;
    }
}
