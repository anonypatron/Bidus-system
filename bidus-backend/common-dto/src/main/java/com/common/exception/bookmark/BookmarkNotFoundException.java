package com.common.exception.bookmark;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class BookmarkNotFoundException extends BusinessException {

    public BookmarkNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
