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

}
