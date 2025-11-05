package com.common.exception.bookmark;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class BookmarkAlreadyExistsException extends BusinessException {

    public BookmarkAlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

}
