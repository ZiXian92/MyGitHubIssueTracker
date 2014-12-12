package view;

import structure.Issue;
import structure.Repository;

/**
 * Defines the class that takes the given data and outputs them to the screen.
 * @author ZiXian92
 */
public class View {
	/**
	 * Prints the given message to the console.
	 * @param message The message to be printed. Cannot be null or empty string.
	 */
	public void updateView(String message){
		assert message!=null && !message.isEmpty();
		System.out.println(message);
	}
	
	/**
	 * Displays the list of repositories to the console.
	 * @param repoList The list of repositories to be displayed.
	 */
	public void updateView(String[] repoList){
		assert repoList!=null;
		int size = repoList.length;
		for(int i=0; i<size; i++){
			System.out.println((i+1)+". "+repoList[i]);
		}
	}
	
	/**
	 * Prints the contents of the given repository to the console, formatted in its toString() method.
	 * @param repo The repository to be displayed.
	 */
    public void updateView(Repository repo){
    	assert repo!=null;
    	System.out.println(repo);
    }
    
    /**
     * Prints the contents of the given issue to the console, formatted in its toString() method.
     * @param issue The issue to be displayed.
     */
    public void updateView(Issue issue){
    	assert issue!=null;
    	System.out.println(issue);
    }
}
