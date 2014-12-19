package controller;

import Misc.InvalidContextException;
import controller.Parser;
import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Defines the unit test for Parser class.
 * @author ZiXian92
 */
public class ParserTest {

	@Test
	//Tests the list command parsing
	public void testList() throws IllegalArgumentException, InvalidContextException {
		Parser parser = new Parser();
		Command cmd = parser.parse("list", null, null);
		assertTrue(cmd instanceof ListCommand);
		cmd = parser.parse(" ls", null, null);
		assertTrue(cmd instanceof ListCommand);
	}
	
	@Test
	//Tests parsing of select command
	public void testSelect() throws IllegalArgumentException, InvalidContextException{
		Parser parser = new Parser();
		Command cmd = parser.parse("select MyGitHubIssueTracker", null, null);
		assertTrue(cmd instanceof SelectRepo);
		
		cmd = parser.parse("MyGitHubIssueTracker", null, null);
		assertTrue(cmd instanceof SelectRepo);
		
		cmd = parser.parse(" MyGitHubIssueTracker", null, null);
		assertTrue(cmd instanceof SelectRepo);
		
		cmd = parser.parse("MyGitHubIssueTracker ", null, null);
		assertTrue(cmd instanceof SelectRepo);
		
		cmd = parser.parse("issue1", "MyGitHubIssueTracker", null);
		assertTrue(cmd instanceof SelectIssue);
		
		cmd = parser.parse("comment1", "MyGitHubIssueTracker", "issue1");
		assertTrue(cmd instanceof CommentIssue);
	}
	
	@Test
	public void testBack() throws IllegalArgumentException, InvalidContextException{
		Parser parser = new Parser();
		Command cmd = parser.parse("back", "MyGitHubIssueTracker", "issue1");
		assertTrue(cmd instanceof SelectRepo);
		
		cmd = parser.parse("back", "MyGitHubIssueTracker", null);
		assertTrue(cmd instanceof ListCommand);
	}
	
	@Test
	public void testAdd() throws IllegalArgumentException, InvalidContextException{
		Parser parser = new Parser();
		Command cmd = parser.parse("add new issue", "MyGitHubIssueTracker", null);
		assertTrue(cmd instanceof AddIssue);
		
		cmd = parser.parse("add new issue", "MyGitHubIssueTracker", "issue1");
		assertTrue(cmd instanceof AddIssue);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidCommands() throws IllegalArgumentException, InvalidContextException{
		Parser parser = new Parser();
		parser.parse("select", null, null);
		parser.parse(" select", null, null);
		parser.parse("select ", null, null);
		parser.parse("", null, null);
		parser.parse(" ", null, null);
		parser.parse("close", "repo1", null);
		parser.parse("add", "MyGitHubIssueTracker", null);
		parser.parse("add ", "MyGitHubIssueTracker", null);
	}
	
	@Test(expected=InvalidContextException.class)
	public void testInvalidContext() throws IllegalArgumentException, InvalidContextException {
		Parser parser = new Parser();
		parser.parse("back", null, null);
		parser.parse("close", null, null);
		parser.parse("add", null, null);
		parser.parse("add new issue", null, null);
		parser.parse("select 2", "repo1", "issue1");
	}

	@Test
	public void testClose() throws IllegalArgumentException, InvalidContextException{
		Parser parser = new Parser();
		
		Command cmd = parser.parse("close", "repo1", "issue1");
		assertTrue(cmd instanceof CloseIssue);
		
		cmd = parser.parse("close 1", "repo1", null);
		assertTrue(cmd instanceof CloseIssue);
	}
}
