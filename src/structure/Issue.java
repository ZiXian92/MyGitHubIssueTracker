package structure;

/**
 * Defines the data structure to represent an issue on GitHub.
 * @author ZiXian92
 */
public class Issue {
	//Data members
	private String title;
	
	/**
	 * Creates a new issue instance.
	 * @param title The title of the issue.
	 */
	public Issue(String title){
		assert title!=null && !title.isEmpty();
		this.title = title;
	}
	
	/**
	 * Gets the title of this issue.
	 * @return The title of this issue.
	 */
	public String getTitle(){
		return title;
	}
}
