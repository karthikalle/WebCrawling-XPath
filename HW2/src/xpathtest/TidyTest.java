package xpathtest;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import junit.framework.TestCase;

public class TidyTest extends TestCase {
	XPathEngineImpl xp;
	String[] xpathsValid;
	String[] xpathsInvalid;

	@Before
	public void init() {

	}
	
	@Test
	public void test1() {
		xp = new XPathEngineImpl();	
		xpathsValid = new String[] {
				"/note/to[text()=\"Tove\"]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		
		//assertTrue(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/note.xml" ));
		Document d = (xp.parseFile(xpathsValid[0],"http://www.cis.upenn.edu.com" ));
		assertTrue(xp.evaluate(d)[0]);
	}
}
