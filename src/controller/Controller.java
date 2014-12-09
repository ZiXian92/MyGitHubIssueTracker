package controller;

import java.io.IOException;

import view.MessageView;
import model.Model;

/**
 * Defines the Controller that coordinates between the views and the Model.
 * @author ZiXian92
 */
public class Controller implements Observer {
	private static final String MSG_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
	private static final String MSG_LOGGEDIN = "Logged in as %1$s.\nLoading data from GitHub...";
	private static final String MSG_IOERROR = "An I/O error has occured. Login failed.";

	//Data members
	private String selectedRepository = null, selectedIssue = null;
	private Model model;
	private MessageView msgView;
	private Parser parser;

	/**
	 * Creates a new instance of this controller.
	 */
	public Controller(){
		model = Model.getInstance();
		model.addObserver(this);
		msgView = new MessageView();
		parser = new Parser();
	}

	/**
	 * Logs the user with the given username into GitHub.
	 * @param username The user's GitHub username.
	 */
	public boolean executeLogin(String username, String password){
		try{
			if(model.loginUser(username, password)){
				msgView.updateView(String.format(MSG_LOGGEDIN, username));
				model.initialise();
				new List().execute();
				return true;
			} else{
				msgView.updateView(MSG_FAILEDLOGIN);
				return false;
			}
		} catch(IOException e){
			msgView.updateView(MSG_IOERROR);
			return false;
		}
	}

	/**
	 * Executes the given input command.
	 * @param input The input command to execute. Cannot be null or an empty string.
	 */
	public void processInput(String input){
		assert input!=null && !input.isEmpty();
		Command cmd = parser.parse(input, selectedRepository, selectedIssue);
		cmd.execute();
	}

	@Override
	public void updateSelectedRepository(String repo) {
		if(repo==null || repo.isEmpty()){
			selectedRepository = null;
			updateSelectedIssue(null);
		} else{
			selectedRepository = repo;
		}
	}

	@Override
	public void updateSelectedIssue(String issueName) {
		selectedIssue = (issueName==null || issueName.isEmpty())? null: issueName;
	}

	/**
	 * Gets the name of the selected project.
	 * @return The name of the selected project.
	 */
	public String getSelectedProject(){
		return selectedRepository;
	}

	/**
	 * Gets the name of the selected issue.
	 * @return The name of the selected issue.
	 */
	public String getSelectedIssue(){
		return selectedIssue;
	}
}
