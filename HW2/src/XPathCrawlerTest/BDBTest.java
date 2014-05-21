package XPathCrawlerTest;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.BerkleyDBWrapper;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class BDBTest {

	@Test
	public void testUser() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect("./BDBStoreTest");
	//	bdb.putUser("a", "a");
		assertEquals("c",bdb.getUser("c"));
		bdb.destroy();

	}
	
	@Test
	public void testFile() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		XPathCrawler c = new XPathCrawler();
		String Url = "http://crawltest.cis.upenn.edu/";
		String content = c.getBody(Url);
		bdb.putFileandContent(Url, content);
		System.out.println(bdb.getFileandContent(Url));
	}

	@Test
	public void testUser2() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		bdb.putUser("alle", "a");
		assertNotSame("a",bdb.getUser("nalle"));
		bdb.destroy();

	}

	@Test
	public void testChannels() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		assertTrue(bdb.putChannel("alle", "c1"));
		ArrayList<String> a = new ArrayList<String>();
		a = bdb.getChannel("alle");
		assertEquals(1, a.size());
		assertEquals("c1", a.get(0));
		bdb.destroy();

	}
	
	@Test
	public void testMultipleChannels() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		assertTrue(bdb.putChannel("balle", "channel1"));
		assertTrue(bdb.putChannel("balle", "channel2"));
		assertTrue(bdb.putChannel("balle", "channel3"));

		ArrayList<String> a = new ArrayList<String>();
		a = bdb.getChannel("balle");
		//System.out.println(a.get(0)+a.get(1)+a.get(2));

		assertEquals(3, a.size());
		assertEquals("channel1", a.get(0));
		assertEquals("channel2", a.get(1));
		assertEquals("channel3", a.get(2));
		bdb.destroy();

	}

	@Test
	public void testExistingChannel() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		assertFalse(bdb.putChannel("balle", "channel1"));
		assertFalse(bdb.putChannel("balle", "channel2"));
		bdb.destroy();

	}
	
	
	@Test
	public void testChannelandXpath() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		assertTrue(bdb.putChannelandXpaths("c1", "/foo/bar/xyz"));
		assertTrue(bdb.putChannelandXpaths("c1", "/foo/bar[@att=\"123\"]"));
		assertTrue(bdb.putChannelandXpaths("c1", "/foo/bar[@att1=\"123\"][@att2=\"345\"]"));
		
		ArrayList<String> a = new ArrayList<String>();
		a = bdb.getChannelandXpaths("c1");
		assertEquals(3, a.size());
		assertEquals("/foo/bar/xyz", a.get(0));
		assertEquals("/foo/bar[@att=\"123\"]",a.get(1));
		assertEquals("/foo/bar[@att1=\"123\"][@att2=\"345\"]",a.get(2));
		bdb.destroy();
	}
	
	@Test
	public void testChannelandUrl() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		assertTrue(bdb.putChannelandUrls("c1", "http://crawltest.cis.upenn.edu/nytimes/"));
		assertTrue(bdb.putChannelandUrls("c1", "http://crawltest.cis.upenn.edu/nytimes/Europe.xml"));
		assertTrue(bdb.putChannelandUrls("c1", "http://crawltest.cis.upenn.edu/misc/eurofxref-daily.xml"));
		
		ArrayList<String> a = new ArrayList<String>();
		a = bdb.getChannelandUrls("c1");
		assertEquals(3, a.size());
		assertEquals("http://crawltest.cis.upenn.edu/nytimes/", a.get(0));
		assertEquals("http://crawltest.cis.upenn.edu/nytimes/Europe.xml",a.get(1));
		assertEquals("http://crawltest.cis.upenn.edu/misc/eurofxref-daily.xml",a.get(2));
		bdb.destroy();

	}

	@Test
	public void testifExists() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		System.out.println(bdb.getUser("alle"));
		bdb.destroy();

	}
	
	@Test
	public void testContains() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		System.out.println(bdb.getFileandContent("http://crawltest.cis.upenn.edu/nytimes/"));
	}
	
	@Test
	public void testgetAll() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		bdb.getAllFiles();
	}

}
