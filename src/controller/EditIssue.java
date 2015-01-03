package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Misc.Constants;
import Misc.FailedRequestException;
import Misc.MissingMessageException;
import Misc.RequestException;
import structure.Issue;

/**
 * Defines the command to edit the given issue.
 * @author ZiXian92
 */
public class EditIssue extends Command {
	//Prompt messages
	private static final String PROMPT_MESSAGE = "For the following fields, enter nothing to retain current "+
												"value, empty spaces(only) to erase, new values to "+
												"replace current value.";
	private static final String PROMPT_TITLE = "New title: ";
	private static final String PROMPT_CONTENT = "New content: ";
	private static final String PROMPT_ASSIGNEE = "New assignee: ";
	private static final String PROMPT_MILESTONE = "New milestone: ";
	private static final String PROMPT_LABELS = "Labels(comma-separated): ";
	
	//For labels processing
	private static final String LABEL_DELIM = ",";
	
	//Data members
	private String repoName, issueName;
	
	/**
	 * Creates a new Command instance to edit the given issue in the given repository.
	 * @param repoName The name of the repository from which to edit the issue. Cannot be null or empty string.
	 * @param issueName The name of the issue to edit. Cannot be null or empty string.
	 */
	public EditIssue(String issueName, String repoName){
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
				obj.put(Constants.KEY_ISSUETITLE, input);
			}
			
			printPrompt(PROMPT_CONTENT);
			input = reader.readLine();
			if(!input.isEmpty()){
				obj.put(Constants.KEY_CONTENT, input.trim());
			}
			
			printPrompt(PROMPT_ASSIGNEE);
			if(!(input = reader.readLine()).isEmpty()){
				input = input.trim();
				obj.put(Constants.KEY_ASSIGNEE, input.isEmpty()? JSONObject.NULL: input);
			}
			
			printPrompt(PROMPT_MILESTONE);
			input = reader.readLine();
			if(!input.isEmpty()){
				input = input.trim();
				if(input.isEmpty()){
					obj.put(Constants.KEY_MILESTONE, JSONObject.NULL);
				} else{
					obj.put(Constants.KEY_MILESTONE, input);
				}
			}
			
			printPrompt(PROMPT_LABELS);
			if(!(input = reader.readLine()).isEmpty()){
				obj.put(Constants.KEY_LABELS, new JSONArray());
				if(!(input = input.trim()).isEmpty()){
					String[] labels = input.split(LABEL_DELIM);
					int numLabels = labels.length;
					for(int i=0; i<numLabels; i++){
						obj.append(Constants.KEY_LABELS, labels[i].trim());
					}
				}
			}
		} catch(Exception e){
			view.updateView(Constants.ERROR_PARSEINPUTTOJSON);
			new SelectIssue(issueName, repoName).execute();
			return;
		}
		try {	//Main execution
			Issue issue = model.editIssue(obj, issueName, repoName);
			if(issue!=null){
				view.updateView(issue);
			} else{	//Either repository or issue is invalid
				view.updateView(Constants.ERROR_ISSUENOTFOUND);
				new SelectRepo(repoName).execute();
			}
		} catch(RequestException | FailedRequestException e){	//Issue is not edited on GitHub
			view.updateView(Constants.ERROR_EDITISSUE);
			new SelectIssue(issueName, repoName).execute();
		} catch (MissingMessageException | JSONException e) {	//Issue is edited on GitHub
			view.updateView(Constants.ERROR_UPDATELOCALDATA);
			new SelectIssue(issueName, repoName).execute();
		}
	}
	
	private void printPrompt(String msg){
		assert msg!=null && !msg.isEmpty();
		System.out.print(msg);
	}

}
