package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONObject;

import structure.Issue;

/**Defines the command to add a new issue.
 * @author ZiXian92
 */
public class AddIssue extends Command {
	private static final String KEY_TITLE = "title";
	private static final String KEY_BODY = "body";
	private static final String KEY_ASSIGNEE = "assignee";
	
	private static final String PROMPT_BODY = "Please enter body message(terminate with Enter button): ";
	private static final String PROMPT_ASSIGNEE = "Assignee: ";
	
	//Data members
	private String title, repoName;
	
	/**
	 * Creates a new command instance to an an issue.
	 * @param title The title of the new issue. Cannot be null or empty.
	 * @param repoName The name of the repository to add the new issue into. Cannot be null or empty.
	 */
	public AddIssue(String title, String repoName){
		assert title!=null && !title.isEmpty() && repoName!=null && !repoName.isEmpty();
		this.title = title;
		this.repoName = repoName;
	}

	@Override
	public void execute() throws Exception {
		JSONObject obj = new JSONObject();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		obj.put(KEY_TITLE, title);
		printPrompt(PROMPT_BODY);
		String input = reader.readLine();
		obj.put(KEY_BODY, input);
		printPrompt(PROMPT_ASSIGNEE);
		input = reader.readLine();
		if(input.trim().isEmpty()){
			obj.put(KEY_ASSIGNEE, JSONObject.NULL);
		} else{
			obj.put(KEY_ASSIGNEE, input);
		}
		Issue issue = model.addIssue(obj, repoName);
		if(issue==null){
			view.updateView(model.getRepository(repoName));
		} else{
			view.updateView(issue);
		}
	}

	private void printPrompt(String msg){
		assert msg!=null && !msg.isEmpty();
		System.out.print(msg);
	}
}
