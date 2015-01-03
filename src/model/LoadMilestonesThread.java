package model;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Misc.Constants;
import Misc.Util;
import structure.Repository;

/**
 * Defines the Runnable class that fetches and adds milestones for the given repository until an error occurs.
 * @author ZiXian92
 */
public class LoadMilestonesThread implements Runnable {
	//For logging purpose
	private static Logger logger = Logger.getLogger("com.MyGitHubIssueTracker.model.LoadMilestonesThread");
	
	//Data members
	private Repository repo;
	
	public LoadMilestonesThread(Repository repo){
		assert repo!=null;
		this.repo = repo;
		logger.setUseParentHandlers(true);
	}
	
	@Override
	public void run(){
		String url = Constants.API_URL+String.format(Constants.EXT_MILESTONES, repo.getOwner(), repo.getName());
		try{
			CloseableHttpResponse response = Util.sendGetRequest(url, null);
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_OK)){
				logger.log(Level.WARNING, "Failed to get milestones.\nResponse: {0}", response.getStatusLine().toString());
				response.close();
				return;
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Missing message in response.");
				response.close();
			}
			JSONArray milestones = new JSONArray(Util.getJSONString(messageBody.getContent()));
			JSONObject milestone;
			int numMilestones = milestones.length();
			for(int i=0; i<numMilestones; i++){
				milestone = milestones.getJSONObject(i);
				repo.addMilestone(milestone.getInt(Constants.KEY_MILESTONENUMBER), milestone.getString(Constants.KEY_MILESTONETITLE));
			}
		} catch(JSONException e){
			logger.log(Level.WARNING, "Error parsing JSON. Some milestones may not have been added to repository.");
		} catch(IOException e){
			logger.log(Level.WARNING, "Error sending request for milestones.");
		}
	}
}
