package model;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

/**
 * Defines the component that manages the in-memory storage of GitHub issues,
 * as well as synchronizing data with GitHub.
 * @author ZiXian92
 */
public class Model {
    private static final String API_URL = "https://api.github.com";
    private static final String HEADER_ACCEPT = "Accept";
    private static final String VAL_ACCEPT = "application/vnd.github.v3+json";
    private static final String HEADER_AUTH = "Authorization";
    private static final String VAL_AUTH = "Basic %1$s";
    private static final String RESPONSE_OK = "HTTP/1.1 200 OK";
    
    private static Model instance = null;
    
    //Data members
    private String authCode;
    
    private Model(){
	
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
	HttpGet request = new HttpGet(API_URL+"/user");
	request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
	
	//Encoding for basic authentication is to be done on username:password.
	authCode = new String(Base64.encodeBase64((username+":"+password).getBytes()));
	request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
	
	CloseableHttpResponse response = HttpClients.createDefault().execute(request);
	return response.getStatusLine().toString().equals(RESPONSE_OK);
    }
    
    /**
     * Returns the list of repository names that the logged in user works on.
     * @return An array of the names of the repositories under the logged in user or
     * 		null if the array is empty or the request was not successful.
     * @throws IOException if an IO error occurs during the request.
     */
    public String[] listProjects() throws IOException {
	HttpGet request = new HttpGet(API_URL+"/user/repos");
	request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
	request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, authCode));
	CloseableHttpResponse response = HttpClients.createDefault().execute(request);
	if(!response.getStatusLine().toString().equals(RESPONSE_OK)){
	    return null;
	}
    }
}
