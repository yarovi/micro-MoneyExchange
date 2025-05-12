package org.inamsay.net.micromoneyexchange.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthenticationFailedException
        extends RuntimeException {

    Logger logger = LoggerFactory.getLogger(AuthenticationFailedException.class);
    public AuthenticationFailedException(String message) {
        super(message);
        logger.info(message);
    }

    public AuthenticationFailedException(String message, Throwable cause) {
        super(message, cause);
        logger.info(message, cause);
    }
}
