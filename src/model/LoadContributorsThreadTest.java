package model;

import static org.junit.Assert.*;

import org.apache.http.client.methods.HttpGet;
import org.junit.Test;

import structure.Repository;

public class LoadContributorsThreadTest {

	@Test
	public void test() {
		Repository repo = new Repository("main", "cs2103aug2014-w13-2j");
		HttpGet req = new HttpGet("https://api.github.com/repos/cs2103aug2014-w13-2j/main/contributors");
		req.addHeader("Accept", "application/vnd.github.v3+json");
		Thread thread = new Thread(new LoadContributorsThread(repo, req));
		thread.run();
		assertTrue(repo.getAssignees()!=null);
		assertEquals(4, repo.getAssignees().length);
	}

}
