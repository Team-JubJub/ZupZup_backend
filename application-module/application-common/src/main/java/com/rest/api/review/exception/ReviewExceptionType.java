package com.rest.api.review.exception;

import com.zupzup.untact.exception.BaseExceptionType;
import org.springframework.http.HttpStatus;

public enum ReviewExceptionType implements BaseExceptionType {

    NO_MATCH_REVIEW(HttpStatus.NOT_FOUND, "해당 리뷰가 존재하지 않습니다.");

    //    private int errorCode;
    private HttpStatus httpStatus;
    private String errorMessage;

    ReviewExceptionType(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    @Override
    public String getErrMsg() {
        return this.errorMessage;
    }
}
