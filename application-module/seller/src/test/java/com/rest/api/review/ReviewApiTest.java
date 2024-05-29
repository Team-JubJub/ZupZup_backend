package com.rest.api.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rest.api.document.RestDocsConfig;
import com.rest.api.review.model.dto.ReviewAnnouncementRequest;
import com.rest.api.review.service.impl.ReviewServiceImpl;
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
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        when(reviewService.updateReviewAnnouncement(storeID, accessToken, rq)).thenReturn(storeID);

        mockMvc.perform(
                        patch("/review/{storeID}", storeID)
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
                                requestFields(
                                        fieldWithPath("reviewAnnouncement").type(JsonFieldType.STRING).description("리뷰 공지사항")
                                ),
                                responseFields()
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
        when(reviewService.updateReviewAnnouncement(storeID, accessToken, rq)).thenReturn(storeID);

        // then
        mockMvc.perform(
                        delete("/review/{storeID}", storeID)
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
                                requestFields(
                                        fieldWithPath("reviewAnnouncement").type(JsonFieldType.STRING).description("리뷰 공지사항")
                                ),
                                responseFields()
                        )
                );
    }
}
