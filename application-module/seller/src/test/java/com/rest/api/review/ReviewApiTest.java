package com.rest.api.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.document.RestDocsConfig;
import com.rest.api.review.exception.ReviewException;
import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.model.dto.ReviewCommentRequest;
import com.rest.api.review.model.dto.ReviewListResponse;
import com.rest.api.review.service.impl.ReviewServiceImpl;
import com.zupzup.untact.exception.store.ForbiddenStoreException;
import com.zupzup.untact.exception.store.seller.NoSuchStoreException;
import io.swagger.v3.core.util.Json;
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
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static com.rest.api.review.exception.ReviewExceptionType.NO_MATCH_REVIEW;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
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
    private ReviewServiceImpl reviewService;

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
    @DisplayName("리뷰 공지 작성 및 수정 - 성공")
    public void success_reviewAnnouncement_save() throws Exception {

        // Request 설정
        ReviewAnnouncementRequest rq = new ReviewAnnouncementRequest();
        rq.setReviewAnnouncement("test review announcement");

        // Response 설정
        Long storeID = 1L;

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.updateReviewAnnouncement(anyLong(), anyString(), any(ReviewAnnouncementRequest.class))).thenReturn(storeID);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .content(jsonRq)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "success-save-reviewAnnouncement",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("리뷰 공지사항 수정하려는 가게 ID")
                                ),
                                requestFields(
                                        fieldWithPath("reviewAnnouncement").type(JsonFieldType.STRING).description("리뷰 공지사항")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 공지 삭제 - 성공")
    public void success_reviewAnnouncement_delete() throws Exception {

        // given
        // Request 설정
        ReviewAnnouncementRequest rq = new ReviewAnnouncementRequest();
        rq.setReviewAnnouncement(null);

        // Response 설정
        Long storeID = 1L;

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.updateReviewAnnouncement(anyLong(), anyString(), any(ReviewAnnouncementRequest.class))).thenReturn(storeID);

        // then
        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .content(jsonRq)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                // then
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "success-delete-reviewAnnouncement",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("리뷰 공지사항 수정하려는 가게 ID")
                                ),
                                requestFields(
                                        fieldWithPath("reviewAnnouncement").type(JsonFieldType.NULL).description("리뷰 공지사항")
                                ),
                                responseBody()
                        )
                );
    }
    @Test
    @DisplayName("리뷰 공지 작성 및 수정 - 실패 (가게 찾지 못함)")
    public void fail_reviewAnnouncement_save_cannot_find_store() throws Exception {

        // Request 설정
        ReviewAnnouncementRequest rq = new ReviewAnnouncementRequest();
        rq.setReviewAnnouncement("test review announcement");

        // Response 설정
        Long storeID = 1L;

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.updateReviewAnnouncement(anyLong(), anyString(), any(ReviewAnnouncementRequest.class)))
                .thenThrow(new NoSuchStoreException("해당 ID의 가게가 존재하지 않습니다."));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .content(jsonRq)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andDo(
                        document(
                                "fail-save-reviewAnnouncement-cannot-find-store",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("리뷰 공지사항 수정하려는 가게 ID")
                                ),
                                requestFields(
                                        fieldWithPath("reviewAnnouncement").type(JsonFieldType.STRING).description("리뷰 공지사항")
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 공지 작성 및 수정 - 실패 (권한 없음)")
    public void fail_reviewAnnouncement_save_wrong_accessToken() throws Exception {

        // Request 설정
        ReviewAnnouncementRequest rq = new ReviewAnnouncementRequest();
        rq.setReviewAnnouncement("test review announcement");

        // Response 설정
        Long storeID = 1L;

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.updateReviewAnnouncement(anyLong(), anyString(), any(ReviewAnnouncementRequest.class)))
                .thenThrow(new ForbiddenStoreException("해당 가게에 대한 권한이 없습니다."));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .content(jsonRq)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        document(
                                "fail-save-reviewAnnouncement-wrong-accessToken",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("리뷰 공지사항 수정하려는 가게 ID")
                                ),
                                requestFields(
                                        fieldWithPath("reviewAnnouncement").type(JsonFieldType.STRING).description("리뷰 공지사항")
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 전체 보기 - 성공")
    public void success_findAll() throws Exception {

        // Request 설정
        Long storeID = 1L;

        // Response 설정
        ReviewListResponse rs = new ReviewListResponse();
        rs.setReviewID(1L);
        rs.setNickname("닉네임1");
        rs.setStarRate(3.5f);
        rs.setContent("리뷰 내용");
        rs.setImageURL("이미지 url");
        rs.setMenu("메뉴 1");
        rs.setComment("답글1");
        rs.setCreatedAt("2022-01-11 01:11:11");

        ReviewListResponse rs2 = new ReviewListResponse();
        rs2.setReviewID(2L);
        rs2.setNickname("닉네임2");
        rs2.setStarRate(4.5f);
        rs2.setContent("리뷰 내용~~");
        rs2.setImageURL("이미지 url");
        rs2.setMenu("메뉴 1, 메뉴 2, 메뉴 3");
        rs2.setComment(null);
        rs2.setCreatedAt("2022-02-22 02:22:22");

        ReviewListResponse rs3 = new ReviewListResponse();
        rs3.setReviewID(3L);
        rs3.setNickname("닉네임3");
        rs3.setStarRate(5.0f);
        rs3.setContent("리뷰 내용!");
        rs3.setImageURL("이미지 url");
        rs3.setMenu("메뉴 1");
        rs3.setComment(null);
        rs3.setCreatedAt("2023-03-13 03:33:33");

        //when
        when(reviewService.findAll(anyInt(), anyLong(), anyString()))
                .thenReturn(List.of(rs3, rs2, rs));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .param("page", "0")
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "success-findAll",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("가게 ID")
                                ),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호")
                                ),
                                responseFields(
                                        fieldWithPath("[].reviewID").type(JsonFieldType.NUMBER).description("리뷰 ID"),
                                        fieldWithPath("[].nickname").type(JsonFieldType.STRING).description("닉네임"),
                                        fieldWithPath("[].starRate").type(JsonFieldType.NUMBER).description("별점"),
                                        fieldWithPath("[].content").type(JsonFieldType.STRING).description("리뷰 내용"),
                                        fieldWithPath("[].imageURL").type(JsonFieldType.STRING).description("이미지 url"),
                                        fieldWithPath("[].menu").type(JsonFieldType.STRING).description("메뉴 내역"),
                                        fieldWithPath("[].comment").type(JsonFieldType.STRING).description("댓글").optional(),
                                        fieldWithPath("[].createdAt").type(JsonFieldType.STRING).description("리뷰 생성 날짜(최신순)")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 전체 보기 - 성공 (빈배열)")
    public void success_findAll_empty() throws Exception {

        // Request 설정
        Long storeID = 1L;

        // Response 설정
        ArrayList emptyList = new ArrayList<>();

        //when
        when(reviewService.findAll(anyInt(), anyLong(), anyString()))
                .thenReturn(emptyList);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .param("page", "0")
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "success-findAll-empty",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("가게 ID")
                                ),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 전체 보기 - 실패")
    public void fail_findAll_empty_wrong_accessToken() throws Exception {

        // Request 설정
        Long storeID = 1L;

        //when
        when(reviewService.findAll(anyInt(), anyLong(), anyString()))
                .thenThrow(new ForbiddenStoreException("해당 가게에 대한 권한이 없습니다."));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/review/{storeID}", storeID)
                                .header("accessToken", accessToken)
                                .param("page", "0")
                )
                .andExpect(status().isForbidden())
                .andDo(
                        document(
                                "fail-findAll-wrong-accessToken",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("storeID").description("가게 ID")
                                ),
                                queryParameters(
                                        parameterWithName("page").description("페이지 번호")
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }

    @Test
    @DisplayName("리뷰 답글 달기 - 성공")
    public void success_write_review_comment() throws Exception {

        // Request 설정
        Long reviewID = 1L;

        ReviewCommentRequest rq = new ReviewCommentRequest();
        rq.setReviewComment("리뷰 답글");

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.writeReviewComment(anyLong(), anyString(), any(ReviewCommentRequest.class)))
                .thenReturn(reviewID);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/comment/{reviewID}", reviewID)
                                .content(jsonRq)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andDo(
                        document(
                                "success-write_review_comment",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("리뷰 ID")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 답글 달기 - 실패 (리뷰 찾지 못함)")
    public void fail_write_review_comment_cannot_find_review() throws Exception {

        // Request 설정
        Long reviewID = 1L;

        ReviewCommentRequest rq = new ReviewCommentRequest();
        rq.setReviewComment("리뷰 답글");

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.writeReviewComment(anyLong(), anyString(), any(ReviewCommentRequest.class)))
                .thenThrow(new ReviewException(NO_MATCH_REVIEW));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/comment/{reviewID}", reviewID)
                                .content(jsonRq)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andDo(
                        document(
                                "fail-write-review-comment-cannot-find-review",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("리뷰 ID")
                                ),
                                responseBody()
                        )
                );
    }

    @Test
    @DisplayName("리뷰 답글 달기 - 실패 (접근 권한 없음)")
    public void fail_write_review_comment_wrong_accessToken() throws Exception {

        // Request 설정
        Long reviewID = 1L;

        ReviewCommentRequest rq = new ReviewCommentRequest();
        rq.setReviewComment("리뷰 답글");

        String jsonRq = objectMapper.writeValueAsString(rq);

        //when
        when(reviewService.writeReviewComment(anyLong(), anyString(), any(ReviewCommentRequest.class)))
                .thenThrow(new ForbiddenStoreException("해당 가게에 대한 권한이 없습니다."));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.patch("/review/comment/{reviewID}", reviewID)
                                .content(jsonRq)
                                .header("accessToken", accessToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isForbidden())
                .andDo(
                        document(
                                "fail-write_review_comment-wrong-accessToken",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("reviewID").description("리뷰 ID")
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메세지")
                                )
                        )
                );
    }
}
