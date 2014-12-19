package structure;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * JUnit test clsss to test Issue class.
 * @author ZiXian92
 */
public class IssueTest {
	@Test
	public void testMakeInstance() throws IOException, JSONException {
		File file = new File("testFiles/issue7");
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String input;
		StringBuilder strBuilder = new StringBuilder();
		while((input = reader.readLine())!=null){
			strBuilder = strBuilder.append(input);
		}
		reader.close();
		JSONObject obj = new JSONObject(strBuilder.toString());
		Issue issue = Issue.makeInstance(obj);
		assertTrue(issue!=null);
		assertEquals(7, issue.getNumber());
		assertEquals("ZiXian92", issue.getAssignee());
	}

}
