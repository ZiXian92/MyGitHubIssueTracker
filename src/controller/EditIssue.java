package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Misc.Constants;
import structure.Issue;

/**
 * Defines the command to edit the given issue.
 * @author ZiXian92
 */
public class EditIssue extends Command {
	//JSON key values
	private static final String KEY_TITLE = "title";
	private static final String KEY_BODY = "body";
	private static final String KEY_ASSIGNEE = "assignee";
	private static final String KEY_LABELS = "labels";
	
	//Prompt messages
	private static final String PROMPT_MESSAGE = "For the following fields, enter nothing to retain current "+
												"value, empty spaces(only) to erase, new values to "+
												"replace current value.";
	private static final String PROMPT_TITLE = "New title: ";
	private static final String PROMPT_CONTENT = "New content: ";
	private static final String PROMPT_ASSIGNEE = "New assignee: ";
	private static final String PROMPT_LABELS = "Labels(comma-separated): ";
	
	//For labels processing
	private static final String LABEL_DELIM = ",";
	
	//Error messages
	private static final String MSG_NOSUCHITEM = "Repository/Issue not found.";
	
	//Data members
	private String repoName, issueName;
	
	/**
	 * Creates a new Command instance to edit the given issue in the given repository.
	 * @param repoName The name of the repository from which to edit the issue. Cannot be null or empty string.
	 * @param issueName The name of the issue to edit. Cannot be null or empty string.
	 */
	public EditIssue(String repoName, String issueName){
		assert repoName!=null && !repoName.isEmpty() && issueName!=null && !issueName.isEmpty();
		this.repoName = repoName;
		this.issueName = issueName;
	}

	@Override
	public void execute() {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		JSONObject obj = new JSONObject();
		String input;
		view.updateView(PROMPT_MESSAGE);
		try{	//Gets details from user input
			printPrompt(PROMPT_TITLE);
			input = reader.readLine().trim();
			if(!input.isEmpty()){
				obj.put(KEY_TITLE, input);
			}
			
			printPrompt(PROMPT_CONTENT);
			input = reader.readLine();
			if(!input.isEmpty()){
				obj.put(KEY_BODY, input.trim());
			}
			
			printPrompt(PROMPT_ASSIGNEE);
			if(!(input = reader.readLine()).isEmpty()){
				input = input.trim();
				obj.put(KEY_ASSIGNEE, input.isEmpty()? JSONObject.NULL: input);
			}
			
			printPrompt(PROMPT_LABELS);
			if(!(input = reader.readLine()).isEmpty()){
				obj.put(KEY_LABELS, new JSONArray());
				if(!(input = input.trim()).isEmpty()){
					String[] labels = input.split(LABEL_DELIM);
					int numLabels = labels.length;
					for(int i=0; i<numLabels; i++){
						obj.append(KEY_LABELS, labels[i].trim());
					}
				}
			}
		} catch(Exception e){
			view.updateView(Constants.ERROR_PARSEINPUTTOJSON);
			new SelectIssue(issueName, repoName).execute();
			return;
		}
		try {	//Main execution
			Issue issue = model.editIssue(obj, repoName, issueName);
			if(issue!=null){
				view.updateView(issue);
			} else{	//Either repository or issue is invalid
				view.updateView(MSG_NOSUCHITEM);
				new ListCommand().execute();
			}
		} catch (JSONException e) {
			view.updateView(Constants.ERROR_UPDATELOCALCOPY);
			new SelectIssue(issueName, repoName).execute();
		}
	}
	
	private void printPrompt(String msg){
		assert msg!=null && !msg.isEmpty();
		System.out.print(msg);
	}

}
