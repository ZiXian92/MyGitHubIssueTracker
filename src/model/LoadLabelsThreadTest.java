package model;

import static org.junit.Assert.*;

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
		LoadLabelsThread thread = new LoadLabelsThread(repo);
		thread.run();
		assertEquals(12, repo.getLabels().size());
	}

}
