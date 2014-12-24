package Misc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Util {
	/**
	 * Reads the JSON string from the message body of the given HTTP response.
	 * @param in The input stream of the HTTP response's message body. Cannot be null.
	 * @return The JSON string contained in the given message body or whatever is read if an error occurs.
	 */
	public static String getJSONString(InputStream in){
		assert in!=null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder strBuilder = new StringBuilder();
		String input;
		try{
			while((input = reader.readLine())!=null){
				strBuilder = strBuilder.append(input);
			}
			reader.close();
		} catch(IOException e){
			
		}
		return strBuilder.toString();
	}
}
