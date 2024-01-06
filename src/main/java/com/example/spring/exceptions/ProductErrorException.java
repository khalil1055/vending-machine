package com.example.spring.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProductErrorException extends IllegalStateException {
    Logger logger = LoggerFactory.getLogger(ProductErrorException.class);

    public ProductErrorException(String message) {
        super(message);
        logger.error(message);
    }
}
