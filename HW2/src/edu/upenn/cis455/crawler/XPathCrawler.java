package edu.upenn.cis455.crawler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.upenn.cis455.storage.*;
import edu.upenn.cis455.xpathengine.XPathEngineImpl;

public class XPathCrawler {

	public static HashMap<String, String> disallowFiles;
	public static HashMap<String, Integer> crawlDelays;
	public static HashMap<String, Long> whenToRequest;
	public static ArrayList<String> hosts;
	public static int numofFiles = 0, maxNumOfFiles, maxSize, datasize = 0, numofxmlfiles=0, numofhtmlfiles=0;
	public static ArrayList<String> alreadyVisited;
	public static ArrayList<String> checkURL;

	public static HashMap<String, HashMap<String, Date>> alreadyVisited_host;
	public static HashMap<String, String> initParams;
	public static HashMap<String, String> filesContent;
	public static Queue<String> urlList;

	public static void main(String args[]) throws InterruptedException, MalformedURLException
	{
		//If invalid number of arguments
		if(args.length<3) {
			return;
		}

		startCrawl(args);
	}

	public static String startCrawl(String[] args) throws MalformedURLException,
	InterruptedException {

		disallowFiles = new HashMap<String, String>();
		crawlDelays = new HashMap<String, Integer>();
		whenToRequest = new HashMap<String, Long>();
		hosts = new ArrayList<String>();
		alreadyVisited = new ArrayList<String>();
		alreadyVisited_host = new HashMap<String, HashMap<String,Date>>();
		filesContent = new HashMap<String, String>();
		initParams = new HashMap<String, String>();
		initParams.put("firstPage", args[0]);

		initParams.put("bdbDirectory",args[1]);
		File f = new File(initParams.get("bdbDirectory"));
		System.out.println("File "+initParams.get("bdbDirectory")+f.exists());
		if(!f.exists()) {
			System.out.println("BDB Does not exist, creating new one");
			f.mkdirs();
		}
		initParams.put( "maxSize", args[2]);
		maxSize = Integer.parseInt(args[2]);

		numofFiles = 0;
		maxNumOfFiles =0;
		datasize = 0;
		numofxmlfiles=0;
		numofhtmlfiles=0;

		if(args.length>3) {
			initParams.put("maxNumOfFiles",args[3]);
		}
		pullfrombdb();
		crawl(initParams);
		pushtobdb();
		return "Num of HTML Files "+numofhtmlfiles+"<br/><br/>Num of XML Files "+numofxmlfiles+"<br/><br/>Num of hosts "+hosts.size()+
				"<br/><br/>Data Size "+datasize+" Bytes";
	}

