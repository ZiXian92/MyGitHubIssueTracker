package model;

import static org.junit.Assert.*;

import org.junit.Test;

import structure.Repository;

public class LoadContributorsThreadTest {

	@Test
	public void test() {
		Repository repo = new Repository("main", "cs2103aug2014-w13-2j");
		Thread thread = new Thread(new LoadContributorsThread(repo, "https://api.github.com/repos/cs2103aug2014-w13-2j/main/contributors"));
		thread.start();
		assertTrue(repo.getAssignees()!=null);
		assertEquals(4, repo.getAssignees().length);
	}

}
