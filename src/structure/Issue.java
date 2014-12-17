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
	private static final String KEY_NUMBER = "Number: ";
	
	//Data members
	private String title, status, content, assignee;
	private int number;
	
	/**
	 * Creates a new issue instance.
	 * @param title The title of the issue.
	 */
	public Issue(String title, int number){
		assert title!=null && !title.isEmpty();
		this.title = title;
		this.number = number;
		this.status = STATE_OPEN;
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
	 * Gets this issue's number.
	 * @return This issue's number.
	 */
	public int getNumber(){
		return number;
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
	 * Closes this issue.
	 */
	public void close(){
		status = STATE_CLOSED;
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
		strBuilder = strBuilder.append(KEY_NUMBER).append(number).append(LINE_DELIM);
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
