package com.common.exception.auction;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class AuctionNotFoundException extends BusinessException {

    public AuctionNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
