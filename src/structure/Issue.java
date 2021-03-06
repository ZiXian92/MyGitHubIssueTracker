package structure;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import misc.Constants;
import misc.Util;

/**
 * Defines the data structure to represent an issue on GitHub.
 * @author ZiXian92
 */
public class Issue {
	//Output formatting
	private static final String LINE_DELIM = "\n";
	private static final String SEPARATOR = "\t";
	private static final String FIELD_TITLE = "Title: ";
	private static final String FIELD_NUMBER = "Number: ";
	private static final String FIELD_MILESTONE = "Milestone: ";
	private static final String FIELD_STATUS = "Status: ";
	private static final String FIELD_ASSIGNEE = "Assignee: ";
	private static final String FIELD_LABELS = "Labels: ";
	private static final String FIELD_CONTENT = "Body: ";
	private static final String FIELD_COMMENTS = "Comments: ";
	
	//Represents absence of value.
	private static final String VAL_NOASSIGNEE = "No assignee";
	private static final String VAL_NOMILESTONE = "No milestone";
	
	//Condensed string formatting
	private static final String CONDENSEDFORM = "%1$-6.6s\t%2$-30.30s\t%3$-12.12s\t%4$s";
	private static final int CONDENSEDTITLELENGTH = 30;
	private static final int CONDENSEDMILESTONELENGTH = 12;

	//Data members
	private String title, status, content, assignee, milestone;
	private int number;
	private ArrayList<String> labels, applicableLabels;
	private ArrayList<Comment> comments;
	private boolean isInitialized;
	private Repository repository;
	
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
	 * @param repo The repository this issue belongs to.
	 */
	public Issue(String title, int number, Repository repo){
		assert title!=null && !title.isEmpty() && repo!=null;
		this.title = title;
		this.number = number;
		this.status = Constants.ISSUE_STATUSOPEN;
		this.applicableLabels = new ArrayList<String>();
		this.labels = new ArrayList<String>();
		this.comments = new ArrayList<Comment>();
		this.isInitialized = false;
		this.repository = repo;
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
		this.repository = issue.getRepository();
		this.milestone = issue.getMilestone();
		this.labels = issue.getLabels();
		this.applicableLabels = issue.getApplicableLabels();
	}
	
