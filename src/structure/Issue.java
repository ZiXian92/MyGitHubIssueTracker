package structure;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines the data structure to represent an issue on GitHub.
 * @author ZiXian92
 */
public class Issue {
	public static final String STATE_OPEN = "open";
	public static final String STATE_CLOSED = "closed";
	
	private static final String LINE_DELIM = "\n";
	private static final String SEPARATOR = "\t";
	
	private static final String FIELD_TITLE = "Title: ";
	private static final String FIELD_CONTENT = "Body: ";
	private static final String FIELD_STATUS = "Status: ";
	private static final String FIELD_ASSIGNEE = "Assignee: ";
	private static final String FIELD_NUMBER = "Number: ";
	
	//For JSON parsing
	private static final String KEY_ISSUENUMBER = "number";
	private static final String KEY_ISSUETITLE = "title";
	private static final String KEY_STATUS = "state";
	private static final String KEY_CONTENT = "body";
	private static final String KEY_ASSIGNEE = "assignee";
	private static final String KEY_USERNAME = "login";
	
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
	 * Creates a copy of the given issue.
	 * @param issue The issue to be copied. Cannot be null.
	 */
	public Issue(Issue issue){
		assert issue!=null;
		this.title = issue.getTitle();
		this.number = issue.getNumber();
		this.status = issue.getStatus();
		this.content = issue.getContent();
		this.assignee = issue.getAssignee();
	}
	
	/**
	 * Creates an Issue instance from the given JSON object.
	 * @param obj The JSON object to be converted to an issue.
	 * @throws JSONException If the JSON format is wrong.
	 */
	public static Issue makeInstance(JSONObject obj) throws JSONException{
		assert obj!=null;
		Issue issue = new Issue(obj.getString(KEY_ISSUETITLE), obj.getInt(KEY_ISSUENUMBER));
		issue.setContent(obj.getString(KEY_CONTENT));
		if(obj.isNull(KEY_ASSIGNEE)){
			issue.setAssignee(null);
		} else{
			issue.setAssignee(obj.getJSONObject(KEY_ASSIGNEE).getString(KEY_USERNAME));
		}
		return issue;
	}
	
	/**
	 * Gets the title of this issue.
	 * @return The title of this issue.
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Gets the body content of this issue.
	 * @return The body content of this issue.
	 */
	public String getContent(){
		return content;
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
		if(assignee!=null){
			assert !assignee.isEmpty();
		}
		this.assignee = assignee;
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder =  new StringBuilder(FIELD_TITLE);
		strBuilder = strBuilder.append(title).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_NUMBER).append(number).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_STATUS).append(status).append(SEPARATOR);
		strBuilder = strBuilder.append(FIELD_ASSIGNEE).append(assignee).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_CONTENT).append(content).append(LINE_DELIM);;
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
	
	/**
	 * Gets the JSONObject representation of this issue.
	 * @throws JSONException If error during JSON parsing occurs.
	 */
	public JSONObject toJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(KEY_ISSUETITLE, title);
		obj.put(KEY_CONTENT, content);
		obj.put(KEY_STATUS, status);
		if(assignee==null){
			obj.put(KEY_ASSIGNEE, JSONObject.NULL);
		} else{
			obj.put(KEY_ASSIGNEE, assignee);
		}
		return obj;
	}
}
