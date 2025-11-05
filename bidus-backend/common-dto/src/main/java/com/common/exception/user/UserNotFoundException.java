package com.common.exception.user;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class UserNotFoundException extends BusinessException {

    public UserNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
