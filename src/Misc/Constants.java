package Misc;

/**
 * Defines the class that holds the constants used by multiple classes.
 * @author ZiXian92
 */
public class Constants {
	//Http headers and values
	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_AUTH = "Authorization";
	public static final String VAL_ACCEPT = "application/vnd.github.v3+json";
	public static final String VAL_AUTH = "Basic %1$s";
	public static final String VAL_PREVIEWACCEPT = "application/vnd.github.moondragon-preview+json";
	
	//Http responses
	public static final String RESPONSE_CREATED = "HTTP/1.1 201 Created";
	public static final String RESPONSE_OK = "HTTP/1.1 200 OK";
	
	//JSON keys
	public static final String KEY_ASSIGNEE = "assignee";
	public static final String KEY_COMMENTS = "comments";
	public static final String KEY_CONTENT = "body";
	public static final String KEY_FULLNAME = "full_name";
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
	public static final String ERROR_ADDCOMMENT = "Failed to add comment to issue.";
	public static final String ERROR_ADDISSUE = "Error creating issue.";
	public static final String ERROR_CLOSEISSUE = "Error closing issue.";
	public static final String ERROR_EDITISSUE = "Error editing issue.";
	public static final String ERROR_EMPTYCOMMAND = "Empty command.";
	public static final String ERROR_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
	public static final String ERROR_FAILEDREQUEST = "Request failed.";
	public static final String ERROR_INAPPLICABLEBACKCOMMAND = "No repository is selected. Unable to go further up.";
	public static final String ERROR_INAPPLICABLESELECT = "Select command not allowed when issue is selected.";
	public static final String ERROR_INITIALIZEDATA  = "Error updating local data. Restart to try again.";
	public static final String ERROR_INPUTPARSING = "Error parsing input. Please try again.";
	public static final String ERROR_ISSUENOTFOUND = "Issue/Repository not found.";
	public static final String ERROR_ISSUENOTSELECTED = "Inapplicable action. Please select an issue.";
	public static final String ERROR_MISSINGMESSAGE = "Message missing in response. Unable to update local data.";
	public static final String ERROR_MISSINGTITLE = "No title given.";
	public static final String ERROR_NOPARAMETER = "Invalid command. No parameter passed.";
	public static final String ERROR_PARSEINPUTTOJSON = "Error converting input to request.";
	public static final String ERROR_REPOERROR = "Repository not found or error updating repository.";
	public static final String ERROR_REPONOTFOUND = "Repository not found.";
	public static final String ERROR_REPONOTSELECTED = "Inapplicable action. Please select a repository.";
	public static final String ERROR_SENDINGREQUEST = "Error sending request.";
	public static final String ERROR_UPDATEISSUE = "An error occurred while updating the issue.";
	public static final String ERROR_UPDATELOCALDATA = "Error updating data locally.";
	public static final String ERROR_UPDATEREPO = "An error occurred while updating the repository.";
	
	//Others
	public static final String ISSUE_STATUSCLOSED = "closed";
	public static final String ISSUE_STATUSOPEN = "open";
	public static final String REPO_FULLNAME = "%1$s/%2$s";
	
}
