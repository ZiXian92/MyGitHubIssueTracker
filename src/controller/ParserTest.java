package controller;

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
	public void testList() {
		Parser parser = new Parser();
		Command cmd = parser.parse("list", null, null);
		assertTrue(cmd instanceof ListCommand);
		cmd = parser.parse(" ls", null, null);
		assertTrue(cmd instanceof ListCommand);
	}
	
	@Test
	//Tests parsing of select command
	public void testSelect(){
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
	public void testBack(){
		Parser parser = new Parser();
		Command cmd = parser.parse("back", "MyGitHubIssueTracker", "issue1");
		assertTrue(cmd instanceof SelectRepo);
		
		cmd = parser.parse("back", "MyGitHubIssueTracker", null);
		assertTrue(cmd instanceof ListCommand);
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testInvalidCommands(){
		Parser parser = new Parser();
		parser.parse("select", null, null);
		parser.parse(" select", null, null);
		parser.parse("select ", null, null);
		parser.parse("", null, null);
		parser.parse(" ", null, null);
		parser.parse("back", null, null);
		parser.parse("close", null, null);
		parser.parse("close", "repo1", null);
	}

	@Test
	public void testClose(){
		Parser parser = new Parser();
		
		Command cmd = parser.parse("close", "repo1", "issue1");
		assertTrue(cmd instanceof CloseIssue);
		
		cmd = parser.parse("close 1", "repo1", null);
		assertTrue(cmd instanceof CloseIssue);
	}
}
