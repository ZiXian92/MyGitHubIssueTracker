package model;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import structure.Repository;
import Misc.RequestException;

/**
 * JUnit tezt class for Model.
 * @author ZiXian92
 */
public class ModelTest {
	//To set up and initialise data from own GitHub account beforehand.
	//This removes extra fetch overhead for tests on functionalities that do not involve fetching data.
	//Change the username and password to that of your own GitHub account.
	@BeforeClass
	public static void setup(){
		Model model = Model.getInstance();
		try {
			model.loginUser("username", "password");
			model.initialise();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	//Tests the basic authentication to GitHub.
	//Replace all <username> and <password> with your own GitHub login credentials before testing.
	//No boundary values for any equivalence partition.
	//Input partitioned into valid and invalid for each username and password.
	public void testLoginUser() {
		Model model = Model.getInstance();
		try {
			assertFalse(model.loginUser("wrong username", "correct password"));
			assertFalse(model.loginUser("correct username", "wrong password"));
			assertTrue(model.loginUser("correct username", "correct password"));	//correct password
		} catch (RequestException e) {
			e.printStackTrace();
		}
	}

	@Test
	//Tests the functionality of listing projects.
	//Replace <username> and <password> with your own GitHub login credentials before running the test.
	public void testListRepositories(){
		Model model = Model.getInstance();
		String[] list = model.listRepositories();
		assertTrue(list!=null);
		assertEquals(5, list.length);
	}
	
	//As each person's repository listing is different and Repository does not have an ID in this application,
	//it is almost impossible to automate testing of success use cases.
	//Thus this tests only for fail cases.
	//To test correctness of success cases, do it using exploratory testing.
	//Once query for a few repositories are correct, it should be correct for general cases.
	@Test
	public void testGetRepository() throws Exception{
		Model model = Model.getInstance();
		assertTrue(model.getRepository(-1)==null);
		assertTrue(model.getRepository(100)==null);	//Change if you have at least 100 repositories
		assertTrue(model.getRepository("some repository")==null);
		assertFalse(model.getRepository("ZiXian92/MyGitHubIssueTracker")==null);
	}
	
	//Reasoning for testing fail cases is the same as that of testing of selecting repository.
	//Change the repository name to one of your own
	@Test
	public void testGetIssue() throws Exception{
		Model model = Model.getInstance();
		assertTrue(model.getIssue("-1", "ZiXian92/Orbital")==null);
		assertTrue(model.getIssue("1000000", "ZiXian92/Orbital")==null);
		assertFalse(model.getIssue("1", "ZiXian92/MyGitHubIssueTracker")==null);
	}
	
	@Test
	public void testAddRepository(){
		Model model = Model.getInstance();
		int numRepos = model.listRepositories().length;
		Repository repo = new Repository("repo", "owner");
		repo.setIsInitialized(true);
		model.addRepository(repo);
		assertEquals(numRepos+1, model.listRepositories().length);
		assertEquals("repo", model.listRepositories()[numRepos]);
	}
}
