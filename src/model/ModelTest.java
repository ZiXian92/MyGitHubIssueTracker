package model;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class ModelTest {
    
    @Test
    public void testLoginUser() {
	Model model = Model.getInstance();
	try {
	    assertTrue(model.loginUser("ZiXian92", "Nana7Nana"));
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }
    
}
