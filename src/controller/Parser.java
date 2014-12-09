package controller;

/**
 * Defines the parser class to parse the input commands.
 * @author ZiXian92
 */
public class Parser {
    /**
     * Creates a new instance of parser.
     */
	public Parser(){
		
	}
	
	/**
	 * Parses the given input and generates the appropriate Command object.
	 * @param input The user input to execute. Cannot be null or an empty string.
	 * @param selectedRepo The currently selected repository. Cannot be an empty string.
	 * @param selectedIssue The currently selected issue. Cannot be an empty string.
	 */
	public Command parse(String input, String selectedRepo, String selectedIssue){
		assert input!=null && !input.isEmpty() && !selectedRepo.isEmpty() && !selectedIssue.isEmpty();
		String commandWord = extractFirstWord(input);
		return null;
	}
	
	private String extractFirstWord(String input){
		String[] arr = input.split("\\s+");
		return arr[0];
	}
}
