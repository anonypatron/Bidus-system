package com.common.exception.auction;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class AuctionStatusException extends BusinessException {
    public AuctionStatusException(ErrorCode errorCode) {
        super(errorCode);
    }
}
