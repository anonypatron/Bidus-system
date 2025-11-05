package com.common.exception.value;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class InvalidInputPriceException extends BusinessException {

    public InvalidInputPriceException(ErrorCode errorCode) {
        super(errorCode);
    }

}
