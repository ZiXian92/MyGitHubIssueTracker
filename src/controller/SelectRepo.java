package controller;

import view.View;

/**
 * Defines the command class that selects a repository from the list.
 * @author ZiXian92
 */
public class SelectRepo extends Command {
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
		try{
			view.updateView(model.getRepository(Integer.parseInt(repoName)));
		} catch(NumberFormatException e){
			view.updateView(model.getRepository(repoName));
		}
	}

}
