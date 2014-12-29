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

import Misc.Constants;
import Misc.FailedRequestException;
import Misc.MissingMessageException;
import Misc.RequestException;
import Misc.Util;
import controller.Observer;
import structure.Issue;
import structure.Repository;

/**
 * Defines the component that manages the in-memory storage of GitHub issues,
 * as well as synchronizing data with GitHub. There is only 1 instance of Model for the entire session.
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
	private static final String EXT_ISSUECOMMENTS = "/repos/%1$s/%2$s/issues/%3$d/comments";
	private static final String EXT_COMMENTS = "/repos/%1$s/%2$s/issues/comments/%3$d";

	//Request headers and values
	private static final String HEADER_ACCEPT = "Accept";
	private static final String VAL_ACCEPT = "application/vnd.github.v3+json";
	private static final String VAL_PREVIEWACCEPT = "application/vnd.github.moondragon-preview+json";
	private static final String HEADER_AUTH = "Authorization";
	private static final String VAL_AUTH = "Basic %1$s";

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
	 * @return The only instance of this class.
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
	 * @param password The password for the givne username.
	 * @return true if authentication is successful and false otherwise.
	 * @throws RequestException If an error occurs while connecting to GitHub API.
	 */
	public boolean loginUser(String username, String password) throws RequestException {
		assert username!=null && password!=null;
		if(username.isEmpty() || password.isEmpty()){
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
			if(responseStatus.equals(Constants.RESPONSE_OK)){
				this.authCode = code;
				return true;
			}
			return false;
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute authentication request.");
			throw new RequestException();
		}
	}

	/**
	 * Loads the repositories and issues from GitHub.
	 * Best effort to fetch and parse all data. If an error occurs while parsing an entity, the current entity
	 * is dropped. Probability of this happening is low as the JSON object is produced by GitHub API.
	 * @throws IOException if an error occurred during the request.
	 * @throws RequestException If an error occurs when sending the request.
	 * @throws FailedRequestException If the request fails.
	 * @throws MissingMessageException If the message is missing from the response.
	 * @throws JSONException If an error occurs when parsing the list of repositories.
	 */
	public void initialise() throws IOException, RequestException, FailedRequestException, MissingMessageException, JSONException {
		assert authCode!=null && !authCode.isEmpty();

		//Send request to get list of repositories.
		HttpGet request = new HttpGet(API_URL+EXT_REPOS);
		request.addHeader(HEADER_ACCEPT, VAL_PREVIEWACCEPT);
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		try{
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_OK)){
				logger.log(Level.SEVERE, "Initialization failed.\n Response: {0}", response.getStatusLine().toString());
				response.close();
				throw new FailedRequestException();
			}

			//Get the message body of the response.
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Request successful. Response message missing.");
				response.close();
				throw new MissingMessageException();
			}

			//Parse the JSON string into Repository instances.
			JSONArray arr = new JSONArray(Util.getJSONString(messageBody.getContent()));
			response.close();
			int size = arr.length();
			Repository temp;
			JSONObject obj;
			for(int i=0; i<size; i++){	//Add repository to list.
				obj = arr.getJSONObject(i);
				temp = Repository.makeInstance(obj);
				repoList.add(temp);
				indexList.put(temp.getName(), ++numRepos);
			}
		} catch(JSONException e){
			logger.log(Level.SEVERE, "Failed to parse response message.");
			throw new JSONException(Constants.ERROR_INITIALIZEDATA);
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request for repositories.");
			throw new RequestException();
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
	 * @param repo The repository to update from GitHub.
	 * @return A Repository representing the specified GitHub repository. Returns null if any error
	 * 			occurs when executing the requests or parsing the response objects.
	 * @throws FailedRequestException If the request fails.
	 * @throws MissingMessageException If the message is missing in the response.
	 * @throws JSONException If an error occurs when parsing the response object.
	 * @throws RequestException If an error occurs when sending the request.
	 */
	public void updateRepo(Repository repo) throws FailedRequestException, MissingMessageException, JSONException, RequestException{
		assert repo!=null;
		
		String repoName = repo.getName();
		String owner = repo.getOwner();
		
		//Gets the list of contributors concurrently.
		/*HttpGet contributorRequest = new HttpGet(API_URL+String.format(EXT_CONTRIBUTORS, owner, repoName));
		contributorRequest.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		contributorRequest.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		Thread loadContribThread = new Thread(new LoadContributorsThread(repo, contributorRequest));
		loadContribThread.run();*/
		
		//Gets the list of labels used in this repository.
		/*HttpGet labelsRequest = new HttpGet(API_URL+String.format(EXT_REPOLABELS, owner, repoName));
		labelsRequest.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		labelsRequest.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		Thread loadLabelsThread = new Thread(new LoadLabelsThread(repo, labelsRequest));
		loadLabelsThread.run();*/
		
		//Sends request for issues under this repository.
		HttpGet issueRequest = new HttpGet(API_URL+String.format(EXT_REPOISSUES, owner, repoName));
		issueRequest.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		issueRequest.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		try{
			CloseableHttpResponse response = HttpClients.createDefault().execute(issueRequest);
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_OK)){
				logger.log(Level.WARNING, "Failed to get issues for repository {0}.\nResponse: {1}",
						new Object[] {repoName, response.getStatusLine().toString()});
				response.close();
				throw new FailedRequestException();
			}
			
			//Loads issues from GitHub repository into this repository instance.
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Request successful. Response message missing.");
				response.close();
				throw new MissingMessageException();
			}
			JSONObject temp;
			JSONArray arr = new JSONArray(Util.getJSONString(messageBody.getContent()));
			response.close();
			//loadLabelsThread.join();	//Wait for labels to be loaded.
			int size = arr.length();
			ArrayList<Issue> tempIssueList = new ArrayList<Issue>();
			for(int i=0; i<size; i++){	//If JSON exception occurs here, no issue is added to repo.
				temp = arr.getJSONObject(i);
				tempIssueList.add(Issue.makeInstance(temp, repo));
			}
			repo.setIssues(tempIssueList);
			repo.setIsInitialized(true);
			//loadContribThread.join();
		} /*catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}*/ catch(JSONException e){	//repo has no issue here.
			logger.log(Level.SEVERE, "Failed to parse JSON object(s)");
			throw e;
		} catch(IOException e){	//repo has no issue here.
			logger.log(Level.SEVERE, "Failed to execute request for issues of {0}.", repoName);
			throw new RequestException();
		}
	}
	
	/**
	 * Completes the update of the given issue.
	 * @param issue The issue to fetch comments for.
	 * @throws FailedRequestException If the request fails.
	 * @throws RequestException If an error occurred when sending the request.
	 * @throws MissingMessageException If the JSON contents are missing from the response.
	 * @throws JSONException If an error occurred when parsing the response JSON object.
	 */
	public void updateIssue(Issue issue, Repository repo) throws FailedRequestException, RequestException, MissingMessageException, JSONException{
		assert repo!=null && issue!=null;
		HttpGet request = new HttpGet(API_URL+String.format(EXT_COMMENTS, repo.getOwner(), repo.getName(), issue.getNumber()));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		try{
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_OK)){
				logger.log(Level.WARNING, "Failed to get comments.");
				response.close();
				throw new FailedRequestException();
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Request successful. Response message missing.");
				response.close();
				throw new MissingMessageException();
			}
			JSONArray commentArray = new JSONArray(Util.getJSONString(messageBody.getContent()));
			issue.addComments(commentArray);
		} catch(JSONException e){
			logger.log(Level.SEVERE, "Failed to parse JSON object(s)");
			throw e;
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request for comments.");
			throw new RequestException();
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
	 * Gets the repository based on its index in the list. Updates with information from GitHub on first access.
	 * @param index An integer between 1 and the number of repositories in the list.
	 * @return The index-th Repository instance in the list or null if index is invalid.
	 * @throws Exception If an error occurred while updating the repository.
	 */
	public Repository getRepository(int index) throws Exception{
		if(index<1 || index>repoList.size()){
			return null;
		}
		Repository repo = repoList.get(index-1);
		if(!repo.isInitialized()){
			try{
				updateRepo(repo);
			} catch(Exception e){
				throw new Exception(Constants.ERROR_UPDATEREPO);
			}
		}
		notifyObservers(repo.getName(), null);
		return repo;
	}
	
	/**
	 * Gets the repository from the list given the repository name.
	 * @param repoName The name of the repository to retrieve. Cannot be null or empty.
	 * @return The Repository with the given name or null if the given repository cannot be found.
	 * @throws Exception 
	 */
	public Repository getRepository(String repoName) throws Exception{
		assert repoName!=null && !repoName.isEmpty();
		if(!indexList.containsKey(repoName)){
			return null;
		}
		return getRepository(indexList.get(repoName));
	}
	
	/**
	 * Gets the specified issue from the given repository.
	 * @param issueName The name of the issue to be selected or the index of the issue in the repository,
	 * 					starting from 1.
	 * @param repoName The name of the repository that contains the issue to be selected. Cannot be null or empty.
	 * @return The issue with the given issue name from the given repository or null
	 * 			if the repository and/or issue cannot be found. 
	 */
	public Issue getIssue(String issueName, String repoName){
		assert issueName!=null && !issueName.isEmpty() && repoName!=null && !repoName.isEmpty();
		Repository repo = null;
		try{
			repo = getRepository(repoName);
			if(repo==null){
				logger.log(Level.SEVERE, "Failed to get repository {0}.", repoName);
				return null;
			}
		} catch(Exception e){
			//This will not happen as user is unable to select issue when repository cannot be selected.
			return null;
		}
		
		Issue issue;
		try{
			issue = repo.getIssue(Integer.parseInt(issueName));
		} catch(NumberFormatException e){
			issue = repo.getIssue(issueName);
		}
		if(issue!=null){
			if(!issue.isInitialized()){
				try{
					updateIssue(issue, repo);
					issue.setIsInitialized(true);
				} catch(Exception e){
					throw new Exception(Constants.ERROR_UPDATEISSUE);
				}
			}
			notifyObservers(repoName, issue.getTitle());
		}
		return issue;
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
		if(repo==null){
			logger.log(Level.SEVERE, "Failed to get repository {0}.", repoName);
			return null;
		}
		HttpPost request = new HttpPost(API_URL+String.format(EXT_REPOISSUES, repo.getOwner(), repo.getName()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		try{
			request.setEntity(new StringEntity(jsonIssue.toString()));
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_CREATED)){
				logger.log(Level.SEVERE, "Request to add new issue failed. Response: {0}", response.getStatusLine().toString());
				response.close();
				return null;
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				response.close();
				logger.log(Level.SEVERE, "Missing response message. Check with Github or restart to confirm creation of issue.");
				return null;
			}
			JSONObject obj = new JSONObject(Util.getJSONString(messageBody.getContent()));
			response.close();
			Issue issue = Issue.makeInstance(obj);
			repo.addIssue(issue);
			notifyObservers(repoName, issue.getTitle());
			return issue;
		} catch (JSONException e) {
			logger.log(Level.WARNING, "Failed to parse created issue.");
			throw new JSONException(MSG_LOCALISSUEPARSINGERROR);
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request to create issue.");
			return null;
		}
	}
	
	/**
	 * Closes the given issue from the given repository.
	 * Does nothing if issueName and/or repoName are represent non-existent items,
	 * an error occurs during parsing or request, or if the request is unsuccessful.
	 * @param issueName The name or 1-based index of the issue in the given repository's list of issues.
	 * @param repoName The name of the repository to close the issue.
	 */
	public void closeIssue(String issueName, String repoName){
		assert issueName!=null && !issueName.isEmpty() && repoName!=null && !repoName.isEmpty();
		Repository repo = getRepository(repoName);
		if(repo==null){
			logger.log(Level.SEVERE, "Unable to find repository {0}.", repoName);
			return;
		}
		Issue issue, temp;
		try{
			issue = repo.getIssue(Integer.parseInt(issueName));
		} catch(NumberFormatException e){
			issue = repo.getIssue(issueName);
		}
		if(issue==null){
			logger.log(Level.SEVERE, "Unable to find issue {0}.", issueName);
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
			} else{
				logger.log(Level.WARNING, "Request to close issue {0} failed.", issue.getTitle());
			}
			response.close();
		} catch (JSONException e) {
			logger.log(Level.WARNING, "Failed to parse issue into JSON.");
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request to close issue.");
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
			logger.log(Level.SEVERE, "Failed to get repository {0}.", repoName);
			return null;
		}
		Issue issue = repo.getIssue(issueName);
		if(issue==null){
			logger.log(Level.SEVERE, "Failed to get issue {0}.", issueName);
			return null;
		}
		
		HttpPatch request = new HttpPatch(API_URL+String.format(EXT_EDITISSUE, repo.getOwner(), repo.getName(), issue.getNumber()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		try {
			request.setEntity(new StringEntity(changes.toString()));
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_OK) || response.getEntity()==null){
				logger.log(Level.SEVERE, "Request to edit issue failed.\nResponse: {0}", response.getStatusLine().toString());
				response.close();
				return issue;
			}
			HttpEntity messageBody = response.getEntity();	//Will not be null, as defined in GitHub API response.
			if(messageBody==null){
				logger.log(Level.SEVERE, "Missing response message. Check with GitHub or restart to confirm changes.");
				response.close();
				return null;
			}
			JSONObject obj = new JSONObject(Util.getJSONString(messageBody.getContent()));
			response.close();
			Issue editedIssue = Issue.makeInstance(obj);
			repo.replaceIssue(issue.getTitle(), editedIssue);
			notifyObservers(repoName, editedIssue.getTitle());
			return editedIssue;
		} catch(JSONException e){
			logger.log(Level.WARNING, "Failed to parse edited issue from JSON in response message. Check GitHub to confirm changes.");
			throw e;
		} catch(IOException e){	//If error occurs during request.
			logger.log(Level.SEVERE, "Failed to execute request to edit issue.");
			return issue;
		}
	}
	
	/**
	 * Adds the given comment to the given issue.
	 * @param comment The JSON representation of comment to add.
	 * @param issueName The name of the issue to add comment to.
	 * @param repoName The name of the repository holding the issue to be commented on.
	 * @throws FailedRequestException If the request was unsuccessful.
	 * @throws MissingMessageException If the response does not have any message.
	 * @throws RequestException If an error occurred while executing the request.
	 * @throws JSONException If an error occurs while parsing the newly created comment.
	 */
	public Issue addComment(JSONObject comment, String issueName, String repoName) throws FailedRequestException, MissingMessageException, RequestException, JSONException{
		assert comment!=null && issueName!=null && !issueName.isEmpty() && repoName!=null && !repoName.isEmpty();
		Repository repo = getRepository(repoName);
		if(repo==null){
			return null;
		}
		Issue issue = repo.getIssue(issueName);
		if(issue==null){
			return null;
		}
		HttpPost request = new HttpPost(API_URL+String.format(EXT_ISSUECOMMENTS, repo.getOwner(), repo.getName(), issue.getNumber()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
		try{
			request.setEntity(new StringEntity(comment.toString()));
			CloseableHttpResponse response = HttpClients.createDefault().execute(request);
			if(!response.getStatusLine().toString().equals(RESPONSE_CREATED)){
				logger.log(Level.WARNING, "Request to comment issue failed.Response: {0}", response.getStatusLine().toString());
				response.close();
				throw new FailedRequestException();
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				logger.log(Level.WARNING, "Request successful. Response message missing.");
				response.close();
				throw new MissingMessageException();
			}
			comment = new JSONObject(Util.getJSONString(messageBody.getContent()));
			issue.addComment(comment);
			return issue;
		}  catch (JSONException e) {
			logger.log(Level.WARNING, "Failed to parse comment from JSON in response message. Check GitHub to confirm changes.");
			throw new JSONException(Constants.ERROR_UPDATELOCALCOPY);
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request to add comment to issue.");
			throw new RequestException();
		}
	}
}
