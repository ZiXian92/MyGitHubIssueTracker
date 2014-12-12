package controller;

import view.View;
import model.Model;

/**
 * Defines the blueprint for all commands in this application.
 * @author ZiXian92
 */
public abstract class Command {
	//Shared data member(s)
	protected Model model = Model.getInstance();
	protected View view = View.getInstance();
	
	/**
	 * Executes this command.
	 * @throws Exception If an exception occurs that causes the command;s execution to fail.
	 */
	public abstract void execute() throws Exception;
}
