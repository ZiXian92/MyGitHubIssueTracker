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
		input = input.trim();
		String commandWord = extractFirstWord(input);
		switch(CommandType.getCommandType(commandWord)){
			case LIST: return new ListCommand();
			default: if(selectedRepo==null){
						return new SelectRepo(input);
					} else if(selectedIssue==null){
						return new SelectIssue(input, selectedRepo);
					} else{
						return new CommentIssue(input, selectedRepo, selectedIssue);
					}
		}
	}
	
	/**
	 * Gets the first word in the input, using whitespace as delimters.
	 * @param input The string to extract the first word from. Cannot be null or empty.
	 * @return The first word in the input string.
	 */
	private String extractFirstWord(String input){
		assert input!=null && !input.isEmpty();
		String[] arr = input.split("\\s+", 2);
		return arr[0];
	}
}
