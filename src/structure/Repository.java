package structure;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import Misc.Util;

/**
 * Defines the data structure that represents a repository in GitHub.
 * @author ZiXian92
 */
public class Repository {
	//Error messages
	private static final String MSG_INVALIDINDEX = "Invalid index.";
	private static final String MSG_NOSUCHELEMENT = "The issue does not exist in this repository.";
	
	//For use in output formatting
	private static final String LINE_DELIM = "\n";
	private static final String CONTRIBUTOR_SEPARATOR = ", ";
	private static final String FIELD_NAME = "Name: ";
	private static final String FIELD_OWNER = "Owner: ";
	private static final String FIELD_CONTRIBUTORS = "Contributors: ";
	private static final String FIELD_ISSUES = "Issues: ";
	private static final String FIELD_LABELS = "Labels: ";
	
	//For JSON parsing
	private static final String KEY_REPONAME = "name";
	private static final String KEY_OWNER = "owner";
	private static final String KEY_OWNERLOGIN = "login";
	
	//Data members
	private String name, owner;	//To be extracted by Model to update GitHub.
	private ArrayList<Issue> issueList;
	private ArrayList<String> assignees, labels;
	private HashMap<String, Integer> indexList;
	private int numIssues;
	
	/**
	 * Creates a new repository instance.
	 * @param name The name of the repository.
	 */
	public Repository(String name, String owner){
		assert name!=null && !name.isEmpty() && owner!=null && !owner.isEmpty();
		this.name = name;
		this.owner = owner;
		issueList = new ArrayList<Issue>();
		assignees = new ArrayList<String>();
		indexList = new HashMap<String, Integer>();
		labels = new ArrayList<String>();
		numIssues = 0;
	}
	
	/**
	 * Creates a repository instance from the JSON object.
	 * @param obj The JSON object to convert into Repository.
	 * @throws JSONException If the JSON format is not correct.
	 */
	public static Repository makeInstance(JSONObject obj) throws JSONException {
		assert obj!=null;
		return new Repository(obj.getString(KEY_REPONAME), obj.getJSONObject(KEY_OWNER).getString(KEY_OWNERLOGIN));
	}
	
	/**
	 * Gets the name of this repository.
	 * @return The name of this repository.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Gets the owner of this repository.
	 * @return The name of the owner of this repository.
	 */
	public String getOwner(){
		return owner;
	}
	
	/**
	 * Gets the list of contributors to this repository.
	 * @return An array of the contributors' names.
	 */
	public String[] getAssignees(){
		if(assignees.isEmpty()){
			return null;
		}
		String[] arr = new String[assignees.size()];
		for(int i=0; i<assignees.size(); i++){
			arr[i] = assignees.get(i);
		}
		return arr;
	}
	
	/**
	 * Gets the list of labels in this repository.*/
	public ArrayList<String> getLabels(){
		return labels;
	}
	
	/**
	 * Adds the given issue to this repository's issue list.
	 * @param issue the issue to be added.
	 */
	public void addIssue(Issue issue){
		if(issue!=null){
			issueList.add(issue);
			indexList.put(issue.getTitle(), ++numIssues);
			issue.setApplicableLabels(labels);
		}
	}
	
	/**
	 * Adds the given assignee to the list of allowed assignees.
	 * @param assignee The assignee to add. Cannot be null or empty string.
	 */
	public void addAssignee(String assignee){
		if(assignee!=null && !assignee.isEmpty()){
			assignees.add(assignee);
		}
	}
	
	/**
	 * Adds a new label to this repository.
	 * @param label The new label to be added.
	 */
	public void addLabel(String label){
		if(label!=null && !label.isEmpty()){
			labels.add(label);
		}
	}
	
	/**
	 * Gets the index-th issue in this repository.
	 * @param index The index of the issue on this repository.
	 * @return The index-th issue in this repository's issue list.
	 * @throws IllegalArgumentException If index is less than 1 or is greater than the number of
	 * 									issues in this repository.
	 */
	public Issue getIssue(int index) throws IllegalArgumentException {
		if(index<1 || index>numIssues){
			throw new IllegalArgumentException(MSG_INVALIDINDEX);
		}
		return issueList.get(index-1);
	}
	
	/**
	 * Gets the issue with the given name in this repository.
	 * @param issueName The name of the issue to get. Cannot be null or empty.
	 * @return The issue with the given name in this repository.
	 * @throws IllegalArgumentException If no such issue with the given name exists.
	 */
	public Issue getIssue(String issueName) throws IllegalArgumentException {
		assert issueName!=null && !issueName.isEmpty();
		if(!indexList.containsKey(issueName)){
			throw new IllegalArgumentException(MSG_NOSUCHELEMENT);
		}
		int index = indexList.get(issueName);
		return getIssue(index);
	}
	
	/**
	 * Marks the issue with the given index as closed.
	 * @param index The 1-based index of the issue to close in this repository's issue list.
	 * @throws IllegalArgumentException If index is invalid.
	 */
	public void closeIssue(int index) throws IllegalArgumentException {
		if(index<1 || index>issueList.size()){
			throw new IllegalArgumentException(MSG_INVALIDINDEX);
		}
		issueList.get(index-1).close();
	}
	
	/**
	 * Closes the issue with the given name.
	 * @param issueName The name of the issue to close.
	 * @throws IllegalArgumentException If there is no issue with the given name.
	 */
	public void closeIssue(String issueName) throws IllegalArgumentException {
		assert issueName!=null && !issueName.isEmpty();
		if(!indexList.containsKey(issueName)){
			throw new IllegalArgumentException(MSG_NOSUCHELEMENT);
		}
		int index = indexList.get(issueName);
		closeIssue(index);
	}
	
	/**
	 * Replaces the given issue with the given updated issue.
	 * @param issueName The name of the issue to be replaced. Cannot be null or empty string.
	 * @param editedIssue The new issue to replace the target issue. Cannot be null.
	 * @throws IllegalArgumentException If issueName represents a non-existent issue.
	 */
	public void replaceIssue(String issueName, Issue editedIssue) throws IllegalArgumentException {
		assert issueName!=null && !issueName.isEmpty() && editedIssue!=null;
		if(!indexList.containsKey(issueName)){
			throw new IllegalArgumentException(MSG_NOSUCHELEMENT);
		}
		int index = indexList.get(issueName);
		issueList.set(index-1, editedIssue);
		indexList.remove(issueName);
		indexList.put(editedIssue.getTitle(), index);
		editedIssue.setApplicableLabels(labels);
	}
	
	@Override
	public String toString(){
		StringBuilder strBuilder = new StringBuilder(FIELD_NAME);
		strBuilder = strBuilder.append(name).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_OWNER).append(owner).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_CONTRIBUTORS).append(LINE_DELIM);
		int numContributors = assignees.size();
		for(int i=0; i<numContributors; i++){
			strBuilder = strBuilder.append(assignees.get(i));
			if(i!=numContributors-1){
				strBuilder = strBuilder.append(CONTRIBUTOR_SEPARATOR);
			}
		}
		strBuilder = strBuilder.append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_LABELS).append(LINE_DELIM);
		strBuilder = strBuilder.append(Util.convertToString(labels)).append(LINE_DELIM);
		strBuilder = strBuilder.append(FIELD_ISSUES).append(LINE_DELIM);
		int numIssues = issueList.size();
		for(int i=0; i<numIssues; i++){
			strBuilder = strBuilder.append(i+1).append(". ").append(issueList.get(i).getCondensedString()).append(LINE_DELIM);
		}
		return strBuilder.toString();
	}
}
