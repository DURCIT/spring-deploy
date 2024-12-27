package org.durcit.be.system.exception.auth;

public class EmailNotVerifiedException extends RuntimeException {
    public EmailNotVerifiedException() {
      super();
    }

    public EmailNotVerifiedException(String message) {
      super(message);
    }

    public EmailNotVerifiedException(String message, Throwable cause) {
      super(message, cause);
    }

    public EmailNotVerifiedException(Throwable cause) {
      super(cause);
    }
}
