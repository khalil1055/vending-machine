package com.example.spring.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthException extends Exception {
    Logger logger = LoggerFactory.getLogger(AuthException.class);

    public AuthException(String message) {
        super(message);
        logger.error(message);
    }
}
