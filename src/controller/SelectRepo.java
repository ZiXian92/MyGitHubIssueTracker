package controller;

import view.RepoView;

public class SelectRepo extends Command {
	
	private RepoView repoView = new RepoView();
	private String repoName;

	public SelectRepo(String repoName){
		assert repoName!=null && !repoName.isEmpty();
		this.repoName = repoName.trim();
	}
	
	@Override
	public void execute() {
		try{
			repoView.updateView(model.getRepository(Integer.parseInt(repoName)));
		} catch(NumberFormatException e){
			m
		}
	}

}
