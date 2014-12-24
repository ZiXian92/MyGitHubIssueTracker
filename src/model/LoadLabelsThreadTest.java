package model;

import static org.junit.Assert.*;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import structure.Repository;

/**
 * JUnit test to test LoadLabelsThread class.
 * @author ZiXian92
 */
public class LoadLabelsThreadTest {

	@Test
	public void test() {
		Repository repo = new Repository("MyGitHubIssueTracker", "zixian92");
		HttpGet req = new HttpGet("https://api.github.com/repos/zixian92/MyGitHubIssueTracker/labels");
		req.addHeader("Accept", "application/vnd.github.v3+json");
		LoadLabelsThread thread = new LoadLabelsThread(repo, req);
		thread.run();
		assertEquals(11, repo.getLabels().size());
	}

}
