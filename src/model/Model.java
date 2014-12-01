package model;

import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

public class Model {
    private static final String HEADER_ACCEPT = "Accept";
    private static final String VAL_ACCEPT = "application/vnd.github.v3+json";
    private static final String HEADER_AUTH = "Authorization";
    private static final String VAL_AUTH = "Basic %1$s";
    private static final String RESPONSE_OK = "HTTP/1.1 200 OK";
    
    private static Model instance = null;
    
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
     */
    public boolean loginUser(String username, String password) throws IOException {
	HttpGet request = new HttpGet("https://api.github.com/users/"+username);
	request.addHeader(HEADER_ACCEPT, VAL_ACCEPT);
	String encodedPassword = new String(Base64.encodeBase64((username+":"+password).getBytes()));
	request.addHeader(HEADER_AUTH, String.format(VAL_AUTH, encodedPassword));
	CloseableHttpResponse response = HttpClients.createDefault().execute(request);
	return response.getStatusLine().toString().equals(RESPONSE_OK);
    }
}
