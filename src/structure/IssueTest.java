package structure;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import misc.Constants;
import misc.Util;

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
		Repository repo = new Repository("testRepo", "noOwner");
		
		File file = new File("testFiles/issue7");
		JSONObject issueObj = new JSONObject(Util.getJSONString(new BufferedInputStream(new FileInputStream(file))));
		
		File labelFile = new File("testFiles/labels");
		JSONArray labelsObj = new JSONArray(Util.getJSONString(new BufferedInputStream(new FileInputStream(labelFile))));
		
		ArrayList<String> labels = new ArrayList<String>();
		for(int i=0; i<labelsObj.length(); i++){
			labels.add(labelsObj.getJSONObject(i).getString("name"));
		}
		Issue issue = Issue.makeInstance(issueObj, repo);
		issue.setApplicableLabels(labels);
		assertTrue(issue!=null);
		assertEquals(7, issue.getNumber());
		assertEquals("ZiXian92", issue.getAssignee());
		assertFalse(issue.isInitialized());
		assertEquals(1, issue.getLabels().size());
		assertEquals("bug", issue.getLabels().get(0));
		assertTrue(issue.getApplicableLabels()!=null);
		assertEquals(labelsObj.length(), issue.getApplicableLabels().size());
		assertEquals(Constants.ISSUE_STATUSOPEN, issue.getStatus());
		assertEquals("V0.9", issue.getMilestone());
		assertEquals("open  	[Bug] Password can be seen ...	V0.9        	ZiXian92", issue.getCondensedString());
		System.out.println(issue.toJSONObject().toString());
	}
	
	@Test
	public void testAddComment() throws JSONException{
		Repository repo = new Repository("testRepo", "noOwner");
		Issue issue = new Issue("new issue", 7, repo);
		assertTrue(issue.getComments().isEmpty());
		String commentString = "{\"id\": 3, \"user\":{\"login\": \"author1\"}, \"body\": \"New comment.\"}";
		issue.addComment(new JSONObject(commentString));
		ArrayList<Issue.Comment> comments = issue.getComments();
		assertFalse(comments==null);
		assertEquals(1, comments.size());
		assertEquals("author1", comments.get(0).getAuthor());
		assertEquals("New comment.", comments.get(0).getContent());
	}
}
