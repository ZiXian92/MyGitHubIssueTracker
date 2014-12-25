package structure;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
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
		JSONObject issueObj = new JSONObject(strBuilder.toString());
		File labelFile = new File("testFiles/labels");
		reader = new BufferedReader(new FileReader(labelFile));
		strBuilder = new StringBuilder();
		while((input = reader.readLine())!=null){
			strBuilder = strBuilder.append(input);
		}
		reader.close();
		JSONArray labelsObj = new JSONArray(strBuilder.toString());
		ArrayList<String> labels = new ArrayList<String>();
		for(int i=0; i<labelsObj.length(); i++){
			labels.add(labelsObj.getJSONObject(i).getString("name"));
		}
		Issue issue = Issue.makeInstance(issueObj);
		issue.setApplicableLabels(labels);
		assertTrue(issue!=null);
		assertEquals(7, issue.getNumber());
		assertEquals("ZiXian92", issue.getAssignee());
		assertEquals(1, issue.getLabels().length);
		assertEquals("bug", issue.getLabels()[0]);
		assertTrue(issue.getApplicableLabels()!=null);
		assertEquals(labelsObj.length(), issue.getApplicableLabels().length);
		System.out.println(issue.toJSONObject().toString());
	}

}
