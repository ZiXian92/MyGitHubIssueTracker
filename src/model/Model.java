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
	//The single instance of this class
	private static Model instance = null;
	
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
		observerList = new ArrayList<Observer>();
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
		
		//Encoding for basic authentication is to be done on username:password.
		String code = new String(Base64.encodeBase64((username+":"+password).getBytes()));
		String url = Constants.API_URL+Constants.EXT_USER;
		try{
			CloseableHttpResponse response = Util.sendGetRequest(url, code);
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
	 * Loads the repositories and issues from GitHub. Clears all current data in the process.
	 * @throws IOException if an error occurred during the request.
	 * @throws RequestException If an error occurs when sending the request.
	 * @throws FailedRequestException If the request fails.
	 * @throws MissingMessageException If the message is missing from the response.
	 * @throws JSONException If an error occurs when parsing the list of repositories.
	 */
	public void initialise() throws IOException, RequestException, FailedRequestException, MissingMessageException, JSONException {
		assert authCode!=null && !authCode.isEmpty();

		//Clears all data members
		repoList = new ArrayList<Repository>();
		indexList = new HashMap<String, Integer>();
		numRepos = 0;
		
		//Send request to get list of repositories.
		String url = Constants.API_URL+Constants.EXT_REPOS;
		HttpGet request = new HttpGet(url);
		request.addHeader(Constants.HEADER_ACCEPT, Constants.VAL_PREVIEWACCEPT);
		request.addHeader(Constants.HEADER_AUTH, String.format(Constants.VAL_AUTH, authCode));
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
				addRepository(temp);
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
		Thread loadContribThread = new Thread(new LoadContributorsThread(repo));
		loadContribThread.run();
		
		//Gets the list of labels used in this repository.
		Thread loadLabelsThread = new Thread(new LoadLabelsThread(repo));
		loadLabelsThread.run();
		
		//Gets list of milestones
		Thread loadMilestonesThread = new Thread(new LoadMilestonesThread(repo));
		loadMilestonesThread.run();
		
		String url = Constants.API_URL+String.format(Constants.EXT_REPOISSUES, owner, repoName);
		
		try{
			CloseableHttpResponse response = Util.sendGetRequest(url, authCode);
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
			
			int size = arr.length();
			ArrayList<Issue> tempIssueList = new ArrayList<Issue>();
			for(int i=0; i<size; i++){	//If JSON exception occurs here, no issue is added to repo.
				temp = arr.getJSONObject(i);
				tempIssueList.add(Issue.makeInstance(temp, repo));
			}
			loadLabelsThread.join();	//Wait for labels to be loaded.
			repo.setIssues(tempIssueList);	//Involves setting of applicable labels to issues
			repo.setIsInitialized(true);
			loadContribThread.join();
			loadMilestonesThread.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch(JSONException e){	//repo has no issue here.
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
	public void updateIssue(Issue issue) throws FailedRequestException, RequestException, MissingMessageException, JSONException{
		assert issue!=null;
		Repository repo = issue.getRepository();
		String url = Constants.API_URL+String.format(Constants.EXT_COMMENTS, repo.getOwner(), repo.getName(), issue.getNumber());
		try{
			CloseableHttpResponse response = Util.sendGetRequest(url, authCode);
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
			issue.setComments(commentArray);
			issue.setIsInitialized(true);
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
			logger.log(Level.WARNING, "Invalid index {0}.", index);
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
		notifyObservers(repo.getFullName(), null);
		return repo;
	}
	
	/**
	 * Gets the repository from the list given the repository name.
	 * @param repoName The full name of the repository to retrieve. Cannot be null or empty.
	 * @return The Repository with the given name or null if the given repository cannot be found.
	 * @throws Exception If an error occurs while updating the repository.
	 */
	public Repository getRepository(String repoName) throws Exception{
		assert repoName!=null && !repoName.isEmpty();
		if(!indexList.containsKey(repoName)){
			logger.log(Level.SEVERE, "Repository {0} not found.", repoName);
			return null;
		}
		return getRepository(indexList.get(repoName));
	}
	
	/**
	 * Gets the specified issue from the given repository.
	 * @param issueName The name of the issue to be selected or the index of the issue in the repository,
	 * 					starting from 1.
	 * @param repoName The full name of the repository that contains the issue to be selected. Cannot be null or empty.
	 * @return The issue with the given issue name from the given repository or null
	 * 			if the repository and/or issue cannot be found. 
	 * @throws Exception If an error occurs while updating the issue.
	 */
	public Issue getIssue(String issueName, String repoName) throws Exception{
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
			//May happen if developer call this method directly without going through the user interface.
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
					updateIssue(issue);
				} catch(Exception e){
					throw new Exception(Constants.ERROR_UPDATEISSUE);
				}
			}
			notifyObservers(repoName, issue.getTitle());
		} else{
			logger.log(Level.SEVERE, "Failed to get issue {0} from repository {1}.",
					new Object[] {issueName, repoName});
		}
		return issue;
	}
	
	/**
	 * Adds the given repository locally only if there is no repository with the same full name.
	 * @param repo The repository to be added.
	 */
	public void addRepository(Repository repo){
		assert repo!=null;
		String fullName = repo.getFullName();
		if(!indexList.containsKey(fullName)){
			repoList.add(repo);
			indexList.put(fullName, ++numRepos);
		}
	}
	
	/**
	 * Adds the given issue(in JSON string format) to the given repository.
	 * @param jsonIssue The JSON representation of the issue to be added. Cannot be null or empty.
	 * @param repoName The name of the repository to add the issue to.
	 * @return The created issue. Returns null if the repository is not found or cannot be updated.
	 * @throws JSONException If an error occurs when parsing the JSON representation of the new issue.
	 * @throws FailedRequestException If the request fails.
	 * @throws MissingMessageException If the message is missing from the response.
	 * @throws RequestException If an error occurred while sending the request.
	 */
	public Issue addIssue(JSONObject jsonIssue, String repoName) throws JSONException, FailedRequestException, MissingMessageException, RequestException {
		assert jsonIssue!=null && repoName!=null && !repoName.isEmpty();
		Repository repo = null;
		try{
			repo = getRepository(repoName);
			if(repo==null){
				return null;
			}
		} catch(Exception e){
			logger.log(Level.SEVERE, e.getMessage());
			return null;
		}
		
		if(!jsonIssue.isNull(Constants.KEY_MILESTONE)){
			int milestoneNumber = repo.getMilestoneNumber(jsonIssue.getString(Constants.KEY_MILESTONE));
			if(milestoneNumber==-1){
				jsonIssue.remove(Constants.KEY_MILESTONE);
			} else{
				jsonIssue.put(Constants.KEY_MILESTONE, milestoneNumber);
			}
		}
		
		String url = Constants.API_URL+String.format(Constants.EXT_REPOISSUES, repo.getOwner(), repo.getName());
		try{
			CloseableHttpResponse response = Util.sendPostRequest(url, authCode, new StringEntity(jsonIssue.toString()));
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_CREATED)){
				logger.log(Level.SEVERE, "Request to add new issue failed. Response: {0}", response.getStatusLine().toString());
				response.close();
				throw new FailedRequestException();
			}
			HttpEntity messageBody = response.getEntity();
			if(messageBody==null){
				response.close();
				logger.log(Level.SEVERE, "Missing response message. Check with Github or restart to confirm creation of issue.");
				throw new MissingMessageException();
			}
			
			//Updates repository with new issue locally.
			JSONObject obj = new JSONObject(Util.getJSONString(messageBody.getContent()));
			response.close();
			Issue issue = Issue.makeInstance(obj, repo);
			repo.addIssue(issue);
			issue.setIsInitialized(true);
			notifyObservers(repoName, issue.getTitle());
			return issue;
		} catch (JSONException e) {
			logger.log(Level.WARNING, "Failed to parse created issue.");
			throw e;
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request to create issue.");
			throw new RequestException();
		}
	}
	
	/**
	 * Edits the given issue with the given changes.
	 * @param changes The JSON object representing the changes to be made.
	 * @param repoName The name of the repository containing the issue to be edited.
	 * @param issueName The name of the issue to be edited.
	 * @return The edited issue. Returns the original issue if the request fails or the response message does not exist.
	 * 			Returns null if the given issue and/or repository cannot be found.
	 * @throws JSONException If an error occurs while parsing the JSON object in the response.
	 * @throws RequestException If an error occurs while sending the request.
	 * @throws FailedRequestException If the request fails
	 * @throws MissingMessageException If the message containing the edited issue is missing from the response.
	 */
	public Issue editIssue(JSONObject changes, String issueName, String repoName) throws JSONException, RequestException, FailedRequestException, MissingMessageException {
		assert changes!=null && repoName!=null && !repoName.isEmpty() && issueName!=null && !issueName.isEmpty();
		
		Repository repo;
		Issue issue = null;
		try{
			issue = getIssue(issueName, repoName);
			if(issue==null){
				return null;
			}
			repo = issue.getRepository();
		} catch(Exception e){
			//Will not happen if this method is called using the workflow.
			logger.log(Level.SEVERE, e.getMessage());
			return null;
		}
		
		if(!changes.isNull(Constants.KEY_MILESTONE)){
			int milestoneNumber = repo.getMilestoneNumber(changes.getString(Constants.KEY_MILESTONE));
			if(milestoneNumber==-1){
				changes.remove(Constants.KEY_MILESTONE);
			}
			changes.put(Constants.KEY_MILESTONE, milestoneNumber);
		}
		
		String url = Constants.API_URL+String.format(Constants.EXT_EDITISSUE, repo.getOwner(), repo.getName(), issue.getNumber());
		try {
			CloseableHttpResponse response = Util.sendPatchRequest(url, authCode, new StringEntity(changes.toString()));
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_OK) || response.getEntity()==null){
				logger.log(Level.SEVERE, "Request to edit issue failed.\nResponse: {0}", response.getStatusLine().toString());
				response.close();
				throw new FailedRequestException();
			}
			HttpEntity messageBody = response.getEntity();	//Will not be null, as defined in GitHub API response.
			if(messageBody==null){
				logger.log(Level.SEVERE, "Missing response message. Check with GitHub or restart to confirm changes.");
				response.close();
				throw new MissingMessageException();
			}
			JSONObject obj = new JSONObject(Util.getJSONString(messageBody.getContent()));
			response.close();
			Issue editedIssue = Issue.makeInstance(obj, repo);
			editedIssue.setIsInitialized(true);
			repo.replaceIssue(issue.getTitle(), editedIssue);
			notifyObservers(repoName, editedIssue.getTitle());
			return editedIssue;
		} catch(JSONException e){
			logger.log(Level.WARNING, "Failed to parse edited issue from JSON in response message. Check GitHub to confirm changes.");
			throw e;
		} catch(IOException e){	//If error occurs during request.
			logger.log(Level.SEVERE, "Failed to execute request to edit issue.");
			throw new RequestException();
		}
	}
	
	/**
	 * Adds the given comment to the given issue.
	 * @param comment The JSON representation of comment to add.
	 * @param issueName The name of the issue to add comment to.
	 * @param repoName The name of the repository holding the issue to be commented on.
	 * @return The edited issue with the added comment.
	 * @throws FailedRequestException If the request was unsuccessful.
	 * @throws MissingMessageException If the response does not have any message.
	 * @throws RequestException If an error occurred while executing the request.
	 * @throws JSONException If an error occurs while parsing the newly created comment.
	 */
	public Issue addComment(JSONObject comment, String issueName, String repoName) throws FailedRequestException, MissingMessageException, RequestException, JSONException{
		assert comment!=null && issueName!=null && !issueName.isEmpty() && repoName!=null && !repoName.isEmpty();
		Issue issue = null;
		Repository repo;
		
		try{
			issue = this.getIssue(issueName, repoName);
			if(issue==null){
				return null;
			}
			repo = issue.getRepository();
		} catch(Exception e){
			//Will not happen if called through program's workflow.
			logger.log(Level.SEVERE, e.getMessage());
			return null;
		}
		
		String url = Constants.API_URL+String.format(Constants.EXT_ISSUECOMMENTS, repo.getOwner(), repo.getName(), issue.getNumber());
		try{
			CloseableHttpResponse response = Util.sendPostRequest(url, authCode, new StringEntity(comment.toString()));
			if(!response.getStatusLine().toString().equals(Constants.RESPONSE_CREATED)){
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
			throw new JSONException(Constants.ERROR_UPDATELOCALDATA);
		} catch(IOException e){
			logger.log(Level.SEVERE, "Failed to execute request to add comment to issue.");
			throw new RequestException();
		}
	}
}
