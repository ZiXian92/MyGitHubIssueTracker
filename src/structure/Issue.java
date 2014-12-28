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
	private static final String FIELD_COMMENTS = "Comments: ";

	//For JSON parsing
	private static final String KEY_ISSUENUMBER = "number";
	private static final String KEY_ISSUETITLE = "title";
	private static final String KEY_STATUS = "state";
	private static final String KEY_CONTENT = "body";
	private static final String KEY_ASSIGNEE = "assignee";
	private static final String KEY_USERNAME = "login";
	private static final String KEY_LABELS = "labels";
	private static final String KEY_LABELNAME = "name";
	private static final String KEY_COMMENTOR = "user";
	private static final String KEY_ID = "id";
	
	//Data members
	private String title, status, content, assignee;
	private int number;
	private ArrayList<String> labels, applicableLabels;
	private ArrayList<Comment> comments;
	
	/**
	 * Defines each Issue's comment.
	 * @author ZiXian92
	 */
	class Comment{
		//Data members
		private int id;
		private String author, message;
		
		/**
		 * Creates a new comment instance for this issue.
		 * @param author The login name of this comment's author. Cannot be null or empty string.
		 * @param message The content of the comment. Can be empty but not null.
		 * @param id This comment's ID.
		 */
		public Comment(String author, String message, int id){
			assert author!=null && !author.isEmpty() && message!=null;
			this.author = author;
			this.message = message;
			this.id = id;
		}
		
		/**
		 * Gets the author of this comment.
		 * @return The login name of this comment's author.
		 */
		public String getAuthor(){
			return author;
		}
		
		/**
		 * Gets the content of this comment.
		 * @return The contents of this comment.
		 */
		public String getContent(){
			return message;
		}
		
		/**
		 * Gets this comment's ID.
		 * @return This comment's ID.
		 */
		public int getId(){
			return id;
		}
		
		/**
		 * Sets this comment's author.
		 * @param author The login name of this comment's author. Cannot be null or empty string.
		 */
		public void setAuthor(String author){
			assert author!=null && !author.isEmpty();
			this.author = author;
		}
		
		/**
		 * Sets the content of this comment.
		 * @param content The content of this comment. Cannot be null.
		 */
		public void setContent(String content){
			assert content!=null;
			this.message = content;
		}
		
		/**
		 * Sets this comment's ID.
		 * @param id This comment's ID.
		 */
		public void setId(int id){
			this.id = id;
		}
		
		@Override
		public String toString(){
			StringBuilder strBuilder = new StringBuilder(author).append(LINE_DELIM);
			strBuilder.append(message);
			return strBuilder.toString();
		}
	}
	
	/**
	 * Creates a new issue instance.
	 * @param title The title of the issue.
	 * @param number This issue's number.
	 */
	public Issue(String title, int number){
		assert title!=null && !title.isEmpty();
		this.title = title;
		this.number = number;
		this.status = STATE_OPEN;
		this.applicableLabels = new ArrayList<String>();
		this.labels = new ArrayList<String>();
		this.comments = new ArrayList<Comment>();
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
		this.labels = new ArrayList<String>();
		this.applicableLabels = new ArrayList<String>();
		String[] arr = issue.getLabels();
		if(arr!=null){
			for(String str: arr){
				labels.add(str);
			}
		}
		arr = issue.getApplicableLabels();
		if(arr!=null){
			for(String str: arr){
				applicableLabels.add(str);
			}
		}
	}
	
	/**
	 * Creates an Issue instance from the given JSON object.
	 * @param obj The JSON object to be converted to an issue.
	 * @return The issue represented by the given JSON object.
	 * 			Returns null if obj is null or an error occurred while parsing the JSON object.
	 */
	public static Issue makeInstance(JSONObject obj){
		assert obj!=null;
		try{
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
		} catch(JSONException e){
			return null;
		}
	}
	
	/**
	 * Gets the title of this issue.
	 * @return The title of this issue.
	 */
	public String getTitle(){
		return title;
	}
	
	/**
	 * Gets this issue's number.
	 * @return This issue's number.
	 */
	public int getNumber(){
		return number;
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
	 * Gets the list of labels assigned to this issue.
	 * @return An array of label names or null if no label is assigned to this issue.
	 */
	public String[] getLabels(){
		int size = labels.size();
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
	 * Gets the list of comments for this issue.
	 * @return AN array of comments for this issue or null if there is none.
	 */
	public Comment[] getComments(){
		if(comments.isEmpty()){
			return null;
		}
		return comments.toArray(new Comment[comments.size()]);
	}
	
	/**
	 * Gets the comment indexed at the given 1-based index.
	 * @param index The 1-based index of the comment in this issue's comment list.
	 * @return The index-th comment in this issue, or null if the given index is invalid.
	 */
	public Comment getComment(int index){
		if(index<1 || index>comments.size()){
			return null;
		}
		return comments.get(index-1);
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
	 * Sets the content of this issue.
	 * @param content The content of this issue.
	 */
	public void setContent(String content){
		if(content==null){
			content = "";
		}
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
	
	/**
	 * Adds the given comment. Does nothing if the given JSON object is not formatted correctly.
	 * @param jsonComment The JSON representation of the comment as provided by GitHub API.
	 * @throws JSONException If jsonComment does not contain required keys or values.
	 */
	public void addComment(JSONObject jsonComment) throws JSONException{
		assert jsonComment!=null;
		String author = jsonComment.getJSONObject(KEY_COMMENTOR).getString(KEY_USERNAME);
		String message = jsonComment.getString(KEY_CONTENT);
		int id = jsonComment.getInt(KEY_ID);
		comments.add(new Comment(author, message, id));
	}
	
	/**
	 * Adds the given comments. Does nothing if the JSON array is not formatted properly.
	 * @param jsonComments The JSON array representation of the comments to add as provided by GitHub API.
	 * @throws JSONException If an error occurs while parsing jsonComments.
	 */
	public void addComments(JSONArray jsonComments) throws JSONException{
		assert jsonComments!=null;
		int numComments = jsonComments.length();
		for(int i=0; i<numComments; i++){
			addComment(jsonComments.getJSONObject(i));
		}
	}
	
	/**
	 * Closes this issue.
	 */
	public void close(){
		status = STATE_CLOSED;
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder =  new StringBuilder(FIELD_TITLE);
		strBuilder = strBuilder.append(title).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_NUMBER).append(number).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_STATUS).append(status).append(SEPARATOR);
		strBuilder = strBuilder.append(FIELD_ASSIGNEE).append(assignee).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_LABELS).append(Util.convertToString(labels)).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_CONTENT).append(content).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_COMMENTS).append(LINE_DELIM);
		int numComments = comments.size();
		for(int i=0; i<numComments; i++){
			strBuilder = strBuilder.append(comments.get(i)).append(LINE_DELIM);
		}
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
	 * @return The JSON object representation of this issue.
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
