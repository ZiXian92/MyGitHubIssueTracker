package controller;

import view.View;

/**
 * Defines the list command.
 * @author ZiXian92
 */
public class ListCommand extends Command {
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
