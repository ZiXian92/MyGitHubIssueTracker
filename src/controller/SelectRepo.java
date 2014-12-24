package controller;

import structure.Repository;

/**
 * Defines the command class that selects a repository from the list.
 * @author ZiXian92
 */
public class SelectRepo extends Command {
	//Error message
	private static final String MSG_NOSUCHREPO = "Repository does not exist.";
	
	//Data members
	private String repoName;

	/**
	 * Creates a new instance of this command.
	 * @param repoName
	 */
	public SelectRepo(String repoName){
		assert repoName!=null && !repoName.isEmpty();
		this.repoName = repoName.trim();
	}
	
	@Override
	public void execute() {
		Repository repo;
		try{
			repo = model.getRepository(Integer.parseInt(repoName));
		} catch(NumberFormatException e){
			repo = model.getRepository(repoName);
		}
		if(repo==null){
			view.updateView(MSG_NOSUCHREPO);
			new ListCommand().execute();
		}
	}

}
