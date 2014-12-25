package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Misc.Util;
import controller.Observer;
import structure.Issue;
import structure.Repository;

/**
 * Defines the component that manages the in-memory storage of GitHub issues,
 * as well as synchronizing data with GitHub.
 * @author ZiXian92
 */
public class Model {
	//API URL and extensions
	private static final String API_URL = "https://api.github.com";
	private static final String EXT_USER = "/user";
	private static final String EXT_REPOS = "/user/repos";
	private static final String EXT_REPOISSUES = "/repos/%1$s/%2$s/issues";
	private static final String EXT_REPOLABELS = "/repos/%1$s/%2$s/labels";
	private static final String EXT_CONTRIBUTORS = "/repos/%1$s/%2$s/contributors";
	private static final String EXT_EDITISSUE = "/repos/%1$s/%2$s/issues/%3$d";

	//Request headers and values
	private static final String HEADER_ACCEPT = "Accept";
	private static final String VAL_ACCEPT = "application/vnd.github.v3+json";
	private static final String VAL_PREVIEWACCEPT = "application/vnd.github.moondragon-preview+json";
	private static final String HEADER_AUTH = "Authorization";
	private static final String VAL_AUTH = "Basic %1$s";

	//HTTP response
	private static final String RESPONSE_OK = "HTTP/1.1 200 OK";
	private static final String RESPONSE_CREATED = "HTTP/1.1 201 Created";

	//Error messages
	private static final String MSG_CONNECTIONERROR = "Error executing request. Connect to the Internet and try again.";
	private static final String MSG_LOCALISSUEPARSINGERROR = "Failed to create local instance issue. You may want to restart the program.";
	private static final String MSG_REQUESTERROR = "An error occurred while trying to send request. Please try again.";
	
	private static Model instance = null;	//The single instance of this class
	
	//For logging
	private static final Logger logger = Logger.getLogger("com.MyGitHubIssueTracker.model");

	//Data members
	private String authCode;
	private ArrayList<Repository> repoList;
	private ArrayList<Observer> observerList;
	private int numRepos;
	
	//Stores the indices of the repositories in repoList, starting from 1.
	private HashMap<String, Integer> indexList;

	private Model(){
		repoList = new ArrayList<Repository>();
		observerList = new ArrayList<Observer>();
		indexList = new HashMap<String, Integer>();
		numRepos = 0;
		logger.setUseParentHandlers(true);
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

		try{
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			String responseStatus = response.getStatusLine().toString();
			response.close();
			if(responseStatus.equals(RESPONSE_OK)){
				this.authCode = code;
				return true;
			}
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute authentication request.");
			throw new IOException(MSG_CONNECTIONERROR);
		}
		return false;
	}

	/**
	 * Loads the repositories and issues from GitHub.
	 * Best effort to fetch and parse all data. If an error occurs while parsing an entity, the current entity
	 * is dropped. Probability of this happening is low as the JSON object is produced by GitHub API.
	 * @throws IOException if an error occurred during the request.
	 */
	public void initialise() throws IOException {
		assert authCode!=null && !authCode.isEmpty();

		//Send request to get list of repositories.
		HttpGet request = new HttpGet(API_URL+EXT_REPOS);
		request.addHeader(HEADER_ACCEPT, VAL_PREVIEWACCEPT);
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		try{
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_OK) || response.getEntity()==null){
				response.close();
				return;
			}

			//Get the message body of the response.
			HttpEntity messageBody = response.getEntity();

