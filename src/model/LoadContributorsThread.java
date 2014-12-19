package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;

import structure.Repository;

/**
 * Defines a thread to fetch the given repository's contributors.
 * @author ZiXian92
 */
public class LoadContributorsThread implements Runnable {
	private static final String KEY_USERNAME = "login";
	private static final String RESPONSE_OK = "HTTP/1.1 200 OK";
	
	//Data members
	private Repository repo;
	private HttpGet request;
	
	/**
	 * Creates a Runnable instance to fetch the given repository's contributors in another thread.
	 * @param repo The repository to load contributors into.
	 * @param request The request to be executed to fetch contributor data.
	 */
	public LoadContributorsThread(Repository repo, HttpGet request){
		assert repo!=null && request!=null;
		this.repo = repo;
		this.request = request;
	}

	@Override
	public void run() {
		try{
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_OK)){
				response.close();
				return;
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
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
				repo.addAssignee(arr.getJSONObject(i).getString(KEY_USERNAME));
			}
		} catch(JSONException e){
			
		} catch (IOException e) {
			
		}
	}
	
}
