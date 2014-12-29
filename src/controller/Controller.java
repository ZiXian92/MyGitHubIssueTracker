package controller;

import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import Misc.Constants;
import Misc.InvalidContextException;
import Misc.RequestException;
import view.View;
import model.Model;

/**
 * Defines the Controller that coordinates between the views and the Model.
 * @author ZiXian92
 */
public class Controller implements Observer {
	//Error messages
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
	 * @param password The password for the given username.
	 * @return true on successful login and false otherwise.
	 */
	public boolean executeLogin(String username, String password){
		try{
			if(model.loginUser(username, password)){
				logger.log(Level.INFO, "Login success!");
				view.updateView(String.format(Constants.MSG_LOGGEDIN, username));
			}
			return true;
		} catch(RequestException e){
			view.updateView(e.getMessage());
			return false;
		}
	}
	
	/**
	 * Loads data from GitHub into the Model and displays the list of repositories on success.
	 * @throws Exception If an error occurs during the initialization process.
	 */
	public void loadData() throws Exception{
		try{
			model.initialise();
			new ListCommand().execute();
		} catch(Exception e){
			view.updateView(Constants.ERROR_INITIALIZEDATA);
			throw new Exception();
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
		} catch(IllegalArgumentException | InvalidContextException e){
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
