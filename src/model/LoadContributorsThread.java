package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.json.JSONArray;
import org.json.JSONException;

import Misc.Constants;
import Misc.Util;
import structure.Repository;

/**
 * Defines a thread to fetch the given repository's contributors.
 * @author ZiXian92
 */
public class LoadContributorsThread implements Runnable {
	//For logging purpose
	private static Logger logger = Logger.getLogger("com.MyGitHubIssueTracker.model.LoadContributorsThread");
	
	//Data members
	private Repository repo;
	
	/**
	 * Creates a Runnable instance to fetch the given repository's contributors in another thread.
	 * @param repo The repository to load contributors into.
	 * @param request The request to be executed to fetch contributor data.
	 */
	public LoadContributorsThread(Repository repo){
		assert repo!=null;
		this.repo = repo;
		logger.setUseParentHandlers(true);
	}

	@Override
	public void run() {
		String url = Constants.API_URL+String.format(Constants.EXT_CONTRIBUTORS, repo.getOwner(), repo.getName());
		try{
			CloseableHttpResponse response = Util.sendGetRequest(url, null);
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_OK)){
				logger.log(Level.WARNING, "Request to get contributors failed.\nResponse: {0}",
						response.getStatusLine().toString());
				response.close();
				return;
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Missing message from response.");
				response.close();
				return;
			}
			String input;
			StringBuilder strBuilder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(messageBody.getContent()));
			while((input = reader.readLine())!=null){
				strBuilder = strBuilder.append(input);
			}
			reader.close();
			response.close();
			JSONArray arr = new JSONArray(strBuilder.toString());
			int numContributors = arr.length();
			for(int i=0; i<numContributors; i++){
				repo.addAssignee(arr.getJSONObject(i).getString(Constants.KEY_USERLOGIN));
			}
		} catch(JSONException e){	//Will not happen unless JSON format of GitHub API changes.
			logger.log(Level.WARNING, "Error parsing JSON data.");
		} catch (IOException e) {
			//Happens if url is invalid or something unexpected happens.
			logger.log(Level.WARNING, "Error executing request for contibutors.");
		}
	}
	
}
