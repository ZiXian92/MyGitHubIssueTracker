package controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import structure.Issue;

/**Defines the command to add a new issue.
 * @author ZiXian92
 */
public class AddIssue extends Command {
	//JSON parameter key values.
	private static final String KEY_TITLE = "title";
	private static final String KEY_BODY = "body";
	private static final String KEY_ASSIGNEE = "assignee";
	private static final String KEY_LABELS = "labels";
	
	//Prompt messages
	private static final String PROMPT_BODY = "Please enter body message(terminate with Enter button): ";
	private static final String PROMPT_ASSIGNEE = "Enter assignee: ";
	private static final String PROMPT_LABELS = "Enter label(s)(comma-separated): ";
	
	//Error messages
	private static final String MSG_INPUTERROR = "An error occurred while reading/parsing input.";
	private static final String MSG_LOCALPARSINGERROR = "Request successful. Error parsing local copy of issue.";
	private static final String MSG_REQUESTERROR = "Failed to create issue.";
	
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

			printPrompt(PROMPT_LABELS);
			input = reader.readLine().trim();
			obj.put(KEY_LABELS, new JSONArray());
			if(!input.isEmpty()){
				String[] labels = input.split(LABEL_SEPARATOR);
				int numLabels = labels.length;
				for(int i=0; i<numLabels; i++){
					obj.append(KEY_LABELS, labels[i].trim());
				}
			}
		} catch(Exception e){
			view.updateView(MSG_INPUTERROR);
			new SelectRepo(repoName).execute();
			return;
		}
		try{
			Issue issue = model.addIssue(obj, repoName);
			if(issue==null){
				view.updateView(MSG_REQUESTERROR);
				new SelectRepo(repoName).execute();
			} else{
				view.updateView(issue);
			}
		}  catch(JSONException e){
			view.updateView(MSG_LOCALPARSINGERROR);
			new SelectRepo(repoName).execute();
		}
		
	}

	private void printPrompt(String msg){
		assert msg!=null && !msg.isEmpty();
		System.out.print(msg);
	}
}
