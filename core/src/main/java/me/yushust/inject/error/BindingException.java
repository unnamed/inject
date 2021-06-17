package me.yushust.inject.error;

/**
 * An exception that can be thrown while configuring
 * injector bindings
 */
public class BindingException extends RuntimeException {

	public BindingException() {
		super();
	}

	public BindingException(String message) {
		super(message);
	}

	public BindingException(String message, Throwable cause) {
		super(message, cause);
	}

	public BindingException(Throwable cause) {
		super(cause);
	}

}
