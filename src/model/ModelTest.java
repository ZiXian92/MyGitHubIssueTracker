package model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

/**
 * JUnit tezt class for Model.
 * @author ZiXian92
 */
public class ModelTest {
    
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
    public void testListProjects(){
	try{
	    Model model = Model.getInstance();
	    model.loginUser("zixian92", "Nana7Nana");
	    assertTrue(model.listProjects()!=null);
	    //System.out.println(model.listProjects());
	} catch(IOException e){
	    
	}
    }
    
}
