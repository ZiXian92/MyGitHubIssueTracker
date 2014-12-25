package controller;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import view.View;
import model.Model;

/**
 * Defines the Controller that coordinates between the views and the Model.
 * @author ZiXian92
 */
public class Controller implements Observer {
	//Error messages
	private static final String MSG_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
	private static final String MSG_LOGGEDIN = "Logged in as %1$s.\nLoading data from GitHub...";
	private static final String MSG_FAILEDLOGGING = "Failed to open file for logging.";
	
	//For logging. Also the main logger for the program.
	private static final Logger logger = Logger.getLogger("com.MyGitHubIssueTracker");
	
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
		logger.setLevel(Level.INFO);
		logger.setUseParentHandlers(false);
		try {
			FileHandler fh = new FileHandler("MyGitHubIssueTracker-log.txt", true);
			fh.setFormatter(new SimpleFormatter());
			logger.addHandler(fh);
		} catch (Exception e) {
			view.updateView(MSG_FAILEDLOGGING);
		}
		
	}

	/**
	 * Logs the user with the given username into GitHub.
	 * @param username The user's GitHub username.
	 */
	public boolean executeLogin(String username, String password){
		try{
			if(model.loginUser(username, password)){
				logger.log(Level.INFO, "Login success!");
				view.updateView(String.format(MSG_LOGGEDIN, username));
				model.initialise();
				new ListCommand().execute();
				return true;
			} else{
				view.updateView(MSG_FAILEDLOGIN);
				return false;
			}
		} catch(Exception e){
			view.updateView(e.getMessage());
			return false;
		}
	}

	/**
	 * Executes the given input command.
	 * @param input The input command to execute.
	 */
	public void processInput(String input){
		if(input==null){
			input = "";
		}
		try{
			Command cmd = parser.parse(input, selectedRepository, selectedIssue);
			logger.log(Level.INFO, "Executing {0}", input);
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
