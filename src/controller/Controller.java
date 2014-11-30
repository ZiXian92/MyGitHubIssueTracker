package controller;

/**
 * Defines the Controller that coordinates between the views and the Model.
 * @author ZiXian92
 */
public class Controller implements Observer {
    //Data members
    private boolean isLoggedIn = false;
    private String selectedProject = null, selectedIssue = null;
    
    public Controller(){
	
    }
    
    /**
     * Logs the user with the given username into GitHub.
     * @param username The user's GitHub username.
     */
    public void executeLogin(String username){
	
    }
    
    /**
     * Executes the given input command.
     * @param input The input command to execute.
     */
    public void processInput(String input){
	
    }
    
    /**
     * Checks if the user is logged in.
     * @return true if the user is logged in to GitHub via this application and false otherwise.
     */
    public boolean getLoginStatus(){
	return isLoggedIn;
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
