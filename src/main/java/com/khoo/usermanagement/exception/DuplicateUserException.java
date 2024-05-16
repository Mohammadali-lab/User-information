package com.khoo.usermanagement.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

public class DuplicateUserException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(DuplicateUserException.class);

    public DuplicateUserException(String message) {
        super(message);
        logger.warn("DuplicateUserException: {}, Method name: {}, Time: {}", message, new Exception().getStackTrace()[1].getMethodName(), LocalDateTime.now());
    }
}