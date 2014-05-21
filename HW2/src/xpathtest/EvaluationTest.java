package xpathtest;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import junit.framework.TestCase;

public class EvaluationTest extends TestCase {
	XPathEngineImpl xp;
	String[] xpathsValid;
	String[] xpathsInvalid;

	@Before
	public void init() {

	}
	
	@Test
	public void testevaluate001() {
		xp = new XPathEngineImpl();	
		xpathsValid = new String[] {
				"/rss/channel/item/link[contains(text(),\"corriere\")]"
		};
		xp.setXPaths(xpathsValid);
			System.out.println(xp.isValid(0));

		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		
		//assertTrue(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/note.xml" ));
		Document d = (xp.parseFile(xpathsValid[0],"http://crawltest.cis.upenn.edu/international/corriere.xml" ));
		assertTrue(xp.evaluate(d)[0]);
	}

	@Test
	public void testevaluate1() {
		xp = new XPathEngineImpl();	
		xpathsValid = new String[] {
				"/note/to[text()=\"Tove\"]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		
		//assertTrue(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/note.xml" ));
		Document d = (xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/note.xml" ));
		assertTrue(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testevaluate2() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/breakfast_menu/food[price[text()=\"$5.95\"]][calories[text()=\"650\"]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
	//	assertTrue(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml"));
		Document d = xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml");
		assertTrue(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testInvalidevaluate2() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/breakfast_menu/food[price[text()=\"$55.95\"]][calories[text()=\"650\"]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml"));
		Document d = xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml");
		assertFalse(xp.evaluate(d)[0]);

	}
	
	@Test
	public void testInvalidevaluate22() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/breakfast_menu/food[price[text()=\"$5.95\"]][calories[text()=\"600\"]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml"));
		Document d = xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml");
		assertFalse(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testevaluate3() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/breakfast_menu/food[price[text()=\"$5.95\"]][calories[text()=\"650\"]][description[contains(text(),\"no\")]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml"));
		Document d = xp.parseFile(xpathsValid[0],"http://www.w3schools.com/xml/simple.xml");
		assertFalse(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testAtt() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/catalog/book[@id=\"bk101\"]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertTrue(xp.parseFile(xpathsValid[0],"test.xml"));
		Document d = xp.parseFile(xpathsValid[0],"test.xml");
		assertTrue(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testAttandOtherValid() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/catalog/book[@id=\"bk104\"][author[contains(text(),\"Corets\")]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"test.xml"));
		Document d = xp.parseFile(xpathsValid[0],"test.xml");
		assertTrue(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testAttandOtherValid2() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/catalog/book[@id=\"bk103\"][author/details[name[contains(text(),\"Corets\")]][gender[text()=\"Female\"]]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"test.xml"));
		Document d = xp.parseFile(xpathsValid[0],"test.xml");
		assertTrue(xp.evaluate(d)[0]);
	}
	
	@Test
	public void testAttandOtherInValid2() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/catalog/book[@id=\"bk103\"][author/details[name[contains(text(),\"Corets\")]][gender[text()=\"male\"]]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"test.xml"));
		Document d = xp.parseFile(xpathsValid[0],"test.xml");
		assertFalse(xp.evaluate(d)[0]);
	}
	@Test
	public void testAttandOtherInvalid() {
		xp = new XPathEngineImpl();
		xpathsValid = new String[] {
				"/catalog/book[@id=\"bk101\"][author[contains(text(),\"Corets\")]]"
		};
		xp.setXPaths(xpathsValid);
		assertTrue(xp.isValid(0));
		//assertFalse(xp.parseFile(xpathsValid[0],"test.xml"));
		Document d = xp.parseFile(xpathsValid[0],"test.xml");
		assertFalse(xp.evaluate(d)[0]);
	}
}
