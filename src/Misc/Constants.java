package Misc;

/**
 * Defines the class that holds the constants used by multiple classes.
 * @author ZiXian92
 */
public class Constants {
	//Http responses
	public static final String RESPONSE_CREATED = "HTTP/1.1 201 Created";
	public static final String RESPONSE_OK = "HTTP/1.1 200 OK";
	
	//JSON keys
	public static final String KEY_ASSIGNEE = "assignee";
	public static final String KEY_COMMENTS = "comments";
	public static final String KEY_CONTENT = "body";
	public static final String KEY_HASISSUES = "has_issues";
	public static final String KEY_ID = "id";
	public static final String KEY_ISSUENUMBER = "number";
	public static final String KEY_ISSUETITLE = "title";
	public static final String KEY_LABELNAME = "name";
	public static final String KEY_LABELS = "labels";
	public static final String KEY_OWNER = "owner";
	public static final String KEY_REPONAME = "name";
	public static final String KEY_STATUS = "state";
	public static final String KEY_USER = "user";
	public static final String KEY_USERLOGIN = "login";
	
	//Messages
	public static final String MSG_LOGGEDIN = "Logged in as %1$s.\nLoading data from GitHub...";
	
	//Error messages
	public static final String ERROR_ADDISSUE = "Error creating issue.";
	public static final String ERROR_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
	public static final String ERROR_FAILEDREQUEST = "Request failed.";
	public static final String ERROR_INITIALIZEDATA  = "Error updating local data. Restart to try again.";
	public static final String ERROR_INPUTPARSING = "Error parsing input. Please try again.";
	public static final String ERROR_ISSUENOTFOUND = "Issue not found.";
	public static final String ERROR_MISSINGMESSAGE = "Message missing in response. Unable to update local data.";
	public static final String ERROR_PARSEINPUTTOJSON = "Error converting input to request.";
	public static final String ERROR_REPOERROR = "Repository not found or error updating repository.";
	public static final String ERROR_REPONOTFOUND = "Repository not found.";
	public static final String ERROR_SENDINGREQUEST = "Error sending request.";
	public static final String ERROR_UPDATEISSUE = "An error occurred while updating the issue.";
	public static final String ERROR_UPDATELOCALDATA = "Error updating data locally.";
	public static final String ERROR_UPDATEREPO = "An error occurred while updating the repository.";
	
	
}
