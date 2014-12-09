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
	public void testList() {
		Parser parser = new Parser();
		Command cmd = parser.parse("list", null, null);
		assertTrue(cmd instanceof List);
	}

}
