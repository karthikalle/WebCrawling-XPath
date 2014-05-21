package XPathCrawlerTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.BerkleyDBWrapper;
import junit.framework.TestCase;


public class PutInitialData extends TestCase {
	
	@Test
	public void delete() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		System.out.println(bdb.removeChannel("a", "channel6"));
	}
	
	@Test
	public void persistenceTest() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStoreTest");
		for( String s: bdb.getAllFiles())
			System.out.println(s);
	}
	
	@Test
	public void getDate() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		System.out.println(bdb.getFileandDate("http://crawltest.cis.upenn.edu/"));
	}
	
	@Test
	public void getXML() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		System.out.println("A");
		try {
			System.out.println(bdb.constructXML("channel2"));
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testXpaths() throws MalformedURLException, InterruptedException {
		XPathCrawler c = new XPathCrawler();
//		c.initParams = new HashMap<String, String>();
//		c.initParams.put("bdbDirectory", "./BDBStore");
		
		String storepath = "./BDBStore";
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize(storepath);
		bdb.putUser("a", "a");
		bdb.putUser("b", "b");

		bdb.putChannel("a", "channel1");
		bdb.putChannel("a", "channel2");
		bdb.putChannel("a", "channel3");
		bdb.putChannel("b", "channel4");
		bdb.putChannel("b", "channel5");

		bdb.putChannelandXpaths("channel1", "/rss/channel/description[contains(text(),\"news\")]");
		bdb.putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-us\"]");
		bdb.putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-gb\"]");
		//bdb.putChannelandXpaths("channel2", "/rss/channel/description[contains(text(),\"news\")]");
		bdb.putChannelandXpaths("channel2","/rss/channel/language[text()=\"en-us\"]");
		bdb.putChannelandXpaths("channel2","/rss/channel/language[text()=\"it-IT\"]");
		bdb.putChannelandXpaths("channel3","/rss/channel/item/link[contains(text(),\"corriere\")]");
		bdb.putChannelandXpaths("channel4","/dwml/data/parameters/temperature[@units=\"Fahrenheit\"]");
		bdb.putChannelandXpaths("channel5","/table/T/C_MKTSEGMENT[text()=\"AUTOMOBILE\"]");
		bdb.destroy();		
		
		String args[] = {"http://crawltest.cis.upenn.edu/",storepath,"100","100"};
		c.startCrawl(args);
		c.matchToAllUrls("channel1");
		c.matchToAllUrls("channel2");
		c.matchToAllUrls("channel3");
		c.matchToAllUrls("channel4");
		c.matchToAllUrls("channel5");

		System.out.println(c.numofFiles);
		bdb.initialize(storepath);

		System.out.println("Matching urls channel1:");
		for(String u :bdb.getChannelandUrls("channel1"))
			System.out.println(u);
		System.out.println("Matching urls channel2:");
		for(String u :bdb.getChannelandUrls("channel2"))
			System.out.println(u);
		System.out.println("Matching urls channel3:");
		for(String u :bdb.getChannelandUrls("channel3"))
			System.out.println(u);		
		System.out.println("Matching urls channel4:");
		for(String u :bdb.getChannelandUrls("channel4"))
				System.out.println(u);
		System.out.println("Matching urls channel5:");
		for(String u :bdb.getChannelandUrls("channel5"))
			System.out.println(u);
		bdb.destroy();
	}
	
	@Test
	public void testXpaths25() throws MalformedURLException, InterruptedException {
		XPathCrawler c = new XPathCrawler();
//		c.initParams = new HashMap<String, String>();
//		c.initParams.put("bdbDirectory", "./BDBStore");
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		bdb.putUser("h", "h");
		bdb.putChannel("h", "channel25");

		bdb.putChannelandXpaths("channel25", "/rss/channel/description[contains(text(),\"news\")]");
		bdb.putChannelandXpaths("channel25","/rss/channel/language[text()=\"en-us\"]");
		bdb.putChannelandXpaths("channel25","/rss/channel/language[text()=\"en-gb\"]");
		bdb.putChannelandXpaths("channel25","/rss/channel/language[text()=\"en-us\"]");
		bdb.putChannelandXpaths("channel25","/rss/channel/language[text()=\"it-IT\"]");
		bdb.destroy();		
		
		bdb.initialize("./BDBStore");
		bdb.destroy();

		String args[] = {"http://crawltest.cis.upenn.edu/","./BDBStore","100","100"};
		c.startCrawl(args);
		System.out.println(c.numofFiles);
		bdb.initialize("./BDBStore");

		System.out.println("Matching urls channel25:");
		for(String u :bdb.getChannelandUrls("channel25"))
			System.out.println(u);
		bdb.destroy();
	}
	
	
	@Test
	public void testContent() throws ParseException, IOException {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		
		assertTrue(bdb.constructXML("channel3").startsWith("<documentcollection>"));
	}

	@Test
	public void testrssagg() throws FileNotFoundException,
			IOException, ParseException {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.initialize("./BDBStore");
		File f = new File("./rss/warandpeace.xp");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String s, xpaths = "";
		while((s=br.readLine())!=null) {
			xpaths += s;
		}
		System.out.println(xpaths);
		bdb.putUser("me", "me");
		bdb.putChannel("me", "rssaggregator");
		bdb.putChannelandXpathsString("rssaggregator",xpaths);
		bdb.putChannelandXSLUrl("rssaggregator","");
		
		XPathCrawler c = new XPathCrawler();
		c.initParams = new HashMap<String, String>();
		c.initParams.put("bdbDirectory", "./BDBStore");
		System.out.println(bdb.getChannelandXpathString("rssaggregator"));
		c.matchEverything("rssaggregator");
		ArrayList<String> u = bdb.getChannelandUrls("rssaggregator");
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/middleeast.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/onemore.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/science.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_law.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_topstories.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_world.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/international/corriere.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Europe.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/HomePage.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/MiddleEast.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Movies.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/National.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Science.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/WeekinReview.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_allpolitics.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/cnn/cnn_us.rss.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/AsiaPacific.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Africa.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/nytimes/Business.xml"));
		assertTrue(u.contains("http://crawltest.cis.upenn.edu/bbc/frontpage.xml"));

		

		bdb.destroy();
	}
}
