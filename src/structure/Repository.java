package structure;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import Misc.Util;

/**
 * Defines the data structure that represents a repository in GitHub.
 * It is up to the programmer to ensure that the information is consistent with that on GitHub.
 * @author ZiXian92
 */
public class Repository {
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
	 * @param owner The name of this repository's owner.
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
	 * @return The repository represented by the given JSON object.
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
	 * Gets the list of labels in this repository.
	 * @return An ArrayList of labels that are applicable to issues under it.
	 */
	public ArrayList<String> getLabels(){
		return labels;
	}
	
	/**
	 * Gets the index-th issue in this repository.
	 * @param index The index of the issue on this repository.
	 * @return The index-th issue in this repository's issue list or null if the index is invalid.
	 */
	public Issue getIssue(int index){
		if(index<1 || index>numIssues){
			return null;
		}
		return issueList.get(index-1);
	}
	
	/**
	 * Gets the issue with the given name in this repository.
	 * @param issueName The name of the issue to get. Cannot be null or empty.
	 * @return The issue with the given name in this repository. Returns null if the issue cannot be found or
	 * 			if issueName is invalid.
	 */
	public Issue getIssue(String issueName){
		if(issueName==null || issueName.isEmpty() || !indexList.containsKey(issueName)){
			return null;
		}
		int index = indexList.get(issueName);
		return getIssue(index);
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
	 * Replaces the given issue with the given updated issue.
	 * Does nothing if issueName is null or empty, editedIssue is null, or if this repository
	 * does not contain an issue with the given issueName.
	 * @param issueName The name of the issue to be replaced. Cannot be null or empty string.
	 * @param editedIssue The new issue to replace the target issue. Cannot be null.
	 */
	public void replaceIssue(String issueName, Issue editedIssue){
		if(issueName==null || issueName.isEmpty() || editedIssue==null){
			return;
		}
		if(indexList.containsKey(issueName)){
			int index = indexList.get(issueName);
			issueList.set(index-1, editedIssue);
			indexList.remove(issueName);
			indexList.put(editedIssue.getTitle(), index);
			editedIssue.setApplicableLabels(labels);
		}
	}
	
	/**
	 * Sets the owner of this repository. Does nothing if owner is null or an empty string.
	 * @param owner The name of the owner of this repository.
	 */
	public void setOwner(String owner){
		if(owner!=null && !owner.isEmpty()){
			this.owner = owner;
		}
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
