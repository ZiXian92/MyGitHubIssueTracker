package Misc;

/**
 * Defines the class that holds the constants used by multiple classes.
 * @author ZiXian92
 */
public class Constants {
	
	//Messages
	public static final String MSG_LOGGEDIN = "Logged in as %1$s.\nLoading data from GitHub...";
	
	//Error messages
	public static final String ERROR_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
	public static final String ERROR_FAILEDREQUEST = "Request failed.";
	public static final String ERROR_INITIALIZEDATA  = "Error updating local data. Restart to try again.";
	public static final String ERROR_ISSUENOTFOUND = "Issue not found.";
	public static final String ERROR_MISSINGMESSAGE = "Message missing in response. Unable to update local data.";
	public static final String ERROR_PARSEINPUTTOJSON = "Error converting input to request.";
	public static final String ERROR_REPONOTFOUND = "Repository not found.";
	public static final String ERROR_SENDINGREQUEST = "Error sending request.";
	public static final String ERROR_UPDATELOCALCOPY = "Error updating local data. Check GitHub to confirm changes.";
	
}
