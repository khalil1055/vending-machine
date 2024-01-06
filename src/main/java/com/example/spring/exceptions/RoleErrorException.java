package com.example.spring.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleErrorException extends IllegalStateException {
    Logger logger = LoggerFactory.getLogger(RoleErrorException.class);

    public RoleErrorException(String message) {
        super(message);
        logger.error(message);
    }
}
