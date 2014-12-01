package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Defines the UI class that prompts user and receives user input.
 * @author ZiXian92
 */
public class UI {
    //Constants
    private static final String COMMAND_EXIT = "exit";
    private static final String PROMPT_USERNAME = "GitHub username: ";
    private static final String PROMPT_PASSWORD = "GitHub password: ";
    private static final String PROMPT_COMMAND = "Command: ";
    
    //Data members
    private BufferedReader reader;	//Used to read in user input
    private Controller controller;	//Used to execute user input
    
    /**
     * Creates a new instance of UI.
     */
    public UI(){
	reader = new BufferedReader(new InputStreamReader(System.in));
	controller = new Controller();
    }
    
    /**
     * Runs the UI and the program.
     * @throws IOException if error occurs when reading input or closing input reader.
     */
    public void run() throws IOException {
	String username, password, input;
	do{
	    promptUsername();
	    username = readUsername();
	    promptPassword();
	    password = readPassword();
	} while(!controller.executeLogin(username, password));
	promptUserInput();
	while(!(input = readInput()).equals(COMMAND_EXIT)){
	    controller.processInput(input);
	    promptUserInput();
	}
	reader.close();
    }
    
    private String readUsername() throws IOException{
	return reader.readLine();
    }
    
    private String readPassword() throws IOException{
	return reader.readLine();
    }
    
    private String readInput() throws IOException{
	return reader.readLine();
    }
    
    private void promptUsername(){
	System.out.print(PROMPT_USERNAME);
    }
    
    private void promptPassword(){
	System.out.print(PROMPT_PASSWORD);
    }
    
    private void promptUserInput(){
	System.out.print(PROMPT_COMMAND);
    }
}
