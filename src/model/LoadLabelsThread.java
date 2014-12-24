package model;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import Misc.Util;
import structure.Repository;

/**
 * Defines the Runnable object that executes the given request to load labels to the given repository.
 * @author ZiXian92
 */
public class LoadLabelsThread implements Runnable {
	//Constants
	private static final String KEY_LABELNAME = "name";
	private static final String RESPONSE_OK = "HTTP/1.1 200 OK";
	
	//Data members
	private Repository repo;
	private HttpGet req;
	
	/**
	 * Creates a new instance of this Runnable object.
	 * @param repo The repository to load labels into. Cannot be null.
	 * @param req The Http GET request for the repository's labels. Cannot be null.
	 */
	public LoadLabelsThread(Repository repo, HttpGet req){
		assert repo!=null && req!=null;
		this.repo = repo;
		this.req = req;
	}

	@Override
	public void run() {
		try{
			CloseableHttpResponse res = HttpClients.createDefault().execute(req);
			if(res.getStatusLine().toString().equals(RESPONSE_OK) && res.getEntity()!=null){
				HttpEntity messageBody = res.getEntity();
				JSONArray labelsArr = new JSONArray(Util.getJSONString(messageBody.getContent()));
				int numLabels = labelsArr.length();
				for(int i=0; i<numLabels; i++){
					repo.addLabel(labelsArr.getJSONObject(i).getString(KEY_LABELNAME));
				}
			}
			res.close();
		} catch(Exception e){
			
		}
	}

}
