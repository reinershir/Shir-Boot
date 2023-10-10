package io.github.reinershir.boot.core.easygenerator.annotation;

public class EasyAutoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public EasyAutoException() {
		super();
	}

	public EasyAutoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EasyAutoException(String message, Throwable cause) {
		super(message, cause);
	}

	public EasyAutoException(String message) {
		super(message);
	}

	public EasyAutoException(Throwable cause) {
		super(cause);
	}

	
}
