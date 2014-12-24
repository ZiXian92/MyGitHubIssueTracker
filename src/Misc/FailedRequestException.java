package Misc;

public class FailedRequestException extends Exception {
	private static final String MSG_FAILEDREQUEST = "Request failed.";
	
	public FailedRequestException(){
		super(MSG_FAILEDREQUEST);
	}
}
