package org.inamsay.net.micromoneyexchange.exception;


/** * Custom exception class for handling exchange rate errors.
 * This class extends RuntimeException and provides constructors
 * to create an instance of the exception with a message and/or a cause.
 */
public class HandlerExchangeRateException extends RuntimeException{
    public HandlerExchangeRateException(String message, Throwable cause) {
        super(message, cause);
    }
}
