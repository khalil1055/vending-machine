package com.example.spring.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserErrorException extends IllegalStateException {
    Logger logger = LoggerFactory.getLogger(UserErrorException.class);

    public UserErrorException(String message) {
        super(message);
        logger.error(message);
    }
}
