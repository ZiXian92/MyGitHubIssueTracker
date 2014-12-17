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
	 * @throws IllegalArgumentException if input is an invalid command.
	 */
	public Command parse(String input, String selectedRepo, String selectedIssue) throws IllegalArgumentException{
		if(selectedRepo!=null){
			assert !selectedRepo.isEmpty();
		}
		if(selectedIssue!=null){
			assert !selectedIssue.isEmpty();
		}
		if(input==null || (input = input.trim()).isEmpty()){
			throw new IllegalArgumentException("Empty command.");
		}
		String commandWord = extractFirstWord(input);
		switch(CommandType.getCommandType(commandWord)){
			case LIST: return new ListCommand();
			case SELECT: return createSelectCommand(input, selectedRepo, selectedIssue);
			case BACK: return createBackCommand(selectedRepo, selectedIssue);
			case CLOSE: return createCloseCommand();
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
	 * @param input The string to extract the first word from. Cannot be null or empty. Recommended to use
	 * 		trim() on input before calling this method.
	 * @return The first word in the input string.
	 */
	private String extractFirstWord(String input){
		assert input!=null && !input.isEmpty();
		String[] arr = input.split("\\s+", 2);
		return arr[0];
	}
	
	/**
	 * Returns the input without the first word.
	 * @param input The string to remove the first word from. Cannot be null or empty.
	 * @return The input without the first word or an empty string if input has only 1 word.
	 */
	private String removeFirstWord(String input){
		assert input!=null && !input.isEmpty();
		String[] arr = input.split("\\s+", 2);
		return (arr.length<2)? null: arr[1];
	}
	
	/**
	 * Creates the appropriate select command.
	 * @param input The command input
	 * @param selectedRepo
	 * @param selectedIssue
	 * @return
	 * @throws IllegalArgumentException
	 */
	private Command createSelectCommand(String input, String selectedRepo, String selectedIssue) throws IllegalArgumentException {
		String parameter;
		assert input!=null;
		if((parameter = removeFirstWord(input))==null || parameter.isEmpty()){
			throw new IllegalArgumentException("Invalid command. No parameter passed.");
		} else if(selectedRepo==null){
			return new SelectRepo(parameter);
		} else if(selectedIssue==null){
			return new SelectIssue(parameter, selectedRepo);
		} else{
			throw new IllegalArgumentException("Select command not allowed when issue is selected.");
		}
	}
	
	/**
	 * Creates the appropriate command object to execute the back command.
	 * @param selectedRepo The currently selected repository. Cannot be an empty string.
	 * @param selectedIssue The currently selected issue. Cannot be an empty string
	 * @throws IllegalArgumentException if the context in which back command is issued is invalid.
	 */
	private Command createBackCommand(String selectedRepo, String selectedIssue) throws IllegalArgumentException {
		if(selectedRepo==null){
			throw new IllegalArgumentException("No repository is selected. Unable to go further up.");
		} else if(selectedIssue==null){
			return new ListCommand();
		} else{
			assert !selectedRepo.isEmpty();
			return new SelectRepo(selectedRepo);
		}
	}
	
	private Comamnd createCloseCommand(String input, String selectedRepo, String selectedIssue) throws IllegalArgumentException {
		
	}
}
