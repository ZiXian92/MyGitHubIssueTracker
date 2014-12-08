package structure;

import java.util.ArrayList;

/**
 * Defines the data structure that represents a repository in GitHub.
 * @author ZiXian92
 */
public class Repository {
	private String name;
	private ArrayList<Issue> issueList;
	
	/**
	 * Creates a new repository instance.
	 * @param name The name of the repository.
	 */
	public Repository(String name){
		assert name!=null && !name.isEmpty();
		this.name = name;
		issueList = new ArrayList<Issue>();
	}
	
	/**
	 * Gets the name of this repository.
	 * @return The name of this repository.
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Adds the given issue to this repository's issue list.
	 * @param issue the issue to be added.
	 */
	public void addIssue(Issue issue){
		assert issue!=null;
		issueList.add(issue);
	}
}
