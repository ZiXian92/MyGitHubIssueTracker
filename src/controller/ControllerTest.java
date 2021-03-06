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
		assertFalse(controller.executeLogin("Zi Xian92", "Nana7Nana"));

		//Tests case of unsuccessful login due to wrong password.
		//Please enter a wrong password.
		assertFalse(controller.executeLogin("ZiXian92", "nana"));

		//Tests case of successful login.
		assertTrue(controller.executeLogin("ZiXian92", "Nana7Nana"));
	}

	@Test
	//Tests observer method for updating the selected project.
	public void testUpdateSelectedProject(){
		Controller controller = new Controller();

		//Tests boundary cases.
		controller.updateSelectedRepository("");
		assertEquals(null, controller.getSelectedProject());
		controller.updateSelectedRepository(null);
		assertEquals(null, controller.getSelectedProject());

		//Tests typical case.
		controller.updateSelectedRepository("MyGitHubIssueTracker");
		assertEquals("MyGitHubIssueTracker", controller.getSelectedProject());
	}

	@Test
	//Tests observer method of updating the selected issue.
	public void testUpdateSelectedIssue(){
		Controller controller = new Controller();
		controller.updateSelectedRepository("MyGitHubIssueTracker");

		//Tests boundary cases
		controller.updateSelectedIssue("");
		assertEquals(null, controller.getSelectedIssue());
		controller.updateSelectedIssue(null);
		assertEquals(null, controller.getSelectedIssue());

		//Tests typical case
		controller.updateSelectedIssue("Issue 1");
		assertEquals("Issue 1", controller.getSelectedIssue());

		//Deselecting project should automatically deselect issue.
		controller.updateSelectedRepository(null);
		assertEquals(null, controller.getSelectedIssue());
	}
}
