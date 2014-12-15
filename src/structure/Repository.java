package structure;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Defines the data structure that represents a repository in GitHub.
 * @author ZiXian92
 */
public class Repository {
	private static final String MSG_INVALIDINDEX = "Invalid index.";
	private static final String MSG_NOSUCHELEMENT = "The issue does not exist in this repository.";
	
	private static final String LINE_DELIM = "\n";
	private static final String CONTRIBUTOR_SEPARATOR = ", ";
	private static final String KEY_NAME = "Name: ";
	private static final String KEY_OWNER = "Owener: ";
	private static final String KEY_CONTRIBUTORS = "Contributors: ";
	private static final String KEY_ISSUES = "Issues: ";
	
	//Data members
	private String name, owner;	//To be extracted by Model to update GitHub.
	private ArrayList<Issue> issueList;
	private ArrayList<String> assignees;
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
		numIssues = 0;
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
	 * Adds the given issue to this repository's issue list.
	 * @param issue the issue to be added.
	 */
	public void addIssue(Issue issue){
		assert issue!=null;
		issueList.add(issue);
		indexList.put(issue.getTitle(), ++numIssues);
	}
	
	/**
	 * Adds the given assignee to the list of allowed assignees.
	 * @param assignee The assignee to add. Cannot be null or empty string.
	 */
	public void addAssignee(String assignee){
		assert assignee!=null && !assignee.isEmpty();
		assignees.add(assignee);
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
	
	@Override
	public String toString(){
		StringBuilder strBuilder = new StringBuilder(KEY_NAME);
		strBuilder = strBuilder.append(name).append(LINE_DELIM);
		strBuilder = strBuilder.append(KEY_OWNER).append(owner).append(LINE_DELIM);
		strBuilder = strBuilder.append(KEY_CONTRIBUTORS).append(LINE_DELIM);
		int numContributors = assignees.size();
		for(int i=0; i<numContributors; i++){
			strBuilder = strBuilder.append(assignees.get(i));
			if(i!=numContributors-1){
				strBuilder = strBuilder.append(CONTRIBUTOR_SEPARATOR);
			}
		}
		strBuilder = strBuilder.append(LINE_DELIM);
		strBuilder = strBuilder.append(KEY_ISSUES).append(LINE_DELIM);
		int numIssues = issueList.size();
		for(int i=0; i<numIssues; i++){
			strBuilder = strBuilder.append(i+1).append(". ").append(issueList.get(i).getCondensedString()).append(LINE_DELIM);
		}
		return strBuilder.toString();
	}
}
