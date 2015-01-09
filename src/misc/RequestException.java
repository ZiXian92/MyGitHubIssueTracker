package misc;

/**
 * Defines the exception to be thrown when an error causing the request to not execute occurs.
 * @author ZiXian92
 */
public class RequestException extends Exception {
	private static final long serialVersionUID = 1676711180840167223L;
	
	public RequestException(){
		super(Constants.ERROR_SENDINGREQUEST);
	}
}
