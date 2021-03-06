package org.ringingmaster.engine.compiler;

/**
 * TODO Comments
 *
 * @author Steve Lake
 */
public class TerminateEarlyException extends RuntimeException {

	public TerminateEarlyException() {
		super();
	}

	public TerminateEarlyException(String message) {
		super(message);
	}

	public TerminateEarlyException(String message, Throwable cause) {
		super(message, cause);
	}

	public TerminateEarlyException(Throwable cause) {
		super(cause);
	}

	protected TerminateEarlyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
