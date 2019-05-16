package io.github.ukuz.spring.web.idempotent.autoconfigure.exception;


/**
 * @author ukuz90
 * @date 2019-05-16
 */
public class StoreException extends Exception {

    public StoreException(String message) {
        super(message);
    }

    public StoreException(Throwable cause) {
        super(cause);
    }

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }
}
