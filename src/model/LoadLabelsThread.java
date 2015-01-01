package model;

import java.io.IOException;
import java.util.ArrayList;
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
 * Defines the Runnable object that executes the given request to load labels to the given repository.
 * @author ZiXian92
 */
public class LoadLabelsThread implements Runnable {
	//For logging purpose
	private static Logger logger = Logger.getLogger("com.MyGitHubIssueTracker.model.LoadLabelsThread");
	
	//Data members
	private Repository repo;
	
	/**
	 * Creates a new instance of this Runnable object.
	 * @param repo The repository to load labels into. Cannot be null.
	 * @param req The Http GET request for the repository's labels. Cannot be null.
	 */
	public LoadLabelsThread(Repository repo){
		assert repo!=null;
		this.repo = repo;
		logger.setUseParentHandlers(true);
	}

	@Override
	public void run() {
		String url = Constants.API_URL+String.format(Constants.EXT_REPOLABELS, repo.getOwner(), repo.getName());
		try{
			CloseableHttpResponse res = Util.sendGetRequest(url, null);
			if(!res.getStatusLine().toString().equals(Constants.RESPONSE_OK)){
				logger.log(Level.WARNING, "Request to fetch labels failed.\nResponse: {0}",
						res.getStatusLine().toString());
				res.close();
				return;
			}
			HttpEntity messageBody = res.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Missing message in response.");
				res.close();
				return;
			}
			JSONArray labelsArr = new JSONArray(Util.getJSONString(messageBody.getContent()));
			res.close();
			int numLabels = labelsArr.length();
			ArrayList<String> labels = new ArrayList<String>();
			for(int i=0; i<numLabels; i++){	//Either add all or none of the labels.
				labels.add(labelsArr.getJSONObject(i).getString(Constants.KEY_LABELNAME));
			}
			repo.setLabels(labels);
		} catch(JSONException e){	//Will not appen unless JSON format for GitHub API changes.
			logger.log(Level.WARNING, "Error parsing JSON.");
		} catch(IOException e){	//Happens if url is invalid or something unexpected happens.
			logger.log(Level.WARNING, "Error executing request to fetch labels.");
		}
	}

}
