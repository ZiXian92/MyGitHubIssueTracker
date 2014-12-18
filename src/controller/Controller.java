package controller;

import java.io.IOException;

import view.View;
import model.Model;

/**
 * Defines the Controller that coordinates between the views and the Model.
 * @author ZiXian92
 */
public class Controller implements Observer {
	private static final String MSG_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
	private static final String MSG_LOGGEDIN = "Logged in as %1$s.\nLoading data from GitHub...";
	private static final String MSG_IOERROR = "An I/O error has occured. Login failed.";
	private static final String MSG_NOREPOSELECTED = "Repository selected: None";
	private static final String MSG_SELECTEDREPO = "Repository selected: %1$s";
	private static final String MSG_NOISSUESELECTED = "Issue selected: None";
	private static final String MSG_SELECTEDISSUE = "Issue selected: %1$s";

	//Data members
	private String selectedRepository = null, selectedIssue = null;
	private Model model;
	private View view;
	private Parser parser;

	/**
	 * Creates a new instance of this controller.
	 */
	public Controller(){
		model = Model.getInstance();
		model.addObserver(this);
		view = View.getInstance();
		parser = new Parser();
	}

	/**
	 * Logs the user with the given username into GitHub.
	 * @param username The user's GitHub username.
	 */
	public boolean executeLogin(String username, String password){
		try{
			if(model.loginUser(username, password)){
				view.updateView(String.format(MSG_LOGGEDIN, username));
				model.initialise();
				try {
					new ListCommand().execute();
				} catch (Exception e) {
					view.updateView(e.getMessage());
				}
				return true;
			} else{
				view.updateView(MSG_FAILEDLOGIN);
				return false;
			}
		} catch(IOException e){
			view.updateView(MSG_IOERROR);
			return false;
		}
	}

	/**
	 * Executes the given input command.
	 * @param input The input command to execute. Cannot be null or an empty string.
	 */
	public void processInput(String input){
		try{
			Command cmd = parser.parse(input, selectedRepository, selectedIssue);
			cmd.execute();
		} catch(Exception e){
			view.updateView(e.getMessage());
		}
	}

	@Override
	public void updateSelectedRepository(String repo) {
		if(repo==null || repo.isEmpty()){
			selectedRepository = null;
			//view.updateView(MSG_NOREPOSELECTED);
			updateSelectedIssue(null);
		} else{
			selectedRepository = repo;
			//view.updateView(String.format(MSG_SELECTEDREPO, selectedRepository));
		}
	}

	@Override
	public void updateSelectedIssue(String issueName) {
		selectedIssue = (issueName==null || issueName.isEmpty())? null: issueName;
		//view.updateView((selectedIssue==null)? MSG_NOISSUESELECTED: String.format(MSG_SELECTEDISSUE, selectedIssue));
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
