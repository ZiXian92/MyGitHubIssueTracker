package controller;

import Misc.InvalidContextException;

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
	 * @throws InvalidContextException If the command is invalid for the context.
	 */
	public Command parse(String input, String selectedRepo, String selectedIssue) throws IllegalArgumentException, InvalidContextException{
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
			case ADD: return createAddCommand(input, selectedRepo);
			case LIST: return new ListCommand();
			case SELECT: return createSelectCommand(input, selectedRepo, selectedIssue);
			case BACK: return createBackCommand(selectedRepo, selectedIssue);
			case CLOSE: return createCloseCommand(input, selectedRepo, selectedIssue);
			default: return makeAppropriateCommand(input, selectedRepo, selectedIssue);
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
	 * Creates AddIssueCommand with input(after removing first word) as issue title in the given repository.
	 * @param input The input string containing the issue title. Cannot be null or empty string.
	 * @param selectedRepo The name of the repository to create issue in. Cannot be an empty string.
	 * @throws IllegalArgumentException If input only contains the command word.
	 * @throws InvalidContextException If no repository is selected.
	 * */
	private Command createAddCommand(String input, String selectedRepo) throws IllegalArgumentException, InvalidContextException {
		assert input!=null && !input.isEmpty();
		if(selectedRepo==null){
			throw new InvalidContextException("Inapplicable action. Please select a repository.");
		}
		assert !selectedRepo.isEmpty();
		input = removeFirstWord(input);
		if(input==null || input.isEmpty()){
			throw new IllegalArgumentException("No title given.");
		}
		return new AddIssue(input, selectedRepo);
	}

	/**
	 * Creates the appropriate command object to execute the back command.
	 * @param selectedRepo The currently selected repository. Cannot be an empty string.
	 * @param selectedIssue The currently selected issue. Cannot be an empty string
	 * @throws IllegalArgumentException if the context in which back command is issued is invalid.
	 * @throws InvalidContextException If no repository is selected.
	 */
	private Command createBackCommand(String selectedRepo, String selectedIssue) throws InvalidContextException {
		if(selectedRepo==null){
			throw new InvalidContextException("No repository is selected. Unable to go further up.");
		} else if(selectedIssue==null){
			return new ListCommand();
		} else{
			assert !selectedRepo.isEmpty();
			return new SelectRepo(selectedRepo);
		}
	}
	
	/**
	 * Creates the appropriate select command.
	 * @param input The command input
	 * @param selectedRepo The currently selected repository. Cannot be an empty string.
	 * @param selectedIssue The currently selected issue. Cannot be an empty string.
	 * @return SelectRepo or SelectIssue comamnd.
	 * @throws IllegalArgumentException If input contains only the command word.
	 * @throws InvalidContextException If an issue is already selected.
	 */
	private Command createSelectCommand(String input, String selectedRepo, String selectedIssue) throws IllegalArgumentException, InvalidContextException {
		String parameter;
		assert input!=null;
		if((parameter = removeFirstWord(input))==null || parameter.isEmpty()){
			throw new IllegalArgumentException("Invalid command. No parameter passed.");
		} else if(selectedRepo==null){
			return new SelectRepo(parameter);
		} else if(selectedIssue==null){
			assert !selectedRepo.isEmpty();
			return new SelectIssue(parameter, selectedRepo);
		} else{
			throw new InvalidContextException("Select command not allowed when issue is selected.");
		}
	}
	
	/**
	 * Creates the appropriate command for closing an issue.
	 * @param input The command input. Cannot be null or empty string.
	 * @param selectedRepo The name of the selected repository. Cannot be an empty string.
	 * @param selectedIssue The name of the selected issue. Cannot be an empty string.
	 * @throws IllegalArgumentException If there is insufficient parameters in input or the context
	 * 									in which this command is given is invalid.
	 * @throws InvalidContextException If the context is invalid.
	 */
	private Command createCloseCommand(String input, String selectedRepo, String selectedIssue) throws IllegalArgumentException, InvalidContextException {
		assert input!=null && !input.isEmpty();
		if(selectedRepo==null){
			throw new InvalidContextException("No repository selected.");
		}
		assert !selectedRepo.isEmpty();
		if(selectedIssue==null){
			input = removeFirstWord(input);
			if(input==null || input.isEmpty()){
				throw new IllegalArgumentException("No issue selected.");
			}
			return new CloseIssue(selectedRepo, input);
		}
		assert !selectedIssue.isEmpty();
		return new CloseIssue(selectedRepo, selectedIssue);
	}
	
	/**
	 * Makes the appropriate command based on the input parameters.
	 * @param input The input parameter string.
	 * @param selectedRepo The name of the currently selected repository. Can be null but not an empty string.
	 * @param selectedIssue The name of the currently selected issue. Can be null but not an empty string.
	 * @return
	 */
	private Command makeAppropriateCommand(String input, String selectedRepo, String selectedIssue) {
		if(selectedRepo==null){
			return new SelectRepo(input);
		} else if(selectedIssue==null){
			assert !selectedRepo.isEmpty();
			return new SelectIssue(input, selectedRepo);
		} else{
			assert !selectedRepo.isEmpty() && !selectedIssue.isEmpty();
			return new CommentIssue(input, selectedRepo, selectedIssue);
		}
	}
}
