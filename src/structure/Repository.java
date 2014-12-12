package structure;

import java.util.ArrayList;

/**
 * Defines the data structure that represents a repository in GitHub.
 * @author ZiXian92
 */
public class Repository {
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
	 * Adds the given issue to this repository's issue list.
	 * @param issue the issue to be added.
	 */
	public void addIssue(Issue issue){
		assert issue!=null;
		issueList.add(issue);
	}
	
	/**
	 * Adds the given assignee to the list of allowed assignees.
	 * @param assignee The assignee to add. Cannot be null or empty string.
	 */
	public void addAssignee(String assignee){
		assert assignee!=null && !assignee.isEmpty();
		assignees.add(assignee);
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
