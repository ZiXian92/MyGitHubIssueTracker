package controller;

import org.json.JSONException;
import org.json.JSONObject;

import structure.Issue;
import misc.Constants;
import misc.FailedRequestException;
import misc.MissingMessageException;
import misc.RequestException;

/**
 * Defines the command to close the selected issue.
 * @author ZiXian92
 */
public class CloseIssue extends Command {
	//Data members
	private String repoName, issueName;
	
	/**
	 * Creates a new instance of this command to close an issue.
	 * @param repoName The name of the repository in which to close the issue. Cannot be null or an empty string.
	 * @param issueName The issue to close. Cannot be null or an empty string.
	 */
	public CloseIssue(String issueName, String repoName){
		assert repoName!=null && !repoName.isEmpty() && issueName!=null && !issueName.isEmpty();
		this.repoName = repoName;
		this.issueName = issueName;
	}

	@Override
	public void execute() {
		JSONObject changes = new JSONObject();
		try{
			changes.put(Constants.KEY_STATUS, Constants.ISSUE_STATUSCLOSED);
		} catch(JSONException e){
			view.updateView(Constants.ERROR_PARSEINPUTTOJSON);
		}
		try{
			Issue issue = model.editIssue(changes, issueName, repoName);
			if(issue!=null){
				view.updateView(issue);
			} else{
				view.updateView(Constants.ERROR_ISSUENOTFOUND);
				new SelectRepo(repoName).execute();
			}
		} catch(RequestException | FailedRequestException e){
			view.updateView(Constants.ERROR_CLOSEISSUE);
			new SelectIssue(issueName, repoName).execute();
		} catch(MissingMessageException | JSONException e){
			view.updateView(Constants.ERROR_UPDATELOCALDATA);
			new SelectIssue(issueName, repoName).execute();
		}
	}

}
