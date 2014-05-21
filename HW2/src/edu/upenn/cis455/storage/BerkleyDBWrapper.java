package edu.upenn.cis455.storage;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import com.sleepycat.bind.tuple.StringBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;

//import edu.upenn.cis455.crawler.XPathCrawler;

public class BerkleyDBWrapper {

	EnvironmentConfig e;

	Environment dbenv;
	Database usersDB, userchannelsDB, listOfChannelsDB, channelsXpathDB, channelsUrlsDB, contentSeenDB,
	filesDB, filesandDatedb, channelXSLDb, parametersDb;
	String dbStorePath;

	public void initialize(String storePath) {
		e = new EnvironmentConfig();
		e.setAllowCreate(true);

		dbStorePath = storePath;
		File f = new File(dbStorePath);
		if(!f.exists())
			f.mkdirs();
		dbenv = new Environment(f, e);
		
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);

		//inserted
        e.setTransactionalVoid(false);
        e.setLockingVoid(false);
    
	//	dbConfig.setTransactional(true);
		
		usersDB = dbenv.openDatabase(null, "users", dbConfig);
		userchannelsDB = dbenv.openDatabase(null, "userchannels", dbConfig);
		listOfChannelsDB = dbenv.openDatabase(null, "listOfChannels", dbConfig);
		channelsXpathDB = dbenv.openDatabase(null, "channelsandXpaths", dbConfig);
		channelsUrlsDB = dbenv.openDatabase(null, "channelsandUrls", dbConfig);
		filesDB = dbenv.openDatabase(null, "filesandcontent", dbConfig);
		filesandDatedb = dbenv.openDatabase(null, "filesanddate", dbConfig);
		channelXSLDb = dbenv.openDatabase(null, "channelxsl", dbConfig);
		parametersDb = dbenv.openDatabase(null, "params", dbConfig);
		contentSeenDB = dbenv.openDatabase(null, "contentseen", dbConfig);
	}

	public void destroy() {
		usersDB.close();
		userchannelsDB.close();
		listOfChannelsDB.close();
		channelsXpathDB.close();
		channelsUrlsDB.close();
		filesDB.close();
		filesandDatedb.close();
		channelXSLDb.close();
		parametersDb.close();
		contentSeenDB.close();
		dbenv.close();
	}

	public void connect(String storePath) {

		dbStorePath = storePath;
		File f = new File(dbStorePath);
		try {
			dbenv = new Environment(f, null);
		}
		catch(IllegalArgumentException e) {
			initialize(storePath);
			return;
		}

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setAllowCreate(true);
	//	dbConfig.setTransactional(true);

		usersDB = dbenv.openDatabase(null, "users", dbConfig);
		userchannelsDB = dbenv.openDatabase(null, "userchannels", dbConfig);
		listOfChannelsDB = dbenv.openDatabase(null, "listOfChannels", dbConfig);
		channelsXpathDB = dbenv.openDatabase(null, "channelsandXpaths", dbConfig);
		channelsUrlsDB = dbenv.openDatabase(null, "channelsandUrls", dbConfig);
		filesDB = dbenv.openDatabase(null, "filesandcontent", dbConfig);
		filesandDatedb = dbenv.openDatabase(null, "filesanddate", dbConfig);
		channelXSLDb = dbenv.openDatabase(null, "channelxsl", dbConfig);
		parametersDb = dbenv.openDatabase(null, "params", dbConfig);
		contentSeenDB = dbenv.openDatabase(null, "contentseen", dbConfig);

	}

	/*public void putTestData() {
		putUser("alle", "a");
		StringUrl = "http://crawltest.cis.upenn.edu/";
		XPathCrawler c = new XPathCrawler();
		String content = c.getBody(Url);
		putChannel("alle", "c1");
		putUser("balle","b");
		//System.out.println("p"+putChannel("balle", "channel1"));
		//System.out.println(putChannel("balle", "channel2"));
		//System.out.println(putChannel("balle", "channel3"));
		putChannelandXpaths("channel1", "/rss/channel/description[contains(text(),\"news\")]");
		putChannelandXpaths("channel1","/rss/channel/language[text()=\"en-us\"]");
		//putChannelandXpaths("c1", "/foo/bar[@att1=\"123\"][@att2=\"345\"]");
		putChannelandUrls("channel1", "http://crawltest.cis.upenn.edu/nytimes/");
		putChannelandUrls("channel1", "http://crawltest.cis.upenn.edu/nytimes/Europe.xml");
		putChannelandUrls("channel1", "http://crawltest.cis.upenn.edu/misc/eurofxref-daily.xml");
		putChannelandUrls("channel2", "http://crawltest.cis.upenn.edu/nytimes/Europe.xml");
		putChannelandUrls("channel3", "http://crawltest.cis.upenn.edu/misc/eurofxref-daily.xml");

	}
*/

	public boolean putUser(String username, String pwd) {
		if(getUser(username)!=null)
			return false;
		DatabaseEntry user = new DatabaseEntry();
		DatabaseEntry password = new DatabaseEntry();
		StringBinding.stringToEntry(username, user);
		StringBinding.stringToEntry(pwd, password);
		usersDB.put(null, user, password);
		return true;
	}

	public String getUser(String username) {
		DatabaseEntry user = new DatabaseEntry();
		DatabaseEntry pwd = new DatabaseEntry();
		StringBinding.stringToEntry(username, user);

		if(usersDB.get(null, user, pwd, null) == OperationStatus.SUCCESS) {
			return StringBinding.entryToString(pwd);
		}
		else
			return null;

	}

	public void putFileandContent(String urlofFile, String contentOfFile) {
		////System.out.println(urlofFile);
		////System.out.println(contentOfFile);

		DatabaseEntry url = new DatabaseEntry();
		DatabaseEntry content = new DatabaseEntry();
		StringBinding.stringToEntry(urlofFile, url);
		StringBinding.stringToEntry(contentOfFile, content);
		filesDB.put(null, url, content);
	}

	public boolean getContentSeen(String body) {
		String hash = getHash(body);
		DatabaseEntry hashentry = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();

		StringBinding.stringToEntry(hash, hashentry);
		if(contentSeenDB.get(null, hashentry, value, null) == OperationStatus.SUCCESS) {
			//System.out.println("hashvalue:"+hash);
			return true;
		}
		
		return false;

	}
	public void putContentSeen(String body) {
		String hash = getHash(body);
		DatabaseEntry hashvalue = new DatabaseEntry();
		DatabaseEntry val = new DatabaseEntry();
		StringBinding.stringToEntry(hash, hashvalue);
		String value = "1";
		StringBinding.stringToEntry(value, val);
	//	System.out.println("putting hash value"+hash);
		contentSeenDB.put(null, hashvalue, val);
	}

	private String getHash(String key){
	//	System.out.println("Hash value of ");
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			md.update(key.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int j = 0; j < byteData.length; j++) {
				sb.append(Integer.toString((byteData[j] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();

		}
		catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;

	}	
	public String getFileandContent(String fileName) {
		DatabaseEntry file = new DatabaseEntry();
		DatabaseEntry content = new DatabaseEntry();

		StringBinding.stringToEntry(fileName, file);

		if(filesDB.get(null, file, content, null) == OperationStatus.SUCCESS) {
			return StringBinding.entryToString(content);
		}
		else
			return null;
	}

	public void putFileandDate(String urlofFile, Date date) {
		DatabaseEntry url = new DatabaseEntry();
		DatabaseEntry dateOfFile = new DatabaseEntry();
		StringBinding.stringToEntry(urlofFile, url);
		////System.out.println(date.toString());
		if(date==null) {
			System.out.println("Null date");
			return;
		}
		StringBinding.stringToEntry(date.toString(), dateOfFile);
		filesandDatedb.put(null, url, dateOfFile);

	}

	public Date getFileandDate(String fileName) {
		DatabaseEntry file = new DatabaseEntry();
		DatabaseEntry date = new DatabaseEntry();

		StringBinding.stringToEntry(fileName, file);

		if(filesandDatedb.get(null, file, date, null) == OperationStatus.SUCCESS) {
			//	//System.out.println(fileName);
			//	//System.out.println(StringBinding.entryToString(date));

			return new Date(StringBinding.entryToString(date));
		}
		else
			return null;
	}

	public ArrayList<String> getAllFiles() {
		Cursor cursor = filesandDatedb.openCursor(null, null);
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		ArrayList<String> a = new ArrayList<String>();
		while (cursor.getNext(key,data,LockMode.DEFAULT)==OperationStatus.SUCCESS) {
			//System.out.println("File:"+StringBinding.entryToString(key));
			a.add(StringBinding.entryToString(key));
		}
		cursor.close();
		if(a.size() == 0)
			return null;
		System.out.println("SIZE"+a.size());
		return a;
	}

	public ArrayList<String> getAllChannels() {
		Cursor cursor = listOfChannelsDB.openCursor(null, null);
		DatabaseEntry key = new DatabaseEntry();
		DatabaseEntry data = new DatabaseEntry();
		ArrayList<String> a = new ArrayList<String>();
		while (cursor.getNext(key,data,LockMode.DEFAULT)==OperationStatus.SUCCESS) {
			a.add(StringBinding.entryToString(key));
		}
		cursor.close();
		if(a.size() == 0)
			return null;

		return a;
	}

	public boolean putChannel(String username, String channelName) {
		DatabaseEntry user = new DatabaseEntry();
		DatabaseEntry channel = new DatabaseEntry();

		if(doesChannelExist(channelName)) {
			return false;
		}
		StringBinding.stringToEntry(username, user);
		StringBinding.stringToEntry(channelName, channel);

		listOfChannelsDB.put(null, channel, user);

		ArrayList<String> channels = new ArrayList<String>();
		if((channels = getChannel(username))!=null) {
			channels.add(channelName);
			String str = "";
			for (String s: channels) {
				str = str + s +";";
			}
			str = str.substring(0,str.length()-1);
			channelName = str;
		}
		//System.out.println(channelName);
		StringBinding.stringToEntry(username, user);
		StringBinding.stringToEntry(channelName, channel);
		//System.out.println("insering"+username+" "+channelName);
		userchannelsDB.put(null, user, channel);
		return true;
	}

	public boolean putChannelSubscription(String username, String channelName) {
		DatabaseEntry user = new DatabaseEntry();
		DatabaseEntry channel = new DatabaseEntry();

		StringBinding.stringToEntry(username, user);
		StringBinding.stringToEntry(channelName, channel);

		ArrayList<String> channels = new ArrayList<String>();
		if((channels = getChannel(username))!=null) {
			channels.add(channelName);
			String str = "";
			for (String s: channels) {
				str = str + s +";";
			}
			str = str.substring(0,str.length()-1);
			channelName = str;
		}
		//System.out.println(channelName);
		StringBinding.stringToEntry(username, user);
		StringBinding.stringToEntry(channelName, channel);
		//System.out.println("inserting "+username+" "+channelName);
		userchannelsDB.put(null, user, channel);
		return true;
	}


	public ArrayList<String> getChannel(String username) {
		DatabaseEntry user = new DatabaseEntry();
		DatabaseEntry channelnames = new DatabaseEntry();

		StringBinding.stringToEntry(username, user);

		if(userchannelsDB.get(null, user, channelnames, null) == OperationStatus.SUCCESS) {
			String channels = StringBinding.entryToString(channelnames);
			String channelArray[] = channels.split(";");
			ArrayList<String> channelArrayList = new ArrayList<String>();
			for(String s: channelArray) {
				channelArrayList.add(s);
			}
			return channelArrayList;
		}
		else
			return null;
	}

	public boolean putChannelandXpaths(String channelname, String XPath) {
		DatabaseEntry Xpath = new DatabaseEntry();
		DatabaseEntry channel = new DatabaseEntry();

		if(!doesChannelExist(channelname))
			return false;

		ArrayList<String> xpaths = new ArrayList<String>();
		if((xpaths = getChannelandXpaths(channelname))!=null) {
			xpaths.add(XPath);
			String str = "";
			for (String s: xpaths) {
				str = str + s +";";
			}
			str = str.substring(0,str.length()-1);
			XPath = str;
		}
		StringBinding.stringToEntry(channelname, channel);
		StringBinding.stringToEntry(XPath, Xpath);
		channelsXpathDB.put(null, channel, Xpath);
		return true;
	}

	public boolean putChannelandXpathsString(String channelname, String XPath) {
		DatabaseEntry Xpath = new DatabaseEntry();
		DatabaseEntry channel = new DatabaseEntry();

		if(!doesChannelExist(channelname))
			return false;

		StringBinding.stringToEntry(channelname, channel);
		StringBinding.stringToEntry(XPath, Xpath);
		channelsXpathDB.put(null, channel, Xpath);
		return true;
	}

	public String getChannelandXpathString(String channelname) {
		DatabaseEntry channel = new DatabaseEntry();
		DatabaseEntry xpaths = new DatabaseEntry();

		StringBinding.stringToEntry(channelname, channel);

		if(channelsXpathDB.get(null, channel, xpaths, null) == OperationStatus.SUCCESS) {
			String xpathlist = StringBinding.entryToString(xpaths);
			return xpathlist;
		}
		else
			return null;
	}

	public boolean putChannelandXSLUrl(String channelname, String XPath) {
		DatabaseEntry Xpath = new DatabaseEntry();
		DatabaseEntry channel = new DatabaseEntry();

		if(!doesChannelExist(channelname))
			return false;

		StringBinding.stringToEntry(channelname, channel);
		StringBinding.stringToEntry(XPath, Xpath);
		channelXSLDb.put(null, channel, Xpath);
		return true;
	}

	public String getChannelandXSLUrl(String channelname) {
		DatabaseEntry channel = new DatabaseEntry();
		DatabaseEntry xpaths = new DatabaseEntry();

		StringBinding.stringToEntry(channelname, channel);

		if(channelXSLDb.get(null, channel, xpaths, null) == OperationStatus.SUCCESS) {
			String xpathlist = StringBinding.entryToString(xpaths);
			return xpathlist;
		}
		else
			return null;
	}


	public ArrayList<String> getChannelandXpaths(String channelname) {
		DatabaseEntry channel = new DatabaseEntry();
		DatabaseEntry xpaths = new DatabaseEntry();

		StringBinding.stringToEntry(channelname, channel);

		if(channelsXpathDB.get(null, channel, xpaths, null) == OperationStatus.SUCCESS) {
			String xpathlist = StringBinding.entryToString(xpaths);
			String xpathArray[] = xpathlist.split(";");
			ArrayList<String> xpatharraylist = new ArrayList<String>();
			for(String s: xpathArray) {
				xpatharraylist.add(s);
			}
			return xpatharraylist;
		}
		else
			return null;
	}

	public boolean putChannelandUrls(String channelname, String url) {
		DatabaseEntry URL = new DatabaseEntry();
		DatabaseEntry channel = new DatabaseEntry();

		//System.out.println("Channelnameexists? "+channelname+" "+doesChannelExist(channelname));

		if(!doesChannelExist(channelname))
			return false;
		ArrayList<String> xpaths = new ArrayList<String>();
		if((xpaths = getChannelandUrls(channelname))!=null) {
			if(xpaths.contains(url))
				return true;
			xpaths.add(url);
			String str = "";
			for (String s: xpaths) {
				str = str + s +";";
			}
			str = str.substring(0,str.length()-1);
			url = str;
		}
		StringBinding.stringToEntry(channelname, channel);
		StringBinding.stringToEntry(url, URL);
		channelsUrlsDB.put(null, channel, URL);
		return true;
	}


	public ArrayList<String> getChannelandUrls(String channelname) {
		DatabaseEntry channel = new DatabaseEntry();
		DatabaseEntry urls = new DatabaseEntry();

		StringBinding.stringToEntry(channelname, channel);

		if(channelsUrlsDB.get(null, channel, urls, null) == OperationStatus.SUCCESS) {
			String urllist = StringBinding.entryToString(urls);
			String urlarray[] = urllist.split(";");
			ArrayList<String> ulist = new ArrayList<String>();
			for(String s: urlarray) {
				ulist.add(s);
			}
			return ulist;
		}
		else
			return null;
	}

	public boolean doesChannelExist(String channelName) {
		DatabaseEntry channelnames = new DatabaseEntry();
		StringBinding.stringToEntry(channelName, channelnames);
		DatabaseEntry owner = new DatabaseEntry();
		if(listOfChannelsDB.get(null,channelnames,owner,null)==OperationStatus.SUCCESS)
			return true;
		return false;
	}

	public boolean removeChannel(String username, String channelname) {
		DatabaseEntry user = new DatabaseEntry();
		DatabaseEntry channelnames = new DatabaseEntry();

		String uname2="";
		DatabaseEntry owner2 = new DatabaseEntry();
		DatabaseEntry channelname2 = new DatabaseEntry();
		StringBinding.stringToEntry(channelname, channelname2);
		if(listOfChannelsDB.get(null,channelname2,owner2,null)==OperationStatus.SUCCESS)
		{
			uname2 = StringBinding.entryToString(owner2);
			if(uname2.equals(username)){
				deleteChannel(channelname);
				return true;
			}
		}
		StringBinding.stringToEntry(username, user);
		System.out.println("Deleting"+username+channelname);
		if(userchannelsDB.get(null, user, channelnames, null) == OperationStatus.SUCCESS) {
			String channels = StringBinding.entryToString(channelnames);
			String revisedChannels = "";

			if(!channels.contains(";")) {
				userchannelsDB.removeSequence(null, user);
				return true;
			}

			String channelArray[] = channels.split(";");
			for(String s: channelArray) {
				if(!s.equals(channelname))
					revisedChannels = revisedChannels + s +";";
			}
			revisedChannels = revisedChannels.substring(0,revisedChannels.length()-1);
			DatabaseEntry revisedchannelnames = new DatabaseEntry();
			StringBinding.stringToEntry(revisedChannels,revisedchannelnames);
			userchannelsDB.put(null, user, revisedchannelnames);

			return true;
		}
		else
			return false;
	}

	public boolean deleteChannel(String cname) {
		DatabaseEntry channelname = new DatabaseEntry();
		StringBinding.stringToEntry(cname, channelname);
		String username="";
		DatabaseEntry owner = new DatabaseEntry();
		if(listOfChannelsDB.get(null,channelname,owner,null)==OperationStatus.SUCCESS)
		{
			username = StringBinding.entryToString(owner);
			listOfChannelsDB.removeSequence(null, channelname);
			removeChannel(username, cname);		
			channelsXpathDB.removeSequence(null, channelname);
			channelsUrlsDB.removeSequence(null, channelname);
			channelXSLDb.removeSequence(null, channelname);
			return true;
		}
		return false;
	}

	public String constructXML(String cname) throws ParseException {
		DatabaseEntry channelname = new DatabaseEntry();
		StringBinding.stringToEntry(cname, channelname);
		String xml = "<documentcollection>\n";
		for(String urls: getChannelandUrls(cname)) {
			Date d = getFileandDate(urls);
			long h = d.getHours();
			long m = d.getMinutes();
			long s = d.getSeconds();
			long da = d.getDate();
			long mo = d.getMonth()+1;
			long y = d.getYear()+1900;

			String date = String.valueOf(y)+"-"+String.valueOf(mo)+"-"+String.valueOf(da)+"T"+String.valueOf(h)+":"+String.valueOf(m)+":"+String.valueOf(s);
			//System.out.println(date);
				xml += "<document crawled=\""+date+"\" location=\""+urls+"\">";
				xml += getFileandContent(urls);
				xml += "</document>";
			
		}
		xml += "\n</documentcollection>";

//		System.out.println(xml);
		return xml;

	}

	public boolean putParams(String p, String v) {
		DatabaseEntry param = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();		
		StringBinding.stringToEntry(p, param);
		StringBinding.stringToEntry(v, value);
		parametersDb.put(null, value, param);
		return true;

	}

	public String getParams(String p) {
		DatabaseEntry param = new DatabaseEntry();
		DatabaseEntry value = new DatabaseEntry();

		StringBinding.stringToEntry(p, param);

		if(parametersDb==null)
			return "0";
		if(parametersDb.get(null, param, value, null) == OperationStatus.SUCCESS) {
			return StringBinding.entryToString(value);
		}
		return "0";
	}

}
