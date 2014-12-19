package model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

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
			model.loginUser("ZiXian92", "Nana7Nana");
			model.initialise();
		} catch (IOException e) {
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
			assertFalse(model.loginUser("zixian", "Nana7Nana"));
			assertFalse(model.loginUser("ZiXian92", "nana"));
			assertTrue(model.loginUser("ZiXian92", "Nana7Nana"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	//Tests the functionality of listing projects.
	//Replace <username> and <password> with your own GitHub login credentials before running the test.
	public void testListRepositories(){
		try{
			Model model = Model.getInstance();
			String[] list = model.listRepositories();
			assertTrue(list!=null);
		} catch(IOException e){
			e.printStackTrace();
			fail();
		} catch(Exception e){
			e.printStackTrace();
			fail();
		}
	}
	
	@Test(expected=IllegalArgumentException.class)
	//As each person's repository listng is different and Repository does not have an ID in this application,
	//it is almost impossible to automate testing of success use cases.
	//Thus this tests only for fail cases.
	//To test correctness of success cases, do it using exploratory testing.
	//Once query for a few repositories are correct, it should be correct for general cases.
	public void testGetRepository(){
		Model model = Model.getInstance();
		model.getRepository(-1);
		model.getRepository(100);	//Change if you have at least 100 repositories
		model.getRepository("some repository");
	}
	
	@Test(expected=IllegalArgumentException.class)
	//Reasoning for testing fail cases is the same as that of testing of selecting repository.
	//Change the repository name to one of your own
	public void testGetIssue(){
		Model model = Model.getInstance();
		model.getIssue("-1", "Orbital");
		model.getIssue("1000000", "Orbital");
	}
}