			//Parse the JSON string into Repository instances.
			JSONArray arr = new JSONArray(Util.getJSONString(messageBody.getContent()));
			response.close();
			int size = arr.length();
			Repository temp;
			JSONObject obj;
			for(int i=0; i<size; i++){	//Add repository to list.
				try{
					obj = arr.getJSONObject(i);
					temp = makeRepository(obj);
					if(temp!=null){
						repoList.add(temp);
						indexList.put(temp.getName(), ++numRepos);
					}
				} catch(JSONException e){
					logger.log(Level.WARNING, "Failed to parse a repository.");
				}
			}
		} catch(JSONException e){
			logger.log(Level.SEVERE, "Failed to parse response message.");
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request for repositories.");
			throw new IOException(MSG_REQUESTERROR);
		}
	}
	
	/**
	 * Gets the list of repositories that the current user is involved in.
	 * @return The list of names of repositories that the current user is involved in or null if the list is empty.
	 */
	public String[] listRepositories(){
		if(numRepos==0){
			return null;
		}
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
	 * @return A Repository representing the specified GitHub repository. Returns null if any error
	 * 			occurs when executing the requests or parsing the response objects.
	 */
	public Repository makeRepository(JSONObject obj){
		assert obj!=null;
		
		try{
			Repository repo = Repository.makeInstance(obj);
			String repoName = repo.getName();
			String owner = repo.getOwner();
			
			//Gets the list of contributors concurrently.
			HttpGet contributorRequest = new HttpGet(API_URL+String.format(EXT_CONTRIBUTORS, owner, repoName));
			contributorRequest.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
			contributorRequest.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
			Thread loadContribThread = new Thread(new LoadContributorsThread(repo, contributorRequest));
			loadContribThread.run();
			
			HttpGet labelsRequest = new HttpGet(API_URL+String.format(EXT_REPOLABELS, owner, repoName));
			labelsRequest.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
			labelsRequest.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
			Thread loadLabelsThread = new Thread(new LoadLabelsThread(repo, labelsRequest));
			loadLabelsThread.run();
			
			//Sends request for issues under this repository.
			HttpGet issueRequest = new HttpGet(API_URL+String.format(EXT_REPOISSUES, owner, repoName));
			issueRequest.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
			issueRequest.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
			CloseableHttpResponse response = HttpClients.createDefault().execute(issueRequest);
			if(!response.getStatusLine().toString().equals(RESPONSE_OK) || response.getEntity()==null){
				response.close();
				return null;
			}
			
			//Loads issues from GitHub repository into this repository instance.
			HttpEntity messageBody = response.getEntity();
			JSONObject temp;
			JSONArray arr = new JSONArray(Util.getJSONString(messageBody.getContent()));
			response.close();
			loadLabelsThread.join();
			int size = arr.length();
			for(int i=0; i<size; i++){
				temp = arr.getJSONObject(i);
				repo.addIssue(Issue.makeInstance(temp));
			}
			loadContribThread.join();
			return repo;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			return null;
		} catch(Exception e){
			return null;
		}
		
	}
	
	/**
	 * Notifies observers on the application's status.
	 */
	private void notifyObservers(String selectedRepo, String selectedIssue){
		if(selectedRepo!=null){
			assert !selectedRepo.isEmpty();
		}
		if(selectedIssue!=null){
			assert !selectedIssue.isEmpty();
		}
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
	
	/**
	 * Gets the repository based on its index in the list.
	 * @param index An integer between 1 and the number of repositories in the list.
	 * @return The index-th Repository instance in the list or null if index is invalid.
	 */
	public Repository getRepository(int index){
		if(index<1 || index>repoList.size()){
			return null;
		}
		Repository selectedRepo = repoList.get(index-1);
		notifyObservers(selectedRepo.getName(), null);
		return selectedRepo;
	}
	
	/**
	 * Gets the repository from the list given the repository name.
	 * @param repoName The name of the repository to retrieve. Cannot be null or empty.
	 * @return The Repository with the given name or null if the given repository cannot be found.
	 */
	public Repository getRepository(String repoName){
		if(repoName==null || repoName.isEmpty() || !indexList.containsKey(repoName)){
			return null;
		}
		return getRepository(indexList.get(repoName));
	}
	
	/**
	 * Gets the specified issue from the given repository.
	 * @param issueName The name of the issue to be selected or the index of the issue in the repository,
	 * 					starting from 1.
	 * @param repoName The name of the repository that contains the issue to be selected.
	 * @return The issue with the given issue name from the given repository or null
	 * 			if the repository and/or issue cannot be found. 
	 */
	public Issue getIssue(String issueName, String repoName){
		if(issueName==null || issueName.isEmpty() || repoName==null || repoName.isEmpty()){
			return null;
		}
		Repository repo = getRepository(repoName);
		if(repo==null){
			return null;
		}
		Issue selectedIssue;
		try{
			selectedIssue = repo.getIssue(Integer.parseInt(issueName));
		} catch(NumberFormatException e){
			selectedIssue = repo.getIssue(issueName);
		}
		if(selectedIssue!=null){
			notifyObservers(repoName, selectedIssue.getTitle());
		}
		return selectedIssue;
	}
	
	/**
	 * Adds the given issue(in JSON string format) to the given repository.
	 * @param jsonIssue The JSON representation of the issue to be added. Cannot be null or empty.
	 * @param repoName The name of the repository to add the issue to.
	 * @return The created issue. Returns null if the request fails or an error occurred during the request..
	 * @throws JSONException If an error occurs when parsing the JSON representation of the new issue.
	 */
	public Issue addIssue(JSONObject jsonIssue, String repoName) throws JSONException {
		assert jsonIssue!=null && repoName!=null && !repoName.isEmpty();
		Repository repo = getRepository(repoName);
		HttpPost request = new HttpPost(API_URL+String.format(EXT_REPOISSUES, repo.getOwner(), repo.getName()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		try{
			request.setEntity(new StringEntity(jsonIssue.toString()));
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_CREATED) || response.getEntity()==null){
				response.close();
				return null;
			}
			HttpEntity messageBody = response.getEntity();
			JSONObject obj = new JSONObject(Util.getJSONString(messageBody.getContent()));
			response.close();
			Issue issue = Issue.makeInstance(obj);
			repo.addIssue(issue);
			notifyObservers(repoName, issue.getTitle());
			return issue;
		} catch (JSONException e) {
			throw new JSONException(MSG_LOCALISSUEPARSINGERROR);
		} catch(IOException e){
			return null;
		}
	}
	
	/**
	 * Closes the given issue from the given repository.
	 * Does nothing if issueName and/or repoName are invalid or represent non-existent items,
	 * an error occurs during parsing or request, or if the request is unsuccessful.
	 * @param issueName The name or 1-based index of the issue in the given repository's list of issues.
	 * @param repoName The name of the repository to close the issue.
	 */
	public void closeIssue(String issueName, String repoName){
		if(issueName==null || issueName.isEmpty() || repoName==null || repoName.isEmpty()){
			return;
		}
		Repository repo = getRepository(repoName);
		if(repo==null){
			return;
		}
		Issue issue, temp;
		try{
			issue = repo.getIssue(Integer.parseInt(issueName));
		} catch(NumberFormatException e){
			issue = repo.getIssue(issueName);
		}
		if(issue==null){
			return;
		}
		
		HttpPatch request = new HttpPatch(API_URL+String.format(EXT_EDITISSUE, repo.getOwner(), repo.getName(), issue.getNumber()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		temp = new Issue(issue);
		temp.close();
		
		try {
			JSONObject obj = temp.toJSONObject();
			request.setEntity(new StringEntity(obj.toString()));
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(response.getStatusLine().toString().equals(RESPONSE_OK)){
				issue.close();
			}
			response.close();
		} catch (JSONException e) {
			//Log error
		} catch(IOException e){
			//Log error
		}
	}
	
	/**
	 * Edits the given issue with the given changes.
	 * @param changes The JSON object representing the changes to be made.
	 * @param repoName The name of the repository containing the issue to be edited.
	 * @param issueName The name of the issue to be edited.
	 * @return The edited issue. Returns the original issue if the request fails or the response message does not exist.
	 * 			Returns null if the given issue and/or repository cannot be found.
	 * @throws JSONException if an error occurs while parsing the JSON object in the response.
	 */
	public Issue editIssue(JSONObject changes, String repoName, String issueName) throws JSONException {
		assert changes!=null && repoName!=null && !repoName.isEmpty() && issueName!=null && !issueName.isEmpty();
		
		Repository repo = getRepository(repoName);
		if(repo==null){
			return null;
		}
		Issue issue = repo.getIssue(issueName);
		if(issue==null){
			return null;
		}
		
		HttpPatch request = new HttpPatch(API_URL+String.format(EXT_EDITISSUE, repo.getOwner(), repo.getName(), issue.getNumber()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		try {
			request.setEntity(new StringEntity(changes.toString()));
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_OK) || response.getEntity()==null){
				response.close();
				return issue;
			}
			HttpEntity messageBody = response.getEntity();	//Will not be null, as defined in GitHub API response.
			JSONObject obj = new JSONObject(Util.getJSONString(messageBody.getContent()));
			response.close();
			Issue editedIssue = Issue.makeInstance(obj);
			repo.replaceIssue(issue.getTitle(), editedIssue);
			notifyObservers(repoName, editedIssue.getTitle());
			return editedIssue;
		} catch(IOException e){	//If error occurs during request.
			return issue;
		}
	}
}
