package com.rest.api.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.document.RestDocsConfig;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.model.dto.ReviewRequest;
import com.rest.api.review.service.ReviewService;
import com.zupzup.untact.exception.auth.customer.NoUserPresentsException;
import com.zupzup.untact.exception.store.StoreException;
import com.zupzup.untact.exception.store.order.NoSuchException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.zupzup.untact.exception.store.StoreExceptionType.NO_MATCH_STORE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import({RestDocsConfig.class})
@ExtendWith({RestDocumentationExtension.class})
@AutoConfigureMockMvc
public class ReviewApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    String accessToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0Iiwicm9sZSI6W3sibmFtZSI6IlJPTEVfU0VMTEVSIn1dLCJpYXQiOjE2OTQzNDQ3MDAsImV4cCI6MTY5NDM0ODMwMH0.sLm_sw3uhWH1CbuC8MHyY1f1fAWKw6u22nynkOTzNTk";

    @BeforeEach
    public void before(WebApplicationContext ctx, RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("리뷰 작성 - 성공")
    public void success_review_save() throws Exception {

        // given
        // Request 설정
        ReviewRequest rq = new ReviewRequest();
        rq.setContent("test content");
        rq.setStarRate(4.5F);
        rq.setOrderID(1L);

        // Response 설정
        Long reviewID = 1L;

        String jsonRq = objectMapper.writeValueAsString(rq);

        // 'image' 파트
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        // 'review' 파트
        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", jsonRq.getBytes());

        given(reviewService.save(any(ReviewRequest.class), any(MultipartFile.class), anyString())).willReturn(reviewID);

        // when
        mockMvc.perform(
                        multipart("/review")
                                .file(imageFile)
                                .file(reviewPart)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                // then
                .andExpect(status().isCreated())
                .andDo(
                        document(
                                "success-save-review",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParts(
                                        partWithName("review").description("리뷰 요청 데이터"),
                                        partWithName("image").description("리뷰 이미지").optional()
                                ),
                                requestPartFields("review",
                                        fieldWithPath("content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                        fieldWithPath("starRate").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("orderID").type(JsonFieldType.NUMBER).description("주문 ID")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 작성 - 실패 (accessToken 에러)")
    public void fail_review_save_cannot_find_user() throws Exception {

        // given
        // Request 설정
        ReviewRequest rq = new ReviewRequest();
        rq.setContent("test content");
        rq.setStarRate(4.5F);
        rq.setOrderID(1L);

        String jsonRq = objectMapper.writeValueAsString(rq);

        // 'image' 파트
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        // 'review' 파트
        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", jsonRq.getBytes());

        // NoSuchException 발생
        when(reviewService.save(any(ReviewRequest.class), any(MultipartFile.class), anyString()))
                .thenThrow(new NoUserPresentsException());

        // when & then
        mockMvc.perform(
                        multipart("/review")
                                .file(reviewPart)
                                .file(imageFile)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isUnauthorized())
                .andDo(
                        document(
                                "fail-save-review-cannot-find-user",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParts(
                                        partWithName("review").description("리뷰 요청 데이터"),
                                        partWithName("image").description("리뷰 이미지").optional()
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 작성 - 실패 (주문내역 찾지 못함)")
    public void fail_review_save_cannot_find_order() throws Exception {

        // given
        // Request 설정
        ReviewRequest rq = new ReviewRequest();
        rq.setContent("test content");
        rq.setStarRate(4.5F);
        rq.setOrderID(1L);

        String jsonRq = objectMapper.writeValueAsString(rq);

        // 'image' 파트
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        // 'review' 파트
        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", jsonRq.getBytes());

        // NoSuchException 발생
        when(reviewService.save(any(ReviewRequest.class), any(MultipartFile.class), anyString()))
                .thenThrow(new NoSuchException("해당 주문을 찾을 수 없습니다."));

        // when & then
        mockMvc.perform(
                        multipart("/review")
                                .file(reviewPart)
                                .file(imageFile)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        document(
                                "fail-save-review-cannot-find-order",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParts(
                                        partWithName("review").description("리뷰 요청 데이터"),
                                        partWithName("image").description("리뷰 이미지").optional()
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 작성 - 실패 (accessToken 에러)")
    public void fail_review_save_cannot_find_store() throws Exception {

        // given
        // Request 설정
        ReviewRequest rq = new ReviewRequest();
        rq.setContent("test content");
        rq.setStarRate(4.5F);
        rq.setOrderID(1L);

        String jsonRq = objectMapper.writeValueAsString(rq);

        // 'image' 파트
        MockMultipartFile imageFile = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test image content".getBytes());

        // 'review' 파트
        MockMultipartFile reviewPart = new MockMultipartFile("review", "", "application/json", jsonRq.getBytes());

        // NoSuchException 발생
        when(reviewService.save(any(ReviewRequest.class), any(MultipartFile.class), anyString()))
                .thenThrow(new StoreException(NO_MATCH_STORE));

        // when & then
        mockMvc.perform(
                        multipart("/review")
                                .file(reviewPart)
                                .file(imageFile)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isBadRequest())
                .andDo(
                        document(
                                "fail-save-review-cannot-find-store",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParts(
                                        partWithName("review").description("리뷰 요청 데이터"),
                                        partWithName("image").description("리뷰 이미지").optional()
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 읽어오기 - 성공")
    public void success_review_findAll() throws Exception {

        // given
        // Response 설정
        ReviewListResponse rs = new ReviewListResponse();
        rs.setReviewID(1L);
        rs.setNickname("test nickname");
        rs.setStarRate(3.5F);
        rs.setContent("test content");
        rs.setImageURL("test url");
        rs.setMenu("test menu");
        rs.setComment("");
        rs.setCreatedAt("2021-05-01 08:00:00");

        ReviewListResponse rs2 = new ReviewListResponse();
        rs2.setReviewID(2L);
        rs2.setNickname("test nickname2");
        rs2.setStarRate(4.0F);
        rs2.setContent("test content2");
        rs2.setImageURL("");
        rs2.setMenu("test menu2, test menu2-1, test menu2-2");
        rs2.setComment("test comment2");
        rs2.setCreatedAt("2021-05-02 22:00:00");

        ReviewListResponse rs3 = new ReviewListResponse();
        rs3.setReviewID(3L);
        rs3.setNickname("test nickname3");
        rs3.setStarRate(3.0F);
        rs3.setContent("test content3");
        rs3.setImageURL("test url3");
        rs3.setMenu("test menu3, test menu3-1");
        rs3.setComment("");
        rs3.setCreatedAt("2021-05-03 10:00:00");

        given(reviewService.findAll(0, accessToken)).willReturn(List.of(rs, rs2, rs3));

        // when & then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/review")
                                .header("accessToken", accessToken)
                                .param("page", "0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("[0].menu").value("test menu"))
                .andDo(
                        document(
                                "success-findAll-review",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호")
                                ),
                                responseFields(
                                        fieldWithPath("[].reviewID").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                        fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("닉네"),
                                        fieldWithPath("[].starRate").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("리뷰"),
                                        fieldWithPath("[].imageURL").type(JsonFieldType.STRING).description("이미지 URL"),
                                        fieldWithPath("[].menu").type(JsonFieldType.STRING).description("메뉴명"),
                                        fieldWithPath("[].comment").type(JsonFieldType.STRING).description("사장님 댓글"),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("리뷰 작성 날짜")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 삭제 - 성공")
    public void success_review_delete() throws Exception {

        // given
        Long reviewID = 1L;
        when(reviewService.delete(reviewID, accessToken)).thenReturn(reviewID);

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/review/{reviewID}", reviewID)
                                .header("accessToken", accessToken)
                )
                // then
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "success-delete-review",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("삭제하고자 하는 리뷰 ID")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (accessToken 에러)")
    public void fail_review_delete_cannot_find_user() throws Exception {

        // given
        Long reviewID = 1L;
        when(reviewService.delete(reviewID, accessToken))
                .thenThrow(new NoUserPresentsException());

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/review/{reviewID}", reviewID)
                                .header("accessToken", accessToken)
                )
                // then
                .andExpect(status().isUnauthorized())
                .andDo(
                        document(
                                "fail-delete-review-cannot-find-user",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("삭제하고자 하는 리뷰 ID")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (accessToken 에러)")
    public void fail_review_delete_cannot_find_review() throws Exception {

        // given
        Long reviewID = 1L;
        when(reviewService.delete(reviewID, accessToken))
                .thenThrow(new NoSuchException("해당 리뷰를 찾을 수 없습니다."));

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/review/{reviewID}", reviewID)
                                .header("accessToken", accessToken)
                )
                // then
                .andExpect(status().isNotFound())
                .andDo(
                        document(
                                "fail-delete-review-cannot-find-review",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("삭제하고자 하는 리뷰 ID")
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 삭제 - 실패 (accessToken 에러)")
    public void fail_review_delete_cannot_find_store() throws Exception {

        // given
        Long reviewID = 1L;
        when(reviewService.delete(reviewID, accessToken))
                .thenThrow(new StoreException(NO_MATCH_STORE));

        // when
        mockMvc.perform(
                        RestDocumentationRequestBuilders.delete("/review/{reviewID}", reviewID)
                                .header("accessToken", accessToken)
                )
                // then
                .andExpect(status().isBadRequest())
                .andDo(
                        document(
                                "fail-delete-review-cannot-find-store",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("삭제하고자 하는 리뷰 ID")
                                ),
                                responseBody()
                        )
                );
    }
}