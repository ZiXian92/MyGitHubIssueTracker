package controller;

import misc.Constants;
import structure.Repository;

/**
 * Defines the command class that selects a repository from the list.
 * @author ZiXian92
 */
public class SelectRepo extends Command {
	//Data members
	private String repoName;

	/**
	 * Creates a new instance of this command.
	 * @param repoName The name of the repository to be selected.
	 */
	public SelectRepo(String repoName){
		assert repoName!=null && !repoName.isEmpty();
		this.repoName = repoName.trim();
	}
	
	@Override
	public void execute() {
		Repository repo;
		try{
			try{
				repo = model.getRepository(Integer.parseInt(repoName));
			} catch(NumberFormatException e){
				repo = model.getRepository(repoName);
			}
			if(repo==null){
				view.updateView(Constants.ERROR_REPONOTFOUND);
				new ListCommand().execute();
			} else{
				view.updateView(repo);
			}
		} catch(Exception e){
			view.updateView(e.getMessage());
			new ListCommand().execute();
		}
		
	}

}
