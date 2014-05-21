package XPathCrawlerTest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.BerkleyDBWrapper;
import junit.framework.TestCase;


public class XPathCrawlerTest extends TestCase {
	@Test
	public void test1() throws MalformedURLException {
		String u = "http://crawltest.cis.upenn.edu";
		XPathCrawler c = new XPathCrawler();
		URL ur = new URL(u);
		c.getBody(u);
	}

	@Test
	public void testpull() throws MalformedURLException {
		XPathCrawler c = new XPathCrawler();
		c.initParams = new HashMap<String, String>();
		c.initParams.put("bdbDirectory", "./BDBStore");
		c.alreadyVisited_host = new HashMap<String, HashMap<String,Date>>();
		c.pullfrombdb();
	}

	@Test
	public void test() throws MalformedURLException {
		XPathCrawler c = new XPathCrawler();
		c.initParams = new HashMap<String, String>();
		c.initParams.put("bdbDirectory", "./BDBStore");
		c.alreadyVisited_host = new HashMap<String, HashMap<String,Date>>();
		c.isCrawlingRequired("http://crawltest.cis.upenn.edu/nytimes/Europe.xml","");
	}

	@Test
	public void testXpaths() throws MalformedURLException, InterruptedException {
		XPathCrawler c = new XPathCrawler();
		c.initParams = new HashMap<String, String>();
		c.initParams.put("bdbDirectory", "./BDBStore");
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		//bdb.putTestData();
		//		System.out.println("p"+bdb.putChannel("balle", "channel1"));
		bdb.putChannel("balle", "channel1");
		bdb.putChannel("balle", "channel2");
		bdb.putChannelandXpaths("channel1", "/rss/channel/description[contains(text(),\"news\")]");
		bdb.putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-us\"]");
		bdb.putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-gb\"]");
		bdb.putChannelandXpaths("channel2", "/rss/channel/description[contains(text(),\"news\")]");
		bdb.putChannelandXpaths("channel2","/rss/channel/language[text()=\"en-us\"]");
		String args[] = {"http://crawltest.cis.upenn.edu/","./BDBStore","100","100"};
		//c.startCrawl(args);
		c.matchAllChannels("http://crawltest.cis.upenn.edu/bbc/middleeast.xml");
		c.matchAllChannels("http://crawltest.cis.upenn.edu/bbc/onemore.xml");

		System.out.println("Matching urls");
		ArrayList<String> u=bdb.getChannelandUrls("channel1");
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/MiddleEast.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Americas.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/WeekinReview.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Movies.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/National.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/HomePage.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/AsiaPacific.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_us.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_allpolitics.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Business.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_topstories.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/middleeast.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_law.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/frontpage.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Africa.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/science.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Europe.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/onemore.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Science.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_world.rss.xml"));

		System.out.println("Matching urls");
		ArrayList<String> ur = bdb.getChannelandUrls("channel2");
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/MiddleEast.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Americas.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/WeekinReview.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Movies.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/National.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/HomePage.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/AsiaPacific.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_us.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_allpolitics.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Business.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_topstories.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_law.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Africa.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Europe.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/onemore.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Science.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_world.rss.xml"));
		bdb.destroy();
	}
}
