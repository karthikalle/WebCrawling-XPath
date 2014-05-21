package xpathtest;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import junit.framework.TestCase;

public class ValidityTest extends TestCase {
	XPathEngineImpl xp;
	String[] xpathsValid;
	String[] xpathsInvalid;

	@Before
	public void init() {

	}
	
	@Test
	public void test1() {

		xp = new XPathEngineImpl();
		xpathsValid = new String[] {"/foo/bar/xyz",
				"/foo/bar[@att=\"123\"]",
				"/foo/bar[@att1=\"123\"][@att2=\"345\"]",
				"/xyz/abc[contains(text(),\"someSubstring\")]",
				"/a/b/c[text()=\"theEntireText\"]",
				"/blah[anotherElement]",
				"/this/that[something/else]",
				"/d/e/f[foo[text()=\"something\"]][bar]",
				"/a/b/c[text() =   \"   whiteSpacesShouldNotMatter\"]",
				"/a/b[foo[text()=\"#$(/][]\"]][bar]/hi[@asdf=\"#$(&[]\"][this][is][crazy]",
	 				"/test[ a/b1[ c1[p]/d[p] ] /n1[a]/n2 [c2/d[p]/e[text()=\"/asp[&123(123*/]\"]]]",
					"/note/hello4/this[@val=\"text1\"]/that[@val=\"text2\"][something/else]",
				"/note/hello1/to[text()=\"text2\"][@vp=\"text1\"]",
				"/foo/bar[@abc=\"This is a \"quoted\" test\"]"
		};
	
		xpathsInvalid = new String[] {"/d/e/f[foo[text()=\"something\"]][bar]]"};

		xp.setXPaths(xpathsValid);
		for (int i = 0;i <xpathsValid.length; i++) {
			assertTrue(xp.isValid(i));
			System.out.println("\n"+i+" "+xpathsValid[i]+" returned "+"true");
		}
		xp.setXPaths(xpathsInvalid);
		for (int i = 0;i <xpathsInvalid.length; i++) {
			assertFalse(xp.isValid(i));
			System.out.println("\n"+xpathsInvalid[i]+" returned "+"false");
		}
	}
	 
	@Test
	public void testTrim() {
		xp = new XPathEngineImpl();
		assertEquals("/a/b/c[text()=\"whiteSpacesShouldNotMatter\"]",xp.trimWhiteSpaces("/a/b/c[text() =  \"whiteSpacesShouldNotMatter\"]"));
	}
}
