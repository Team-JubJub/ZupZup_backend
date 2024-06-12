package com.rest.api.review.exception;

import com.zupzup.untact.exception.BaseException;
import com.zupzup.untact.exception.BaseExceptionType;

public class ReviewException extends BaseException {

    private BaseExceptionType exType;

    public ReviewException(BaseExceptionType exType) {
        this.exType = exType;
    }

    @Override
    public BaseExceptionType getExceptionType() {
        return exType;
    }
}
