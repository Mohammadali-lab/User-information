package com.khoo.usermanagement.exception;


import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.LocalDateTime;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
public class UnauthorizedException extends RuntimeException{

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DuplicateUserException.class);

    public UnauthorizedException(String message) {

        super(message);
        logger.error("Exception message: {}, Method name: {}, Time: {}", message, new Exception().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }
}
