package controller;

public class EditIssue extends Command {
	private String repoName, issueName;
	
	/**
	 * Creates a new Command instance to edit the given issue in the given repository.
	 * @param repoName The name of the repository from which to edit the issue. Cannot be null or empty string.
	 * @param issueName The name of the issue to edit. Cannot be null or empty string.
	 */
	public EditIssue(String repoName, String issueName){
		assert repoName!=null && !repoName.isEmpty() && issueName!=null && !issueName.isEmpty();
		this.repoName = repoName;
		this.issueName = issueName;
	}

	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub

	}

}
