package controller;

import view.ListView;

public class ListCommand extends Command {
	//Data member(s)
	private ListView view = new ListView();
	
	/**
	 * Creates a new instance of this command.
	 */
	public ListCommand(){
		
	}

	@Override
	public void execute() {
		view.updateView(model.listRepositories());
	}
}
