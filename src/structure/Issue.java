package structure;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Misc.Util;

/**
 * Defines the data structure to represent an issue on GitHub.
 * @author ZiXian92
 */
public class Issue {
	//Allowable states
	public static final String STATE_OPEN = "open";
	public static final String STATE_CLOSED = "closed";
	
	//Output formatting
	private static final String LINE_DELIM = "\n";
	private static final String SEPARATOR = "\t";
	private static final String FIELD_TITLE = "Title: ";
	private static final String FIELD_CONTENT = "Body: ";
	private static final String FIELD_STATUS = "Status: ";
	private static final String FIELD_ASSIGNEE = "Assignee: ";
	private static final String FIELD_NUMBER = "Number: ";
	private static final String FIELD_LABELS = "Labels: ";

	//For JSON parsing
	private static final String KEY_ISSUENUMBER = "number";
	private static final String KEY_ISSUETITLE = "title";
	private static final String KEY_STATUS = "state";
	private static final String KEY_CONTENT = "body";
	private static final String KEY_ASSIGNEE = "assignee";
	private static final String KEY_USERNAME = "login";
	private static final String KEY_LABELS = "labels";
	private static final String KEY_LABELNAME = "name";
	
	//Data members
	private String title, status, content, assignee;
	private int number;
	private ArrayList<String> labels, applicableLabels;
	
	/**
	 * Creates a new issue instance.
	 * @param title The title of the issue.
	 * @param int number This issue's number.
	 */
	public Issue(String title, int number){
		assert title!=null && !title.isEmpty();
		this.title = title;
		this.number = number;
		this.status = STATE_OPEN;
		this.applicableLabels = new ArrayList<String>();
		this.labels = new ArrayList<String>();
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
		JSONArray labelArray = obj.getJSONArray(KEY_LABELS);
		int numLabels = labelArray.length();
		String label;
		for(int i=0; i<numLabels; i++){
			label = labelArray.getJSONObject(i).getString(KEY_LABELNAME);
			issue.addLabel(label);
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
	 * Gets the list of labels assigned to this issue.
	 * @return An array of label names or null if no label is assigned to this issue.
	 */
	public String[] getLabels(){
		int size = labels.size();;
		if(size==0){
			return null;
		}
		String[] arr = labels.toArray(new String[size]);
		return arr;
	}
	
	/**
	 * Gets the list of labels that can be assigned to this issue.
	 * @return An array of labels that can be assigned to this issue or null if there is none.
	 */
	public String[] getApplicableLabels(){
		int numLabels = applicableLabels.size();
		if(numLabels==0){
			return null;
		}
		String[] arr = applicableLabels.toArray(new String[numLabels]);
		return arr;
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
	
	/**
	 * Sets the list of applicable labels for this issue. Replaces the current list of applicable labels.
	 * @param applicableLabels The list of applicable labels for this issue.
	 * */
	public void setApplicableLabels(ArrayList<String> applicableLabels){
		if(applicableLabels!=null){
			this.applicableLabels = applicableLabels;
		}
	}
	
	
	
	/**
	 * Adds the given label to this issue only if the label is applicable to this issue.
	 * @param label The name of the label to be added to this issue.
	 */
	public void addLabel(String label){
		if(label!=null && !label.isEmpty()){
			labels.add(label);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder =  new StringBuilder(FIELD_TITLE);
		strBuilder = strBuilder.append(title).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_NUMBER).append(number).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_STATUS).append(status).append(SEPARATOR);
		strBuilder = strBuilder.append(FIELD_ASSIGNEE).append(assignee).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_LABELS).append(Util.convertToString(labels)).append(LINE_DELIM);
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
		obj.put(KEY_LABELS, labels.isEmpty()? new JSONArray(): new JSONArray(labels.toArray(new String[labels.size()])));
		return obj;
	}
}
