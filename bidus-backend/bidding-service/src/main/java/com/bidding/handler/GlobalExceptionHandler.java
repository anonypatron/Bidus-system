package com.bidding.handler;

import com.common.error.code.ErrorCode;
import com.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

//    @ExceptionHandler(BusinessException.class)
//    public ResponseEntity<ErrorCode> handleBusinessException(BusinessException e, HttpServletRequest request) {
//        return ResponseEntity.body(e).build();
//    }

}
