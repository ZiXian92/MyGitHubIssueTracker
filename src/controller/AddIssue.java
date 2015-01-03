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

/**Defines the command to add a new issue.
 * @author ZiXian92
 */
public class AddIssue extends Command {
	//Prompt messages
	private static final String PROMPT_BODY = "Please enter body message(terminate with Enter button): ";
	private static final String PROMPT_ASSIGNEE = "Enter assignee: ";
	private static final String PROMPT_MILESTONE = "Enter milestone: ";
	private static final String PROMPT_LABELS = "Enter label(s)(comma-separated): ";
	
	//For separating labels
	private static final String LABEL_SEPARATOR = ",";
	
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
	public void execute() {
		JSONObject obj = new JSONObject();
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try{
			obj.put(Constants.KEY_ISSUETITLE, title);

			printPrompt(PROMPT_BODY);
			String input = reader.readLine();
			obj.put(Constants.KEY_CONTENT, input);

			printPrompt(PROMPT_ASSIGNEE);
			input = reader.readLine();
			if(input.trim().isEmpty()){
				obj.put(Constants.KEY_ASSIGNEE, JSONObject.NULL);
			} else{
				obj.put(Constants.KEY_ASSIGNEE, input);
			}
			
			printPrompt(PROMPT_MILESTONE);
			input = reader.readLine();
			if(!input.isEmpty()){
				obj.put(Constants.KEY_MILESTONE, input);
			}

			printPrompt(PROMPT_LABELS);
			input = reader.readLine().trim();
			obj.put(Constants.KEY_LABELS, new JSONArray());
			if(!input.isEmpty()){
				String[] labels = input.split(LABEL_SEPARATOR);
				int numLabels = labels.length;
				for(int i=0; i<numLabels; i++){
					obj.append(Constants.KEY_LABELS, labels[i].trim());
				}
			}
		} catch(Exception e){
			view.updateView(Constants.ERROR_INPUTPARSING);
			new SelectRepo(repoName).execute();
			return;
		}
		try{
			Issue issue = model.addIssue(obj, repoName);
			if(issue==null){
				view.updateView(Constants.ERROR_REPOERROR);
				new SelectRepo(repoName).execute();
			} else{
				view.updateView(issue);
			}
		} catch(RequestException | FailedRequestException e){
			view.updateView(Constants.ERROR_ADDISSUE);
			new SelectRepo(repoName).execute();
		} catch(MissingMessageException | JSONException e){
			view.updateView(Constants.ERROR_UPDATELOCALDATA);
			new SelectRepo(repoName).execute();
		}
		
	}

	private void printPrompt(String msg){
		assert msg!=null && !msg.isEmpty();
		System.out.print(msg);
	}
}
