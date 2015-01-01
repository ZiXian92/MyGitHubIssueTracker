package model;

import static org.junit.Assert.*;

import org.junit.Test;

import structure.Repository;

/**
 * JUnit test to test thread to load contributors.
 * @author ZiXian92
 */
public class LoadContributorsThreadTest {

	@Test
	public void test() {
		Repository repo = new Repository("main", "cs2103aug2014-w13-2j");
		Thread thread = new Thread(new LoadContributorsThread(repo));
		thread.run();
		assertTrue(repo.getAssignees()!=null);
		assertEquals(4, repo.getAssignees().length);
	}

}
