package me.yushust.inject.error;

import me.yushust.inject.util.Validate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * An implementation of {@link ErrorAttachable}
 * that implements {@link ErrorAttachable#attach} methods but
 * not {@link ErrorAttachable#reportAttachedErrors()}. The
 * error reports are delegated to the sub-class that extends
 * to this class.
 *
 * <p>Use the method {@link ErrorAttachableImpl#getErrorMessages()}
 * to get an immutable copy of all the attached messages.</p>
 */
public class ErrorAttachableImpl implements ErrorAttachable {

	// We don't need access by index to the
	// collection of error messages, so we
	// can use a LinkedList instead of an
	// ArrayList compound by a resizeable array
	private final List<String> errorMessages =
			new LinkedList<>();

	/** Adds all the {@code messages} to the list of error messages */
	@Override
	public void attach(String... messages) {
		Validate.notNull(messages, "errorMessages");
		Collections.addAll(errorMessages, messages);
	}

	@Override
	public void attach(String header, Throwable error) {
		Validate.notNull(error, "error");
		String stackTrace = Errors.getStackTrace(error);
		if (header != null) {
			stackTrace = header + "\n" + stackTrace;
		}
		errorMessages.add(stackTrace);
	}

	/**
	 * Attaches all the error messages of the specified {@code attachable}
	 * into this ErrorAttachable
	 */
	@Override
	public void attachAll(ErrorAttachable attachable) {
		errorMessages.addAll(attachable.getErrorMessages());
	}

	/**
	 * @return True if the errors has been attached
	 * to this object.
	 */
	@Override
	public boolean hasErrors() {
		return !errorMessages.isEmpty();
	}

	/**
	 * Gets an immutable copy of all the attached
	 * error messages.
	 * @return The attached error messages
	 */
	@Override
	public List<String> getErrorMessages() {
		return new ArrayList<>(errorMessages);
	}

	@Override
	public void applySnapshot(List<String> errorMessages) {
		this.errorMessages.clear();
		this.errorMessages.addAll(errorMessages);
	}

	/** Formats the error messages in this error-attachable */
	@Override
	public String formatMessages() {
		return Errors.formatErrorMessages(errorMessages);
	}

	@Override
	public int errorCount() {
		return errorMessages.size();
	}

	/** By default the errors cannot be reported */
	@Override
	public void reportAttachedErrors() {
		throw new UnsupportedOperationException("The attached errors cannot be reported here!");
	}
}