	/**
	 * Creates an Issue instance from the given JSON object.
	 * @param obj The JSON object to be converted to an issue.
	 * @param repo The repository this issue belongs to.
	 * @return The issue represented by the given JSON object.
	 * @throws JSONException If an error occurs when parsing JSON data.
	 */
	public static Issue makeInstance(JSONObject obj, Repository repo) throws JSONException{
		assert obj!=null && repo!=null;
		Issue issue = new Issue(obj.getString(Constants.KEY_ISSUETITLE), obj.getInt(Constants.KEY_ISSUENUMBER), repo);
		issue.setContent(obj.getString(Constants.KEY_CONTENT));
		if(obj.isNull(Constants.KEY_ASSIGNEE)){
			issue.setAssignee(null);
		} else{
			issue.setAssignee(obj.getJSONObject(Constants.KEY_ASSIGNEE).getString(Constants.KEY_USERLOGIN));
		}
		JSONArray labelArray = obj.getJSONArray(Constants.KEY_LABELS);
		int numLabels = labelArray.length();
		String label;
		for(int i=0; i<numLabels; i++){
			label = labelArray.getJSONObject(i).getString(Constants.KEY_LABELNAME);
			issue.addLabel(label);
		}
		issue.setStatus(obj.getString(Constants.KEY_STATUS));
		if(!obj.isNull(Constants.KEY_MILESTONE)){
			issue.setMilestone(obj.getJSONObject(Constants.KEY_MILESTONE).getString(Constants.KEY_MILESTONETITLE));
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
	 * @return The list of label names.
	 */
	public ArrayList<String> getLabels(){
		return labels;
	}
	
	/**
	 * Gets the list of labels that can be assigned to this issue.
	 * @return An array of labels that can be assigned to this issue.
	 */
	public ArrayList<String> getApplicableLabels(){
		return applicableLabels;
	}
	
	/**
	 * Checks whether this issue requires fetching of comments from GitHub.
	 * @return True if there exists comments to be fetched and false otherwise.
	 */
	public boolean isInitialized(){
		return isInitialized;
	}
	
	/**
	 * Gets the list of comments for this issue.
	 * @return The list of comments for this issue.
	 */
	public ArrayList<Comment> getComments(){
		return comments;
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
	 * Gets the repository this issue belongs to.
	 * @return The repository this issue belongs to.
	 */
	public Repository getRepository(){
		return repository;
	}
	
	/**
	 * Gets the milestone for this issue.
	 * @return The name of the milestone for this issue.
	 */
	public String getMilestone(){
		return milestone;
	}
	
	/**
	 * Sets the title for this issue.
	 * @param title The new title for this issue. Cannot be null or empty string.
	 */
	public void setTitle(String title){
		assert title!=null && !title.isEmpty();
		this.title = title;
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
		assert applicableLabels!=null;
		this.applicableLabels = applicableLabels;
	}
	
	/**
	 * Adds the given label to this issue only if the label is applicable to this issue.
	 * @param label The name of the label to be added to this issue. Cannot be null or empty string
	 */
	public void addLabel(String label){
		assert label!=null && !label.isEmpty();
		labels.add(label);
	}
	
	/**
	 * Adds the given comment to this issue.
	 * @param comment The comment to add to this issue.
	 */
	public void addComment(Comment comment){
		assert comment!=null;
		comments.add(comment);
	}
	
	/**
	 * Adds the given comment. Does nothing if the given JSON object is not formatted correctly.
	 * @param jsonComment The JSON representation of the comment as provided by GitHub API.
	 * @throws JSONException If jsonComment does not contain required keys or values.
	 */
	public void addComment(JSONObject jsonComment) throws JSONException{
		assert jsonComment!=null;
		String author = jsonComment.getJSONObject(Constants.KEY_USER).getString(Constants.KEY_USERLOGIN);
		String message = jsonComment.getString(Constants.KEY_CONTENT);
		int id = jsonComment.getInt(Constants.KEY_ID);
		comments.add(new Comment(author, message, id));
	}
	
	/**
	 * Sets the comments for this issue..
	 * @param jsonComments The JSON array representation of the comments for this issue as provided by GitHub API.
	 * @throws JSONException If an error occurs while parsing jsonComments.
	 */
	public void setComments(JSONArray jsonComments) throws JSONException{
		assert jsonComments!=null;
		int numComments = jsonComments.length();
		ArrayList<Comment> temp = new ArrayList<Comment>();
		String author, message;
		int id;
		JSONObject obj;
		for(int i=0; i<numComments; i++){
			obj = jsonComments.getJSONObject(i);
			author = obj.getJSONObject(Constants.KEY_USER).getString(Constants.KEY_USERLOGIN);
			message = obj.getString(Constants.KEY_CONTENT);
			id = obj.getInt(Constants.KEY_ID);
			temp.add(new Comment(author, message, id));
		}
		comments = temp;
	}
	
	/**
	 * Sets the comments for this issue.
	 * @param comments The comments for this issue. Cannot be null.
	 */
	public void setComments(ArrayList<Comment> comments){
		assert comments!=null;
		this.comments = comments;
	}
	
	/**
	 * Sets the status of this issue.
	 * @param status The status of this issue. Should only be Constants.ISSUE_STATUSOPEN or Constants.ISSUE_STATUSCLOSED.
	 */
	public void setStatus(String status){
		assert status!=null && (status.equals(Constants.ISSUE_STATUSOPEN) || status.equals(Constants.ISSUE_STATUSCLOSED));
		this.status = status;
	}
	
	/**
	 * Marks this issue as initialized or uninitialized.
	 * @param isInitialized True if data should not be fetched by Model for this issue and false otherwise.
	 */
	public void setIsInitialized(boolean isInitialized){
		this.isInitialized = isInitialized;
	}
	
	/**
	 * Sets the repository this issue belongs to.
	 * @param repo The repository that this issue belongs to.
	 */
	public void setRepository(Repository repo){
		this.repository = repo;
	}
	
	/**
	 * Sets the milestone for this issue.
	 * @param milestone The name of the milestone for this issue. Can be null but not an empty string.
	 */
	public void setMilestone(String milestone){
		if(milestone!=null){
			assert !milestone.isEmpty();
		}
		this.milestone = milestone;
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder =  new StringBuilder(FIELD_TITLE);
		strBuilder = strBuilder.append(title).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_NUMBER).append(number).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_MILESTONE).append(milestone==null? VAL_NOMILESTONE: milestone).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_STATUS).append(status).append(SEPARATOR);
		strBuilder = strBuilder.append(FIELD_ASSIGNEE).append(assignee==null? VAL_NOASSIGNEE: assignee).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_LABELS).append(Util.convertToString(labels)).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_CONTENT).append(content).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_COMMENTS).append(LINE_DELIM);
		int numComments = comments.size();
		for(int i=0; i<numComments; i++){
			strBuilder = strBuilder.append(LINE_DELIM).append(i+1).append(". ").append(comments.get(i)).append(LINE_DELIM);
		}
		return strBuilder.toString();
	}
	
	/**
	 * Gets the summarized form of this issue.
	 * @return A String summarizing this issue.
	 */
	public String getCondensedString(){
		String title = this.title;
		if(title.length()>CONDENSEDTITLELENGTH){
			title = title.substring(0, CONDENSEDTITLELENGTH-3)+"...";
		}
		String milestone = this.milestone==null? VAL_NOMILESTONE: this.milestone;
		if(milestone.length()>CONDENSEDMILESTONELENGTH){
			milestone = milestone.substring(0, CONDENSEDMILESTONELENGTH-3)+"...";
		}
		String assignee = this.assignee==null? VAL_NOASSIGNEE: this.assignee;
		return String.format(CONDENSEDFORM, status, title, milestone, assignee);
	}
	
	/**
	 * Gets the JSONObject representation of this issue.
	 * @return The JSON object representation of this issue.
	 * @throws JSONException If error during JSON parsing occurs.
	 */
	public JSONObject toJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put(Constants.KEY_ISSUETITLE, title);
		obj.put(Constants.KEY_CONTENT, content);
		obj.put(Constants.KEY_STATUS, status);
		if(assignee==null){
			obj.put(Constants.KEY_ASSIGNEE, JSONObject.NULL);
		} else{
			obj.put(Constants.KEY_ASSIGNEE, assignee);
		}
		obj.put(Constants.KEY_LABELS, labels.isEmpty()? new JSONArray(): new JSONArray(labels.toArray(new String[labels.size()])));
		return obj;
	}
}
