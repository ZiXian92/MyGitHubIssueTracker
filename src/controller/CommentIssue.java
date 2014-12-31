package controller;

import org.json.JSONException;
import org.json.JSONObject;

import Misc.Constants;
import Misc.FailedRequestException;
import Misc.MissingMessageException;
import Misc.RequestException;
import structure.Issue;

/**
 * Defines the command to add a comment to the given issue.
 * @author ZiXian92
 */
public class CommentIssue extends Command {
	private static final String KEY_BODY = "body";
	
	//Data members
	private String issueName, repoName, comment;

	/**
	 * Creates a new instance of this command.
	 * @param comment The content of the comment to add.
	 * @param issue The name of the issue to comment.
	 * @param repo The name of the repository holding the selected issue.
	 */
	public CommentIssue(String comment, String issue, String repo){
		assert issue!=null && !issue.isEmpty() && repo!=null &&!repo.isEmpty() && comment!=null;
		this.issueName = issue;
		this.repoName = repo;
		this.comment = comment;
	}
	
	@Override
	public void execute() {
		JSONObject obj = new JSONObject();
		try {
			obj.put(KEY_BODY, comment);
		} catch (JSONException e) {
			view.updateView("Error parsing comment.");
			return;
		}
		try {
			Issue issue = model.addComment(obj, issueName, repoName);
			if(issue==null){
				view.updateView(Constants.ERROR_ISSUENOTFOUND);
				new SelectRepo(repoName).execute();
			} else{
				view.updateView(issue);
			}
		} catch (RequestException | FailedRequestException e){
			view.updateView(Constants.ERROR_ADDCOMMENT);
			new SelectIssue(issueName, repoName).execute();
		} catch(MissingMessageException | JSONException e) {
			view.updateView(Constants.ERROR_UPDATELOCALDATA);
			new SelectIssue(issueName, repoName).execute();
		}
	}

}
