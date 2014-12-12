import java.io.IOException;

import view.UI;

/**
 * The main program.
 * @author ZiXian92
 */
public class MyGitHubIssueTracker {
    private static final String MSG_IOERROR = "An IO error occurred. Exiting program.";
    
    public static void main(String[] args) {
	UI ui = new UI();
	
	try{
	    ui.run();
	} catch(IOException e){
	    printIOErrorMessage();
	}
    }
    
    private static void printIOErrorMessage(){
	System.out.println(MSG_IOERROR);
    }
}
