package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private static final String PROMPT_TITLE = "New title(Enter nothing to retain current title): ";
	private static final String PROMPT_CONTENT = "New content(Enter nothing to skip, space to remove content): ";
	private static final String PROMPT_ASSIGNEE = "New assignee(Enter nothing to skip, space to remove assignee): ";
	private static final String PROMPT_LABELS = "Labels(Enter nothing to skip, comma-separated labels to replace current labels): ";
	
	private static final String LABEL_DELIM = ",";
	
	//Error messages
	private static final String MSG_PARSEERROR = "Error parsing changes.";
	private static final String MSG_IOERROR = "An IO error occurred.";
	
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
	public void execute() throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		JSONObject obj = new JSONObject();
		String input;
		try{
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
		} catch(JSONException e){
			throw new JSONException(MSG_PARSEERROR);
		} catch(IOException e){
			throw new IOException(MSG_IOERROR);
		}
		view.updateView(model.editIssue(obj, repoName, issueName));
	}
	
	private void printPrompt(String msg){
		assert msg!=null && !msg.isEmpty();
		System.out.print(msg);
	}

}
