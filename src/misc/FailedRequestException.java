package misc;

/**
 * Defines the exception thrown when HTTP request to GitHub API fails.
 * @author ZiXian92
 */
public class FailedRequestException extends Exception {
	private static final long serialVersionUID = -3825977909398565551L;

	public FailedRequestException(){
		super(Constants.ERROR_FAILEDREQUEST);
	}
}
