package com.common.exception.value;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class InvalidRequestException extends BusinessException {
    public InvalidRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
