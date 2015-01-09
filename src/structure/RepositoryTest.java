package structure;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import misc.Util;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

/**
 * JUnit test for Repository.
 * @author ZiXian92
 */
public class RepositoryTest {

	@Test
	public void testMakeInstance() throws FileNotFoundException, JSONException {
		File jsonFile = new File("testFiles/repo.txt");
		JSONObject jsonRepo = new JSONObject(Util.getJSONString(new BufferedInputStream(new FileInputStream(jsonFile))));
		Repository repo = Repository.makeInstance(jsonRepo);
		assertTrue(repo!=null);
		assertEquals("ZiXian92", repo.getOwner());
		assertEquals("MyGitHubIssueTracker", repo.getName());
		assertFalse(repo.isInitialized());
	}

	@Test
	public void testReplaceIssue(){
		Repository repo = new Repository("repo", "owner");
		for(int i=1; i<6; i++){
			repo.addIssue(new Issue("issue"+i, i, repo));
		}
		assertEquals("issue3", repo.getIssue(3).getTitle());
		repo.replaceIssue("issue3", new Issue("edited issue", 3, repo));
		assertEquals("edited issue", repo.getIssue(3).getTitle());
	}
}
