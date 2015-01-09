package misc;

/**
 * Defines the exception to be thrown when message is missing from the HTTP response from GitHub API.
 * @author ZiXian92
 */
public class MissingMessageException extends Exception {
	private static final long serialVersionUID = 6605290320201553787L;

	public MissingMessageException(){
		super(Constants.ERROR_MISSINGMESSAGE);
	}
}
