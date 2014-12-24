package model;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import structure.Repository;

public class LoadLabelsThread implements Runnable {
	private static final String KEY_LABELNAME = "name";
	private static final String RESPONSE_OK = "HTTP/1.1 200 OK";
	
	//Data members
	private Repository repo;
	private HttpGet req;
	
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
			}
		}catch(IOException e){

		}
	}

}
