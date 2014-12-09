package structure;

/**
 * Defines the data structure to represent an issue on GitHub.
 * @author ZiXian92
 */
public class Issue {
	public static final String STATE_OPEN = "open";
	public static final String STATE_CLOSED = "closed";
	
	private static final String LINE_DELIM = "\n";
	private static final String SEPARATOR = "\t";
	
	private static final String KEY_TITLE = "Title: ";
	private static final String KEY_CONTENT = "Body: ";
	private static final String KEY_STATUS = "Status: ";
	private static final String KEY_ASSIGNEE = "Assignee: ";
	
	//Data members
	private String title, status, content, assignee;
	
	/**
	 * Creates a new issue instance.
	 * @param title The title of the issue.
	 */
	public Issue(String title){
		assert title!=null && !title.isEmpty();
		this.title = title;
	}
	
	/**
	 * Gets the title of this issue.
	 * @return The title of this issue.
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Gets the status of this issue.
	 * @return This issue's status.
	 */
	public String getStatus(){
		return status;
	}
	
	/**
	 * Gets the assignee of this issue.
	 * @return The assignee of this issue or null if no one is assigned to this issue.
	 */
	public String getAssignee(){
		return assignee;
	}
	
	/**
	 * Sets the title for this issue.
	 * @param title The new title for this issue. Cannot be null or empty string.
	 */
	public void setTitle(String title){
		if(title!=null && !title.isEmpty()){
			this.title = title;
		}
	}
	
	/**
	 * Sets the status of this issue.
	 * @param status The new status for this issue. Should only be either Issue.STATE_OPEN or
	 * 			Issue.STATE_CLOSED.
	 */
	public void setStatus(String status){
		if(status!=null && (status.equals(STATE_OPEN) || status.equals(STATE_CLOSED))){
			this.status = status;
		}
	}
	
	/**
	 * Sets the content of this issue.
	 * @param content The content of this issue.
	 */
	public void setContent(String content){
		this.content = content;
	}
	
	/**
	 * Sets the assignee for this issue.
	 * Pre-condition: The validity of the assignee must be checked with the Repository this Issue
	 * 					is under before calling this method.
	 * @param assignee The assignee of this issue.
	 */
	public void setAssignee(String assignee){
		this.assignee = assignee;
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder =  new StringBuilder(KEY_TITLE);
		strBuilder = strBuilder.append(title).append(LINE_DELIM);
		strBuilder = strBuilder.append(KEY_STATUS).append(status).append(SEPARATOR);
		strBuilder = strBuilder.append(KEY_ASSIGNEE).append(assignee).append(LINE_DELIM);
		strBuilder = strBuilder.append(KEY_CONTENT).append(content);
		return strBuilder.toString();
	}
	
	/**
	 * Gets the summarized form of this issue.
	 * @return A String summarizing this issue.
	 */
	public String getCondensedString(){
		StringBuilder strBuilder = new StringBuilder(status);
		strBuilder = strBuilder.append(SEPARATOR);
		strBuilder = strBuilder.append(title).append(SEPARATOR);
		strBuilder = strBuilder.append(assignee);
		return strBuilder.toString();
	}
}
