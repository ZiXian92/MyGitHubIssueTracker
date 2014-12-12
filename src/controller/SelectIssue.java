package controller;

/**
 * Defines the command to select an issue from the currently selected repository.
 * @author ZiXian92
 */
public class SelectIssue extends Command {
	//Data members
	private String issueName, repoName;
	
	/**
	 * Creates a new instance of this command.
	 * @param issueName The name of the issue to be selected or the index of the issue in the repository,
	 * 					starting from 1.
	 * @param repo The name of the repository that contains the issue to be selected.
	 */
	public SelectIssue(String issueName, String repo){
		assert issueName!=null && !issueName.isEmpty() && repo!=null && !repo.isEmpty();
		this.repoName = repo;
		this.issueName = issueName;
	}
	
	@Override
	public void execute() throws Exception {
		view.updateView(model.getIssue(issueName, repoName));
	}

}
