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
	private static final String HEADER_AUTH = "Authorization";
	private static final String VAL_AUTH = "Basic %1$s";

	private static final String RESPONSE_OK = "HTTP/1.1 200 OK";

	private static final String KEY_REPONAME = "name";
	private static final String KEY_ISSUETITLE = "title";

	private static Model instance = null;

	//Data members
	private String authCode, username;
	private ArrayList<Repository> repoList;

	private Model(){
		repoList = new ArrayList<Repository>();
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
	 * Authenticates the user with the given username with GitHub API.
	 * @param username The username of the user to log in.
	 * @return true if authentication is successful and false otherwise.
	 * @throws IOException if an IO error occurs during the request.
	 */
	public boolean loginUser(String username, String password) throws IOException {
		if(username==null || username.isEmpty() || password==null || password.isEmpty()){
			return false;
		}
		this.username = username;
		HttpGet request = new HttpGet(API_URL+EXT_USER);
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);

		//Encoding for basic authentication is to be done on username:password.
		authCode = new String(Base64.encodeBase64((username+":"+password).getBytes()));
		request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));

		CloseableHttpResponse response = HttpClients.createDefault().execute(request);
		return response.getStatusLine().toString().equals(RESPONSE_OK);
	}

	/**
	 * Loads the repositories and issues from GitHub.
	 * @throws IOException if an IO error occurs during the request.
	 */
	public void initialise() throws IOException {
		assert username!=null && !username.isEmpty();
		
		//Send request to get list of repositories.
		HttpGet request = new HttpGet(API_URL+EXT_REPOS);
		request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
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
			int size = arr.length();
			for(int i=0; i<size; i++){	//Add repository to list.
				repoList.add(makeRepository(arr.getJSONObject(i).getString(KEY_REPONAME)));
			}
		} catch(JSONException e){
		
		} finally{
			response.close();
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
		return list;
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
	 * Fetches issues under the specified repository and stores them in
	 * a Repository instance.
	 * @param repo The name of the repository whose issues are to be fetched.
	 * @return A Repository representing the specified GitHub repository.
	 * @throws IOException when IO error occurs during the request.
	 */
	public Repository makeRepository(String repoName) throws IOException {
		assert username!=null && !username.isEmpty();
		HttpGet request = new HttpGet(API_URL+String.format(EXT_REPOISSUES, username, repoName));
		CloseableHttpResponse response = HttpClients.createDefault().execute(request);
		if(!response.getStatusLine().toString().equals(RESPONSE_OK)){
			response.close();
			return null;
		}
		Repository repo = new Repository(repoName);
		HttpEntity messageBody = response.getEntity();
		if(messageBody!=null){
			try{
				JSONObject temp;
				JSONArray arr = new JSONArray(getJSONContent(messageBody.getContent()));
				int size = arr.length();
				for(int i=0; i<size; i++){
					temp = arr.getJSONObject(i);
					repo.addIssue(makeIssue(temp));
				}
			} catch(JSONException e){
				
			} finally{
				response.close();
			}
		}
		response.close();
		return repo;
	}
	
	/**
	 * Creates an Issue from the given JSON representation.
	 * @param obj The JSON representation of the issue.
	 * @return An Issue instance representing the issue or null if the JSON object format is wrong.*/
	private Issue makeIssue(JSONObject obj){
		try{
			Issue issue = new Issue(obj.getString(KEY_ISSUETITLE));
			return issue;
		} catch(JSONException e){
			return null;
		}
	}
}
