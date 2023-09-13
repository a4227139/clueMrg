package com.wa.cluemrg.exception;

import com.wa.cluemrg.response.ResponseResult;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


@Order(value = -1)
@ControllerAdvice
public class BusinessExceptionHandler {

    @ResponseBody
    @ExceptionHandler(BusinessException.class)
    public ResponseResult exceptionHandler(Exception e) {
        ResponseResult response = new ResponseResult();
        BusinessException businessException = (BusinessException) e;
        response.setFail(businessException.getCode());
        response.setMessage(businessException.getMessage());
        return response;

    }


}
