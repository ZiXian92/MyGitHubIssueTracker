package controller;

import java.io.IOException;

import view.ListView;

import model.Model;

/**
 * Defines the Controller that coordinates between the views and the Model.
 * @author ZiXian92
 */
public class Controller implements Observer {
    private static final String MSG_FAILEDLOGIN = "Login failed. Either the username and/or password is incorrect.";
    private static final String MSG_LOGGEDIN = "Logged in as %1$s.";
    
    //Data members
    private String selectedProject = null, selectedIssue = null;
    private Model model = Model.getInstance();
    private ListView listView = new ListView();
    
    /**
     * Creates a new instance of this controller.
     */
    public Controller(){
	
    }
    
    /**
     * Logs the user with the given username into GitHub.
     * @param username The user's GitHub username.
     */
    public boolean executeLogin(String username, String password){
	try{
	    if(model.loginUser(username, password)){
		printLoginSuccessMessage(username);
		listView.updateView(model.listProjects());
		return true;
	    } else{
		printLoginFailureMessage();
		return false;
	    }
	} catch(IOException e){
	    //call errorview.updateView()
	    return false;
	}
    }
    
    /**
     * Executes the given input command.
     * @param input The input command to execute.
     */
    public void processInput(String input){
	
    }
    
    private void printLoginSuccessMessage(String username){
	System.out.println(String.format(MSG_LOGGEDIN, username));
    }
    
    private void printLoginFailureMessage(){
	System.out.println(MSG_FAILEDLOGIN);
    }

    @Override
    public void updateSelectedProject(String projectName) {
	if(projectName==null || projectName.isEmpty()){
	    selectedProject = null;
	    updateSelectedIssue(null);
	} else{
	    selectedProject = projectName;
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
	return selectedProject;
    }
    
    /**
     * Gets the name of the selected issue.
     * @return The name of the selected issue.
     */
    public String getSelectedIssue(){
	return selectedIssue;
    }
}
