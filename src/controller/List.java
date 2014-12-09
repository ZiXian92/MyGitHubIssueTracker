package controller;

import view.ListView;

public class List extends Command {
	//Data member(s)
	private ListView view = new ListView();
	
	/**
	 * Creates a new instance of this command.
	 */
	public List(){
		
	}

	@Override
	public void execute() {
		view.updateView(model.listRepositories());
	}
}
