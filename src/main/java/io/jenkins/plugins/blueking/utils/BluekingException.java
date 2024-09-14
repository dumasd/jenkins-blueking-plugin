package io.jenkins.plugins.blueking.utils;

public class BluekingException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BluekingException(String message) {
        super(message);
    }

    public BluekingException(Throwable cause) {
        super(cause);
    }

    public BluekingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
