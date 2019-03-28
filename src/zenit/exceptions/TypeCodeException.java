package zenit.exceptions;

/**
 * To be thrown when CodeSnippets are being accessed with a non-existing TypeCode.
 * @author Alexander Libot
 *
 */
@SuppressWarnings("serial")
public class TypeCodeException extends Exception {
	
	public TypeCodeException() {
		super();
	}

	public TypeCodeException(String message) {
		super(message);
	}
}
