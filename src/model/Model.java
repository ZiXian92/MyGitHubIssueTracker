package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import controller.Observer;
import structure.Issue;
import structure.Repository;

/**
 * Defines the component that manages the in-memory storage of GitHub issues,
 * as well as synchronizing data with GitHub.
 * @author ZiXian92
 */
public class Model {
	private static final String API_URL = "https://api.github.com";
	private static final String EXT_USER = "/user";
	private static final String EXT_REPOS = "/user/repos";
	private static final String EXT_REPOISSUES = "/repos/%1$s/%2$s/issues";

	private static final String HEADER_ACCEPT = "Accept";
	private static final String VAL_ACCEPT = "application/vnd.github.v3+json";
	private static final String VAL_PREVIEWACCEPT = "application/vnd.github.moondragon-preview+json";
	private static final String HEADER_AUTH = "Authorization";
	private static final String VAL_AUTH = "Basic %1$s";

	private static final String RESPONSE_OK = "HTTP/1.1 200 OK";

	private static final String KEY_REPONAME = "name";
	private static final String KEY_OWNER = "owner";
	private static final String KEY_OWNERLOGIN = "login";
	private static final String KEY_ISSUETITLE = "title";
	private static final String KEY_STATUS = "state";
	private static final String KEY_CONTENT = "body";
	private static final String KEY_ASSIGNEE = "assignee";

	private static Model instance = null;	//The single instance of this class

	//Data members
	private String authCode, username;
	private ArrayList<Repository> repoList;
	private ArrayList<Observer> observerList;

	private Model(){
		repoList = new ArrayList<Repository>();
		observerList = new ArrayList<Observer>();
	}

	/**
	 * Gets the only instance of Model.
	 */
	public static Model getInstance(){
		if(instance==null){
			return instance = new Model();
		}
		return instance;
	}
	
	/**
	 * Adds the given observer to the list of observers.
	 * @param observer The observer object to be added. Cannot be null.
	 */
	public void addObserver(Observer observer){
		assert observer!=null;
		observerList.add(observer);
	}

	/**
	 * Authenticates the user with the given username with GitHub API.
	 * @param username The username of the user to log in.
	 * @return true if authentication is successful and false otherwise.
	 * @throws IOException if an IO error occurs during the request.
	 */
	public boolean loginUser(String username, String password) throws IOException {
		if(username==null || username.isEmpty() || password==null || password.isEmpty()){
			return false;
		}
		
		//Creates the request.
		HttpGet request = new HttpGet(API_URL+EXT_USER);
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);

		//Encoding for basic authentication is to be done on username:password.
		String code = new String(Base64.encodeBase64((username+":"+password).getBytes()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, code));

		CloseableHttpResponse response = HttpClients.createDefault().execute(request);
		String responseStatus = response.getStatusLine().toString();
		response.close();
		if(responseStatus.equals(RESPONSE_OK)){
			this.username = username;
			this.authCode = code;
			return true;
		}
		return false;
	}

	/**
	 * Loads the repositories and issues from GitHub.
	 * @throws IOException if an IO error occurs during the request.
	 */
	public void initialise() throws IOException {
		assert authCode!=null && !authCode.isEmpty();

		//Send request to get list of repositories.
		HttpGet request = new HttpGet(API_URL+EXT_REPOS);
		request.addHeader(HEADER_ACCEPT, VAL_PREVIEWACCEPT);
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		CloseableHttpResponse response = HttpClients.createDefault().execute(request);
		if(!response.getStatusLine().toString().equals(RESPONSE_OK)){
			response.close();
			return;
		}

		//Get the message body of the response.
		HttpEntity messageBody = response.getEntity();

		if(messageBody==null){
			response.close();
			return;
		}

		//Parse the JSON string into Repository instances.
		try{
			JSONArray arr = new JSONArray(getJSONContent(messageBody.getContent()));
			response.close();
			int size = arr.length();
			Repository temp;
			for(int i=0; i<size; i++){	//Add repository to list.
				temp = makeRepository(arr.getJSONObject(i));
				if(temp!=null){
					repoList.add(temp);
				}
			}
		} catch(JSONException e){
			//Will not happen unless GitHub API decides to change their JSON format.
		}
	}
	
	/**
	 * Gets the list of repositories that the current user is involved in.
	 * @return The list of names of repositories that the current user is involved in.
	 */
	public String[] listRepositories(){
		String[] list = new String[repoList.size()];
		Iterator<Repository> itr = repoList.iterator();
		for(int i=0; itr.hasNext(); i++){
			list[i] = itr.next().getName();
		}
		notifyObservers(null, null);
		return list;
	}
	
	/**
	 * Fetches issues under the specified repository and stores them in
	 * a Repository instance.
	 * @param obj The JSONObject representation of the repository to be instantiated..
	 * @return A Repository representing the specified GitHub repository.
	 * @throws IOException when IO error occurs during the request.
	 */
	public Repository makeRepository(JSONObject obj) throws IOException {
		assert obj!=null;
		try{
			//Initialize repository with name and owner
			String repoName = obj.getString(KEY_REPONAME);
			String owner = obj.getJSONObject(KEY_OWNER).getString(KEY_OWNERLOGIN);
			Repository repo = new Repository(repoName, owner);
			
			//Sends request for issues under this repository.
			HttpGet request = new HttpGet(API_URL+String.format(EXT_REPOISSUES, owner, repoName));
			request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_OK)){
				response.close();
				return null;
			}

			//Loads issues from GitHub repository into this repository instance.
			HttpEntity messageBody = response.getEntity();
			if(messageBody!=null){
				JSONObject temp;
				JSONArray arr = new JSONArray(getJSONContent(messageBody.getContent()));
				response.close();
				int size = arr.length();
				for(int i=0; i<size; i++){
					temp = arr.getJSONObject(i);
					repo.addIssue(makeIssue(temp));
				}
			}
			return repo;
		} catch(JSONException e){
			return null;
		}
	}
	
	/**
	 * Creates an Issue from the given JSON representation.
	 * @param obj The JSON representation of the issue.
	 * @return An Issue instance representing the issue or null if the JSON object format is wrong.
	 */
	private Issue makeIssue(JSONObject obj){
		Issue issue = null;
		try{
			issue = new Issue(obj.getString(KEY_ISSUETITLE));
			issue.setStatus(obj.getString(KEY_STATUS));
			issue.setContent(obj.getString(KEY_CONTENT));
			issue.setAssignee(obj.getString(KEY_ASSIGNEE));
		} catch(JSONException e){
			
		}
		return issue;
	}
	
	/**
	 * Reads the JSON string from the message body of the given HTTP response.
	 * @param in The input stream of the HTTP response's message body.
	 * @return The JSON string contained in the given message body.
	 * @throws IOException if an IO error occurs.
	 */
	private String getJSONContent(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder strBuilder = new StringBuilder();
		String input;
		while((input = reader.readLine())!=null){
			strBuilder = strBuilder.append(input);
		}
		reader.close();
		return strBuilder.toString();
	}
	
	/**
	 * Notifies observers on the application's status.
	 */
	private void notifyObservers(String selectedRepo, String selectedIssue){
		assert !selectedRepo.isEmpty() && !selectedIssue.isEmpty();
		Iterator<Observer> itr = observerList.iterator();
		Observer temp;
		while(itr.hasNext()){
			temp = itr.next();
			temp.updateSelectedRepository(selectedRepo);
			if(selectedRepo!=null){
				temp.updateSelectedIssue(selectedIssue);
			}
		}
	}
}
