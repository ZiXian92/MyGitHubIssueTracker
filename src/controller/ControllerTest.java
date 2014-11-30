package controller;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * JUnit test class for Controller.
 * @author ZiXian92
 */
public class ControllerTest {
    
    @Test
    //Change the username in executeLogin() to your own GitHub username.
    //You will be prompted for your password when running this test.
    public void testLogin() {
	//Tests case of unsuccessful login due to wrong username.
	//Please use correct password.
	Controller controller = new Controller();
	controller.executeLogin("Zi Xian92");
	assertFalse(controller.getLoginStatus());
	
	//Tests case of unsuccessful login due to wrong password.
	//Please enter a wrong password.
	controller.executeLogin("ZiXian92");
	assertFalse(controller.getLoginStatus());
	
	//Tests case of successful login.
	controller.executeLogin("ZiXian92");
	assertTrue(controller.getLoginStatus());
    }
    
    @Test
    //Tests observer method for updating the selected project.
    public void testUpdateSelectedProject(){
	Controller controller = new Controller();
	
	//Tests boundary cases.
	controller.updateSelectedProject("");
	assertEquals(null, controller.getSelectedProject());
	controller.updateSelectedProject(null);
	assertEquals(null, controller.getSelectedProject());
	
	//Tests typical case.
	controller.updateSelectedProject("MyGitHubIssueTracker");
	assertEquals("MyGitHubIssueTracker", controller.getSelectedProject());
    }
    
    @Test
    //Tests observer method of updating the selected issue.
    public void testUpdateSelectedIssue(){
	Controller controller = new Controller();
	controller.updateSelectedProject("MyGitHubIssueTracker");
	
	//Tests boundary cases
	controller.updateSelectedIssue("");
	assertEquals(null, controller.getSelectedIssue());
	controller.updateSelectedIssue(null);
	assertEquals(null, controller.getSelectedIssue());
	
	//Tests typical case
	controller.updateSelectedIssue("Issue 1");
	assertEquals("Issue 1", controller.getSelectedIssue());
	
	//Deselecting project should automatically deselect issue.
	controller.updateSelectedProject(null);
	assertEquals(null, controller.getSelectedIssue());
    }
}
