package controller;

public class CloseIssue extends Command {
	//Data members
	private String repoName, issueName;
	
	/**
	 * Creates a new instance of this command to close an issue.
	 * @param repoName The name of the repository in which to close the issue. Cannot be null or an empty string.
	 * @param issueName The issue to close. Cannot be null or an empty string.
	 */
	public CloseIssue(String repoName, String issueName){
		assert repoName!=null && !repoName.isEmpty() && issueName!=null && !issueName.isEmpty();
		this.repoName = repoName;
		this.issueName = issueName;
	}

	@Override
	public void execute() throws Exception {
		model.closeIssue(issueName, repoName);
	}

}
