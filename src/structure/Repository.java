package structure;

import java.util.ArrayList;

/**
 * Defines the data structure that represents a repository in GitHub.
 * @author ZiXian92
 */
public class Repository {
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
	 * Gets the list of issues in summarized form.
	 * @return A list of issues in condensed String form.
	 */
	public String[] getIssues(){
		int size = issueList.size();
		String[] arr  = new String[size];
		for(int i=0; i<size; i++){
			arr[i] = issueList.get(i).getCondensedString();
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
		return null;
	}
}
