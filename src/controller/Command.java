package controller;

import model.Model;

/**
 * Defines the blueprint for all commands in this application.
 * @author ZiXian92
 */
public abstract class Command {
	//Shared data member(s)
	protected Model model = Model.getInstance();
	
	/**
	 * Executes this command.
	 */
	public abstract void execute();
}
