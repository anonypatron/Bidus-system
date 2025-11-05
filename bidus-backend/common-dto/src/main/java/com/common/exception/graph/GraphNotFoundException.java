package com.common.exception.graph;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;

public class GraphNotFoundException extends BusinessException {

    public GraphNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

}
