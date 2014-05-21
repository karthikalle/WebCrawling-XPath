package xpathtest;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import edu.upenn.cis455.xpathengine.XPathEngineImpl;
import junit.framework.TestCase;

public class InvalidityTest extends TestCase {


	@Before
	public void init() {
		XPathEngineImpl xp = new XPathEngineImpl();

		String[] xpathsInvalid = new String[] {"/foo/bar/xyz[",
				"/foo/bar[@att=\"123\"]]",
				"/foo//bar[@att1=\"123\"][@att2=\"345\"]",
				"/xyz/abc[contains(text(),\"someSubstring\")]",
				"/a/b/c[text())=\"theEntireText\"]",
				"/blah[?anotherElement]",
				"this/that[something/else]",
				"/d/e/f[foo[text()=\"\"something\"]][bar]",
				"/a/b/c[text() =   \"   whiteSpacesShouldNotMatter\"]",
				"/a/b[foo[text()=\"#$(/][]\"]]][bar]/hi[@asdf=\"#$(&[]\"][this][is][crazy]",
	 				"/test[ a/b1[ c1[p]/d[p] ?] /n1[a]/n2 [c2/d[p]/e[text()=\"/asp[&123(123*/]\"]]]",
					"/note/hello4//this[@val=\"text1\"]/that[@val=\"text2\"][something/else]",
				"/note/hello1/to[[text()=\"text2\"][@vp=\"text1\"]",
				"/foo/bar[@=\"This is a \"quoted\" test\"]",
				"/d/e/f[foo[@text()=\"something\"]][bar]]"};

		xp.setXPaths(xpathsInvalid);
	}
	@Test
	public void test0() {
		XPathEngineImpl xp = new XPathEngineImpl();

		String[] xpathsInvalid = new String[] {"/foo/bar/xyz[[",
				"/foo/bar[@att=\"123\"]]",
				"/foo//bar[@att1=\"123\"][@att2=\"345\"]",
				"/xyz/abc[contains((text(),\"someSubstring\")]",
				"/a/b/c[text())=\"theEntireText\"]",
				"/blah[\"anotherElement]",
				"this/that[something//else]",
				"/d/e/f[foo[text()=\"\"something\"]][bar]",
				"/a/b/c[text()) =   \"   whiteSpacesShouldNotMatter\"]",
				"/a/b[foo[text()=\"#$(/][]\"]]][bar]/hi[@asdf=\"#$(&[]\"][this][is][crazy]",
	 				"/text[ a/b1[ c1[p]/d[p] / ] /n1[a]/n2 [c2/d[p]/e[text())=\"/asp[&123(123*/]\"]]]",
					"/note/hello4//this[@val=\"text1\"]/that[@val=\"text2\"][something/else]",
				"/note/hello1/to[[text()=\"text2\"][@vp=\"text1\"]",
				"/foo/bar[@/\"This is a \"quoted\" test\"]",
				"/d/e/f[foo[@text()=\"\"something\"]][bar]]"};

		xp.setXPaths(xpathsInvalid);
			assertFalse(xp.isValid(0));
			assertFalse(xp.isValid(1));
			assertFalse(xp.isValid(2));
			System.out.println(2);

			assertFalse(xp.isValid(3));
			System.out.println(3);
			
			assertFalse(xp.isValid(4));
			System.out.println(4);
			assertFalse(xp.isValid(5));
			System.out.println(5);

			assertFalse(xp.isValid(6));
			System.out.println(6);
			assertFalse(xp.isValid(7));
			System.out.println(7);

			assertFalse(xp.isValid(8));
			System.out.println(8);
			assertFalse(xp.isValid(9));
			assertFalse(xp.isValid(10));
			assertFalse(xp.isValid(11));
			System.out.println(11);

			assertFalse(xp.isValid(12));
			assertFalse(xp.isValid(13));
			assertFalse(xp.isValid(14));
			
	}
	/*

	@Test
	public void test3() {
			assertFalse(xp.isValid(3));
	}
	@Test
	public void test4() {
			assertFalse(xp.isValid(4));
	}
	@Test
	public void test5() {
			assertFalse(xp.isValid(5));
	}
		
	@Test
	public void test6() {
			assertFalse(xp.isValid(6));
	}
	@Test
	public void test7() {
			assertFalse(xp.isValid(7));
	}
	@Test
	public void test8() {
			assertFalse(xp.isValid(8));
	}
	@Test
	public void test9() {
			assertFalse(xp.isValid(9));
	}
	@Test
	public void test10() {
			assertFalse(xp.isValid(10));
	}
	@Test
	public void test11() {
			assertFalse(xp.isValid(11));
	}
	@Test
	public void test12() {
			assertFalse(xp.isValid(12));
	}
	@Test
	public void test13() {
			assertFalse(xp.isValid(13));
	}
	
	@Test
	public void testTrim() {
		xp = new XPathEngineImpl();
		assertEquals("/a/b/c[text()=\"whiteSpacesShouldNotMatter\"]",xp.trimWhiteSpaces("/a/b/c[text() =  \"whiteSpacesShouldNotMatter\"]"));
	}
	*/
}
