package controller;

import java.util.HashSet;

/**
 * Defines the command type and their list of aliases.
 * @author ZiXian92
 */
public enum CommandType {
	ADD("add", "a"),
	EDIT("edit", "e"),
	LIST("list", "ls", "l"), 
	SELECT("select", "s"),
	BACK("back", "b", "up"),
	CLOSE("close", "c"), 
	OTHER();
	
	private HashSet<String> aliasTable;
	
	CommandType(String... aliases){
		aliasTable = new HashSet<String>();
		for(String str: aliases){
			aliasTable.add(str);
		}
	}
	
	/**
	 * Gets the appropriate command type represented by the given keyword.
	 * @param word The command keyword.
	 * @return The command type represented by the keyword, or null if the keyword
	 * 		does not represent any command type.
	 */
	public static CommandType getCommandType(String word){
		assert word!=null && !word.isEmpty();
		for(CommandType cmd: CommandType.values()){
			if(cmd.aliasTable.contains(word))
				return cmd;
		}
		return OTHER;
	}
}