	public static void crawl(HashMap<String, String> initParams) throws InterruptedException {
		urlList = new LinkedList<String>();
		checkURL = new ArrayList<String>();
		
		urlList.add(initParams.get("firstPage"));
		Document doc = null;
		if(initParams.containsKey("maxNumOfFiles"))
			maxNumOfFiles = Integer.parseInt(initParams.get("maxNumOfFiles"));
		else
			maxNumOfFiles = 10000;

		while(urlList.size()!=0 && alreadyVisited.size()<=maxNumOfFiles) {
			try {
				if(getHrefs(doc, urlList)) {
					numofFiles++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect(initParams.get("bdbDirectory"));
		for(String u: bdb.getAllFiles()) {
			//System.out.println(u);
			matchAllChannels(u);
		}
	}

	public static boolean getHrefs(Document d, Queue<String> urlList) throws IOException, InterruptedException {
		String url = urlList.remove();
		String host = "";

		try {
			host = new URL(url).getHost();
		}
		catch(MalformedURLException e) {
			//urlList.add(url+"/");
			return false;
		}

		//		if(url.equals("http://crawltest.cis.upenn.edu/international/peoplesdaily_world.xml"))
		//			return true;
		/*
		if(alreadyVisited_host.get(host)!=null) {
			if(alreadyVisited_host.get(host).containsKey(url)) {
				return false;
			}
		}
		 */
		if(alreadyVisited.contains(url))
			return false;

		if(!isAccessible(urlList, url, host)){
			System.out.println(url+": Access Restricted");
			return false;
		}
	//	matchAllChannels(url);

		String body2 = getBody(url);

		checkURL.add(url);
		
		BerkleyDBWrapper bdb2 = new BerkleyDBWrapper();
		bdb2.connect(initParams.get("bdbDirectory"));
		//System.out.println(body2);
		if(bdb2.getContentSeen(body2)) {
			System.out.println(url+": content already seen");
			try{
				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("a[href]");
				for (Element link : links){
					String ahref = link.attr("abs:href");

					if(disallowFiles.containsKey(ahref)||disallowFiles.containsKey(ahref+"/")) {
						continue;
					}
					if(!urlList.contains(ahref))
						urlList.add(ahref);
				}

				alreadyVisited.add(url);
			}
			catch(org.jsoup.HttpStatusException e){
				return false;
			}
			return false;
		}
		bdb2.destroy();

		if(!isCrawlingRequired(url, host)){
			System.out.println(url+": Crawling not required");
			//filesContent.put(url, getBody(url));
			try{

				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("a[href]");
				for (Element link : links){
					String ahref = link.attr("abs:href");

					if(disallowFiles.containsKey(ahref)||disallowFiles.containsKey(ahref+"/")) {
						continue;
					}
					if(!urlList.contains(ahref)) {
						urlList.add(ahref);
					}
				}
				alreadyVisited.add(url);
			}
			catch(org.jsoup.HttpStatusException e){
				return false;
			}

			return false;
		}
		else {
			String body = getBody(url);

			BerkleyDBWrapper bdb = new BerkleyDBWrapper();
			bdb.connect(initParams.get("bdbDirectory"));

			if(!bdb.getContentSeen(body)) {
				System.out.println(url+": Downloading");

				filesContent.put(url, body);
				bdb.putContentSeen(body);

				//matchAllChannels(url);

				Document doc = Jsoup.connect(url).get();
				Elements links = doc.select("a[href]");
				for (Element link : links){
					String ahref = link.attr("abs:href");
					int flag = 1;

					if(disallowFiles.containsKey(ahref)||disallowFiles.containsKey(ahref+"/")) {
						System.out.println(disallowFiles);
						flag = 0;
					}

					if(flag==1) {
						if(!urlList.contains(ahref))
							urlList.add(ahref);
					}
				}
				alreadyVisited.add(url);
				bdb.putFileandContent(url, "");

			}
			else {
				System.out.println(url+ ": Content already seen");
			}
			bdb.destroy();
			return true;
		}

	}

	public static boolean isAccessible(Queue<String> urlList, String url, String host) 
	{
		////System.out.println("Checking if we can access: "+url);
		if(!hosts.contains(host)) {
			getRobotsTxt(url);
			hosts.add(host);
		}
		if(crawlDelays.containsKey(host)){
			if(whenToRequest.containsKey(host)){
				if(System.currentTimeMillis()<whenToRequest.get(host)){
					urlList.add(url);
					return false;
				}
			}
			//System.out.println("Can access "+url+" again after: "+crawlDelays.get(host)+" seconds");
			//whenToRequest.put(host, System.currentTimeMillis()+crawlDelays.get(host)*1000);
			whenToRequest.put(host, System.currentTimeMillis()+crawlDelays.get(host)*1);
		}
		return true;
	}

	public static boolean isCrawlingRequired(String url, String host) 
	{
		////System.out.println("Checking if crawling is required for: "+url);

		HashMap<String, String> reqHeaders = getHeaders(url);
		if(reqHeaders==null) {
			return false;
		}

		String contentType = reqHeaders.get("Content-Type");


		if(!(contentType.contains("text/html")||contentType.contains("text/xml")||contentType.contains("application/xml")||contentType.contains("+xml")))
			return false;

		int size = 0;
		if(reqHeaders.containsKey("Content-Length")) {
			size = Integer.parseInt(reqHeaders.get("Content-Length"));
			////System.out.println("Size:"+(size/(1024*1024)));
			if((size/(1024*1024))>maxSize)
				return false;
			datasize += size;
		}

		Date actualModifiedDate = new Date(0);

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect(initParams.get("bdbDirectory"));

		HashMap<String, Date> alreadyDate;
		if(reqHeaders.get("Last-Modified")!=null) {
			actualModifiedDate = new Date(reqHeaders.get("Last-Modified"));
		}

		if(!alreadyVisited_host.containsKey(host)) {
			HashMap<String, Date> newURLDate = new HashMap<String, Date>();
			//	System.out.println("\nPutting"+url+actualModifiedDate);
			newURLDate.put(url, actualModifiedDate);
			bdb.putFileandDate(url, actualModifiedDate);
			bdb.destroy();

			alreadyVisited_host.put(host, newURLDate);
			return true;
		}

		else {
			HashMap<String, Date> a = alreadyVisited_host.get(host);
			Date recordedModifiedDate = new Date(0);
			if((recordedModifiedDate = a.get(url))!=null){
				////System.out.println(recordedModifiedDate.compareTo(actualModifiedDate));
				//System.out.println("\nActual modified date: "+actualModifiedDate);
				//System.out.println("Recorded modified date: "+recordedModifiedDate);

				if((recordedModifiedDate.compareTo(actualModifiedDate)<0)){
					if(reqHeaders.get("Content-Type").equals("text/html"))
						numofhtmlfiles++;
					if(reqHeaders.get("Content-Type").endsWith("xml"))
						numofxmlfiles++;
					//	System.out.println("\nPutting "+url+" "+actualModifiedDate);
					a.put(url, actualModifiedDate);
					bdb.putFileandDate(url, actualModifiedDate);
					bdb.destroy();
					alreadyVisited_host.put(host, a);
					return true;
				}
				else {
					datasize -= size;
					return false;
				}
			}
			else
			{
				a.put(url, actualModifiedDate);
				bdb.putFileandDate(url, actualModifiedDate);
				bdb.destroy();

				alreadyVisited_host.put(host, a);
				//	System.out.println("\nPutting "+url+" "+actualModifiedDate);

				if(reqHeaders.get("Content-Type").equals("text/html"))
					numofhtmlfiles++;
				if(reqHeaders.get("Content-Type").endsWith("xml"))
					numofxmlfiles++;
				return true;
			}
		}
	}

	@SuppressWarnings("resource")
	private static HashMap<String, String> getHeaders(String url) {
		//System.out.println("Getting headers for: "+url);

		String hostName = null;
		URL address = null;
		String urlLink = url;
		PrintWriter out = null;
		BufferedReader in = null;
		String path = null;
		Socket socket = null;
		HashMap<String,String> requestHeaders = new HashMap<String,String>();

		try {
			address = new URL(urlLink);
			hostName = address.getHost();
			path = address.getPath();
			socket = new Socket(hostName,80);
			String readBuffer = null;

			out = new PrintWriter(socket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			//System.out.println("Path:"+path);
			String finalHeaders = "HEAD "+url+" HTTP/1.1\r\n";
			finalHeaders = finalHeaders + "Host: "+hostName+"\r\n";
			finalHeaders = finalHeaders + "User-Agent: cis455crawler\r\n";
			finalHeaders = finalHeaders + "Connection: close\r\n\r\n";

			out.write(finalHeaders);
			out.flush();


			while((readBuffer=in.readLine())!=null)
			{
				if((readBuffer.contains("HTTP/1.0")||readBuffer.contains("HTTP/1.1"))){
					if(readBuffer.contains("HTTP/1.0 301")||readBuffer.contains("HTTP/1.1 301")||readBuffer.contains("HTTP/1.0 302")||readBuffer.contains("HTTP/1.1 302")) {
						while(!(readBuffer = in.readLine()).contains("Location"))
						{			
						}
						//	System.out.println(readBuffer);

						System.out.println(url + " \nRedirected to" + readBuffer.substring(readBuffer.indexOf(": ")+2, readBuffer.length()));
						System.out.println();
						urlList.add(readBuffer.substring(readBuffer.indexOf(": ")+2, readBuffer.length()));
					}
					if(!(readBuffer.contains("HTTP/1.0 20")||readBuffer.contains("HTTP/1.1 20")))
						return null;
				}
				if(readBuffer.contains(":")) {
					String s[] = readBuffer.split(": ");
					requestHeaders.put(s[0], s[1]);
				}
				if(readBuffer.trim().length()==0)
					break;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}

		return requestHeaders;
	}

	@SuppressWarnings("resource")
	public static String getBody(String Url) {

		String hostName = null;
		URL address = null;

		String urlLink = Url;

		PrintWriter out = null;
		BufferedReader in = null;


		String path = null;

		Socket socket = null;
		try {
			File f = new File(urlLink);
			if(!f.exists()) {
				address = new URL(urlLink);
				hostName = address.getHost();
				path = address.getPath();
				socket = new Socket(hostName,80);
				String readBuffer = null;

				out = new PrintWriter(socket.getOutputStream(),true);
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

				String finalHeaders = "GET "+path+" HTTP/1.1\r\n";
				finalHeaders = finalHeaders + "Host: "+hostName+"\r\n";
				finalHeaders = finalHeaders + "User-Agent: cis455crawler\r\n";
				finalHeaders = finalHeaders + "Connection: close\r\n\r\n";

				// i SEND THIS REQUEST
				out.write(finalHeaders);
				out.flush();


				////System.out.println("Reading buffer");
				while((readBuffer=in.readLine())!=null)
				{
					if(readBuffer.trim().length()==0)
						break;
					////System.out.println(readBuffer);
				}
				String content = "";

				////System.out.println("Body");
				while((readBuffer=in.readLine())!=null)
				{
					if(readBuffer.contains("<?xml version=\"1.0\" encoding=\"GB2312\"?>"))
						return "";

					if(readBuffer.contains("doctype")||readBuffer.contains("<?xml"))
						continue;
					content += readBuffer;
					content += "\n";
				}

				if(content.equals("")&&!Url.endsWith("/")) {
				//	System.out.println("Getting body for "+Url);
					content = getBody(Url+"/");
				}
				//System.out.println(content);
				return content;
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	@SuppressWarnings("resource")
	private static void getRobotsTxt(String url) {
		String hostName = null;
		URL address = null;
		String urlLink = url+"/robots.txt";
		PrintWriter out = null;
		BufferedReader in = null;
		String path = null;
		Socket socket = null;
		HashMap<String,String> disallowfor455 = new HashMap<String,String>();
		HashMap<String,String> disallowforeveryone = new HashMap<String,String>();
		HashMap<String,Integer> crawldelayfor455 = new HashMap<String,Integer>();
		HashMap<String,Integer> crawdelayforeveryone = new HashMap<String,Integer>();

		try {
			address = new URL(urlLink);
			hostName = address.getHost();
			path = address.getPath();
			socket = new Socket(hostName,80);
			String readBuffer = null;

			out = new PrintWriter(socket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			String finalHeaders = "GET "+path+" HTTP/1.1\r\n";
			finalHeaders = finalHeaders + "Host: "+hostName+"\r\n";
			finalHeaders = finalHeaders + "User-Agent: cis455crawler\r\n";
			finalHeaders = finalHeaders + "Connection: close\r\n\r\n";

			out.write(finalHeaders);
			out.flush();

			//System.out.println("U:"+url.substring(0,url.length()-1));
			url = url.substring(0,url.length());
			while((readBuffer=in.readLine())!=null)
			{
				if(readBuffer.equals("HTTP/1.1 404 Not Found")||readBuffer.contains("HTTP/1.1 403"))
					return;
				if(readBuffer.trim().length()==0)
					break;
			}

			String host = address.getHost();

			////System.out.println("Body");
			int flag = 0;
			while((readBuffer=in.readLine())!=null)
			{
				if(readBuffer.contains("User-agent: ")) {
					if(readBuffer.split(":")[1].equals(" cis455crawler")) 
						flag = 1;
					else
						flag = 0;
					continue;
				}
				if(flag == 1) {	
					if(readBuffer.contains("Disallow:")) {
						////System.out.println("inserting into disallow:"+readBuffer.split(": ")[0]+": "+readBuffer.split(": ")[1]);
						disallowfor455.put(url+readBuffer.split(": ")[1],"d");
						////System.out.println(readBuffer);
					}
					else if(readBuffer.contains("Crawl-delay:")) {
						////System.out.println("inserting into crawdelay:"+readBuffer.split(": ")[0]+": "+readBuffer.split(": ")[1]);
						crawldelayfor455.put(host,Integer.parseInt(readBuffer.split(": ")[1]));
						////System.out.println(readBuffer);
					}
				}
				else {
					if(readBuffer.contains("Disallow:")||readBuffer.contains("Crawl-delay:")) {
						System.out.println(readBuffer);
						if(readBuffer.split(": ").length>2)
							disallowforeveryone.put(url+readBuffer.split(": ")[1],"d");
						////System.out.println(readBuffer);
					}
					else if(readBuffer.contains("Crawl-delay:")) {
						////System.out.println("inserting into crawdelay:"+readBuffer.split(": ")[0]+": "+readBuffer.split(": ")[1]);
						System.out.println(readBuffer);
						crawdelayforeveryone.put(host,Integer.parseInt(readBuffer.split(": ")[1]));
						////System.out.println(readBuffer);
					}
				}
			}

			if(disallowfor455.size()==0) 
				disallowfor455 = disallowforeveryone;

			if(crawldelayfor455.size()==0) 
				crawldelayfor455 = crawdelayforeveryone;

			socket.close();
			out.close();
			in.close();
			disallowFiles = disallowfor455;
			crawlDelays = crawldelayfor455;
		}
		catch (Exception e){
			e.printStackTrace();
		}


	}

	public static void pullfrombdb() throws MalformedURLException {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();

		try 
		{
			bdb.connect(initParams.get("bdbDirectory"));
		}
		catch(Exception e) {
			bdb.initialize(initParams.get("bdbDirectory"));
		}

		if(bdb.getAllFiles()==null) {
			System.out.println("No data in BDB");
			return;
		}

		HashMap<String,Date> url = new HashMap<String, Date>();
		System.out.println("\n\n"+bdb.getAllFiles().size());

		for(String s: bdb.getAllFiles()) {
			Date d = bdb.getFileandDate(s);
			//	System.out.println("Pulling file and date "+s+" "+d);
			//System.out.println("Date:"+d);
			url.put(s, d);
		}

		for(String u: url.keySet()) {
			String host = new URL(u).getHost();
			if(!alreadyVisited_host.containsKey(host)) {
				//	System.out.println("Pulling host and file "+host+" "+url);

				alreadyVisited_host.put(host, url);
			}
		}

		numofhtmlfiles = Integer.parseInt(bdb.getParams("numofhtmlfiles"));
		numofxmlfiles =Integer.parseInt(bdb.getParams("numofxmlfiles"));
		bdb.destroy();

	}

	public static void pushtobdb() {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect(initParams.get("bdbDirectory"));
		for(String s: alreadyVisited_host.keySet()) {
			HashMap<String,Date> url = alreadyVisited_host.get(s);
			for(String u : url.keySet()) {
				//System.out.println("Pushing url and date:"+u+" "+url.get(u));
				bdb.putFileandDate(u, url.get(u));
				bdb.putFileandContent(u, filesContent.get(u));
				//	System.out.println("\n\n"+bdb.getAllFiles().size());

			}
		}
		/*		for(String f: filesContent.keySet()) {
			System.out.println("Pushing "+f+" "+"Content");
			bdb.putFileandContent(f, filesContent.get(f));
		}
		 */
		System.out.println("Num of HTML Files "+numofhtmlfiles);
		System.out.println("Num of XML Files "+numofxmlfiles);
		System.out.println("Num of hosts "+hosts.size());
		System.out.println("Data Size "+datasize+" Bytes");

		bdb.putParams("numofhtmlfiles", String.valueOf(numofhtmlfiles));
		bdb.putParams("numofxmlfiles", String.valueOf(numofxmlfiles));
		bdb.putParams("numofservers", String.valueOf(hosts.size()));
		bdb.putParams("datasize", String.valueOf(datasize));

		bdb.destroy();
	}

	public static void matchAllChannels(String u) {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect(initParams.get("bdbDirectory"));
		XPathEngineImpl xpatheng = new XPathEngineImpl();
		if(bdb.getAllChannels()==null)
			return;
		for(String c: bdb.getAllChannels()) {
			System.out.println("Channel here:"+c);
			String x = bdb.getChannelandXpathString(c);
			if(u==null||x==null)
				continue;
			org.w3c.dom.Document d = xpatheng.parseFile(x, u);
			boolean[] rs = xpatheng.evaluate(d);
			int flag = 0;
			for(int i = 0; i<rs.length; i++)
				if(rs[i]==true)
					flag = 1;
			if(flag == 1){
				System.out.println("Putting "+c+" "+u);
				bdb.putChannelandUrls(c, u);
			}

		}
		bdb.destroy();
	}

	public static void matchToAllUrls(String c) {
		System.out.println("Channel:"+c);
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect(initParams.get("bdbDirectory"));
		XPathEngineImpl xpatheng = new XPathEngineImpl();
		if(bdb.getAllFiles()==null)
			return;
		for(String u: bdb.getAllFiles()) {
			//System.out.println(c);
			System.out.println("URL:"+u);

			String x = bdb.getChannelandXpathString(c);
			System.out.println("Xpath"+x);
			if(u==null||x==null)
				continue;
			org.w3c.dom.Document d = xpatheng.parseFile(x, u);
			boolean[] rs = xpatheng.evaluate(d);
			int flag = 0;
			for(int i = 0; i<rs.length; i++)
				if(rs[i]==true)
					flag = 1;
			if(flag == 1){
				bdb.putChannelandUrls(c, u);
			}

		}
		bdb.destroy();
	}

	public static void matchEverything(String c) {
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		bdb.connect(initParams.get("bdbDirectory"));
		XPathEngineImpl xpatheng = new XPathEngineImpl();
		String x = bdb.getChannelandXpathString(c);
		//System.out.println(x);
		for(String u: bdb.getAllFiles()) {
			if(u==null||x==null)
				continue;
			org.w3c.dom.Document d = xpatheng.parseFile(x, u);
			boolean[] rs = xpatheng.evaluate(d);
			int flag = 0;
			for(int i = 0; i<rs.length; i++)
				if(rs[i]==true)
					flag = 1;
			if(flag == 1){
				bdb.putChannelandUrls(c, u);
			}
		}

		//	}
		bdb.destroy();
	}

	public static void stopCrawling() {
		urlList.clear();
		System.exit(0);
	}

}
