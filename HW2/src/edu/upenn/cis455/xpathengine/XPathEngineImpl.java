package edu.upenn.cis455.xpathengine;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class XPathEngineImpl implements XPathEngine {
	String [] xpaths;
	String [][] nodes;
	boolean [] isInsideQuote;
	NodeList mainNodeList;

	public XPathEngineImpl() {
		// Do NOT add arguments to the constructor!!
	}

	public void setXPaths(String[] s) {
		if (s.length == 0 || s.equals(null)) {
			return;
		}
		int len = s.length;
		xpaths = new String[len];
		xpaths = s;
	}

	public boolean isValid(int i) {
		String xpath = xpaths[i];
		if(!xpath.startsWith("/"))
			return false;
		if(xpath.contains(":"))
			return false;
		xpath = trimWhiteSpaces(xpath);

		isInsideQuote = determineInsideQuote(xpath);
		if(isInsideQuote==null)
			return false;
		if(parenthesesisMatching(xpath)==null||parenthesesisMatching2(xpath)==null)
			return false;

		Pattern p = Pattern.compile("^[/]{1}.*(\\[{1}.*\\]{1})*([/]{1}.*)?");
		Matcher m = p.matcher(xpath);
		//System.out.println("\n"+xpath);
		if(m.matches() == false)
			return false;
		else {
			return takeStep(xpath.substring(1));
		}
	}

	private boolean[] determineInsideQuote(String xpath) {
		boolean[] isInQ = new boolean[xpath.length()];
		boolean flag = false;
		for(int i = 0; i<xpath.length() ; i++) {
			isInQ[i] = false;
			if(xpath.charAt(i)=='"'&&xpath.charAt(i-1)!='\\') {
				if(flag == true)
					flag = false;
				else 
					flag = true;
			}
			else {
				if(flag == true) {
					isInQ[i] = true;
				}
			}
		}
		/*
		//System.out.println("");
		for(int i = 0; i<xpath.length() ; i++) {
			//System.out.print(xpath.charAt(i));
		}
		//System.out.println("");
		for(int i = 0; i<xpath.length() ; i++) {
			if(isInQ[i]==true)
				//System.out.print("1");
			else
				//System.out.print("0");
		}
		 */
		//System.out.println("");
		if(flag!=false) {
			return null;
		}
		return isInQ;
	}

	public String trimWhiteSpaces(String xpath) {
		String x = new String();
		boolean waitFlag=false;
		for (int i = 0; i<xpath.length(); i++)
		{
			if(xpath.charAt(i)=='\"'){
				if(waitFlag == false)
					waitFlag = true;
				if(waitFlag ==true)
					waitFlag = false;
			}	
			if(xpath.charAt(i)==' ')
				continue;
			if(waitFlag != true) {
				x += xpath.charAt(i);
			}
		}
		return x;
	}

	private boolean takeStep(String step) {
		if(step.length()==0||step==null)
			return true;
		if(step.charAt(0)==('/')||step.charAt(0)==('"'))
			return false;
		
		////System.out.println("Step:"+step);
		Pattern p2 = Pattern.compile("^.+(\\[{1}.*\\]{1})*([/]{1}.*)?");	
		Matcher m2 = p2.matcher(step);

		//No node name for step
		if(m2.matches() == false) {
			//System.out.println("No node name");
			return false;
		}
		//	if(((step.contains("/")&&isInsideQuote[step.indexOf("/")]==false) && (!(step.contains("[")&&isInsideQuote[step.indexOf("[")]||(step.indexOf("/")<step.indexOf("[")))))) {
		if((step.contains("/") && (!step.contains("[")||(step.indexOf("/")<step.indexOf("["))))) {
			////System.out.println("Node "+step.substring(0,step.indexOf("/")));
			return(takeStep(step.substring(0,step.indexOf("/")))&&takeStep(step.substring(step.indexOf("/")+1)));
		}

		else {
			Pattern p = Pattern.compile("^.+(\\[{1}.*\\]{1})+([/]{1}.*)?");
			Matcher m = p.matcher(step);

			//If it contains filters
			if(m.matches() == true){
				////System.out.println(step.substring(step.indexOf("[")));
				//System.out.println("Filter for node:"+step.substring(0,step.indexOf("[")));
				String str = step.substring(step.indexOf("["));
				ArrayList<String> filters = new ArrayList<String>();
				if((filters = parenthesesisMatching(str))==null) {
					//System.out.println("Parenthesis not matched");
					return false;
				}
				else {
					for (String s : filters) {
						//System.out.println("CHecking Test");
						if(s.startsWith("/")) {
							if(!takeTest(s.substring(1,s.length())))
								return false;
						}
						else {
							if(!takeTest(s.substring(1,s.length()-1)))
								return false;
						}
					}
					//System.out.println("Applied filter to node "+step.substring(0,step.indexOf("[")));
				}
			}
			else {
				//System.out.println("Node "+step);
			}
			return true;
		}
	}

	private boolean takeStepE(String step, Node n) {
		if(step.length()==0||step==null)
			return true;
		//System.out.println("Step:"+step);
		//System.out.println("Node:"+n.getNodeName());
		Pattern p2 = Pattern.compile("^.+(\\[{1}.*\\]{1})*([/]{1}.*)?");	
		Matcher m2 = p2.matcher(step);

		//No node name for step
		if(m2.matches() == false) {
			//System.out.println("No node name");
			return false;
		}

		//If it has a / it gets the one before / and after /
		//	if(((step.contains("/")&&isInsideQuote[step.indexOf("/")]==false) && (!(step.contains("[")&&isInsideQuote[step.indexOf("[")]||(step.indexOf("/")<step.indexOf("[")))))) {
		if((step.contains("/") && (!step.contains("[")||(step.indexOf("/")<step.indexOf("["))))) {

			//System.out.println("calling stepe on "+n.getNodeName());

			//Call present node
			takeStepE(step.substring(0,step.indexOf("/")),n);

			boolean flag = false;

			//Check for each child
			NodeList nodes = n.getChildNodes();
			for(int i = 0 ;i <nodes.getLength(); i++){
				Node child = nodes.item(i);
				if(child.getNodeType() == Node.ELEMENT_NODE) {
					//System.out.println("Child name: "+child.getNodeName());
					//System.out.println("Step:"+step);
					//System.out.println("Step sub; "+step.substring(step.indexOf("/")+1,step.indexOf("[")));
//Major Change					
					if((step.substring(step.indexOf("/")+1,step.indexOf("["))).contains(child.getNodeName())){
					//if(child.getNodeName().equals(step.substring(step.indexOf("/")+1,step.indexOf("[")))){
						//System.out.println("calling stepe on child:"+child.getNodeName());

						//call child nodes
						if(takeStepE(step.substring(step.indexOf("/")+1),child)){
							//System.out.println("Matched "+step.substring(step.indexOf("/")+1)+" "+child.getNodeName());
							flag = true;
							break;
						}
					}
				}
			}
			if(flag == false)
				return false;
			else 
				return true;
		}

		else {
			//Check for filters
			Pattern p = Pattern.compile("^.+(\\[{1}.*\\]{1})+([/]{1}.*)?");
			Matcher m = p.matcher(step);

			//If it contains filters
			if(m.matches() == true){
				////System.out.println(step.substring(step.indexOf("[")));
				//System.out.println("Filter for node:"+step.substring(0,step.indexOf("[")));
				String str = step.substring(step.indexOf("["));
				ArrayList<String> filters = new ArrayList<String>();

				if((filters = parenthesesisMatching(str))==null) {
					//System.out.println("Parenthesis not matched");
					return false;
				}

				//Parenthesis Matched
				else {
					for (String s : filters) {
						//System.out.println("CHecking Test on:"+n.getNodeName());

						//If it has child nodes inside
						if(s.startsWith("/")) {
							NodeList nodes = n.getChildNodes();
							for(int i = 0 ;i <nodes.getLength(); i++){
								Node child = nodes.item(i);
								//System.out.println(child.getNodeName()+" "+s.substring(1,s.indexOf("[")));
								if(child.getNodeName().equals(s.substring(1,s.indexOf("[")))) {
									//System.out.println("calling stepe on child:"+child.getNodeName());
									if(!takeTestE(s.substring(1,s.length()),child)) {
										//System.out.println("Test Failed on filter:"+child.getNodeName());
										return false;
									}
								}
							}
						}
						else {

							//No child nodes inside
							NodeList nodes = n.getChildNodes();
							//System.out.println(s);
							//System.out.println(n.getNodeName());
							if(s.startsWith("[text()=")||s.startsWith("[@")||s.startsWith("[contains(text()"))
							{
								//System.out.println("A filter is there"+s.substring(1,s.length()-1));
								if(!takeTestE(s.substring(1,s.length()-1), n)) {
									//System.out.println("Test Failed on filter:"+s.substring(1,s.length()-1));
									return false;
								}

								else
									continue;

							}

							String s2 = s.substring(1,s.length()-1);
							if((s2.contains("/") && (!s2.contains("[")||(s2.indexOf("/")<s2.indexOf("["))))) {
								for(int i = 0 ;i <nodes.getLength(); i++){
									Node child = nodes.item(i);
									if (child.getNodeType() == Node.ELEMENT_NODE) {
										if(child.getNodeName().equals(s.substring(1,s.indexOf("/")))) {
											//System.out.println("calling stepe on child:"+child.getNodeName()+" and "+s.substring(1,s.indexOf("[",2)));
											if(takeStepE(s.substring(1,s.length()-1), child))
												return true;
											else 
												return false;
										}
									}
								}
							}
							else{

								for(int i = 0 ;i <nodes.getLength(); i++){
									Node child = nodes.item(i);
									if (child.getNodeType() == Node.ELEMENT_NODE) {
										//System.out.println(child.getNodeName()+" "+s.substring(1,s.indexOf("[",2)));
										if(child.getNodeName().equals(s.substring(1,s.indexOf("[",2)))) {
											//System.out.println("calling teste on child:"+child.getNodeName()+" and "+s.substring(1,s.indexOf("[",2)));
											if(!takeTestE(s.substring(1,s.length()-1),child)) {
												//System.out.println("test failed for child "+child.getNodeName());
												return false;
											}
										}
									}
								}
							}
						}
					}

					//System.out.println("Applied filter to node "+step.substring(0,step.indexOf("[")));
				}
			}
			else {
				//System.out.println(n.getNodeName());
				if(!(n.getNodeName().equals(step)))
					return false;
				//System.out.println("Node "+step);
			}
			return true;
		}
	}

	private boolean takeTest(String s) {
		//System.out.println("inside taketest: "+s);

		if (s.matches(("(text\\(\\)=\\\".*\\\")+"))) {
			//System.out.println(s+" Matches "+"text()=");
			return true;
		}

		else if (s.matches("contains\\(text\\(\\),\\\".*\\\"\\)")) {
			//System.out.println(s+" matches "+"contains(text(),\"\"");
			return true;
		}
		else if (s.matches("@.+=\".*\"")) {
			String att = s.substring(s.indexOf('@'),s.indexOf('"'));
			//System.out.println("Attribute"+att);
			//System.out.println(s+" matches "+"@att,\"\"");
			return true;
		}
		else {
			Pattern p2 = Pattern.compile("^.+(\\[{1}.*\\]{1})*([/]{1}.*)?");	
			Matcher m2 = p2.matcher(s);

			// If step
			if (m2.matches() == true) {
				//System.out.println("Got step");
				return takeStep(s);
			}
			else {
				//System.out.println("No match on test");
				return false;
			}
		}

	}

	private boolean takeTestE(String s, Node n) {
		boolean[] iN = determineInsideQuote(s);
		//System.out.println("inside taketest: "+s);
		//System.out.println("Testing on node:"+n.getNodeName());

		if (s.matches(("(text\\(\\)=\\\".*\\\")+"))) {
			//System.out.println(s+" Matches "+"text()=");
			int startIndex = 0, lastIndex = 0;
			for(int i = 0; i<s.length(); i++) {
				if(s.charAt(i)=='"'&&iN[i]==false) {
					if(startIndex == 0)
						startIndex = i;
					else {
						lastIndex = i;
						break;
					}
				}
			}
			//System.out.println("\n"+s.substring(startIndex+1,lastIndex));
			//System.out.println("n.textcontent "+n.getTextContent());
			if(n.getTextContent().equals(s.substring(startIndex+1,lastIndex))){
				//System.out.println("Match done: "+n.getTextContent());
				return true;
			}
			else
				return false;
		}

		else if (s.matches("(contains\\(text\\(\\),\\\".*\\\"\\))+")) {
			//System.out.println(s+" matches "+"contains(text(),\"\"");
			int startIndex = 0, lastIndex = 0;
			for(int i = 0; i<s.length(); i++) {
				if(s.charAt(i)=='"'&&iN[i]==false) {
					if(startIndex == 0)
						startIndex = i;
					else {
						lastIndex = i;
						break;
					}
				}
			}

			//System.out.println("\n"+s.substring(startIndex+1,lastIndex));
			//System.out.println("n.textcontent "+n.getTextContent());
			if(n.getTextContent().contains(s.substring(startIndex+1,lastIndex))){
				//System.out.println("Match done: "+n.getTextContent());
				return true;
			}
			else
				return false;
		}
		else if (s.matches("@.+=\".*\"")) {
			String att = s.substring(s.indexOf('@')+1,s.indexOf('"')-1);
			//System.out.println(s+" matches "+"@att,\"\"");
			//System.out.println("Attribute"+att);

			int startIndex = 0, lastIndex = 0;
			for(int i = 0; i<s.length(); i++) {
				if(s.charAt(i)=='"'&&iN[i]==false) {
					if(startIndex == 0)
						startIndex = i;
					else {
						lastIndex = i;
						break;
					}
				}
			}
			String value = s.substring(startIndex+1,lastIndex);
			//System.out.println("Value: "+value);
			if (n.hasAttributes()) {
				// get attributes names and values
				NamedNodeMap nodeMap = n.getAttributes();

				for (int i = 0; i < nodeMap.getLength(); i++) {
					Node node = nodeMap.item(i);
					//System.out.println("attr name : " + node.getNodeName());
					//System.out.println("attr value : " + node.getNodeValue());	
					if(node.getNodeName().equals(att)&&node.getNodeValue().equals(value)) {
						//System.out.println("Match SUccess");
						return true;
					}
				} 

			}
			else{
				//System.out.println("Match failed:"+att+"="+value);
				return false;
			}
		}
		else {
			Pattern p2 = Pattern.compile("^.+(\\[{1}.*\\]{1})*([/]{1}.*)?");	
			Matcher m2 = p2.matcher(s);

			// If step
			if (m2.matches() == true) {
				//System.out.println("Got step "+n.getNodeName());
				if(takeStepE(s,n)){
					//System.out.println("Match done: "+n.getNodeName());
					return true;
				}
				else {
					//System.out.println("Failed "+n.getNodeName());
					return false;
				}		

			}
			else {
				//System.out.println("No match on test");
				return false;
			}
		}
		return false;
	}

	private ArrayList<String> parenthesesisMatching(String str) {
		//System.out.println(str);
		boolean[] in = new boolean[str.length()];
		in = determineInsideQuote(str);
		if(in.equals(null))
			return null;
		int count = 0, startIndex= 0 , endIndex= 0;
		ArrayList<String> list = new ArrayList<String>();
		for (int i =0 ;i<str.length(); i++) {
			//	if(str.charAt(i)==('[')&&isInsideQuote[i]==false) {
			////System.out.println(str.charAt(i)+" "+in[i]);
			if((str.charAt(i)==('[')&&in[i]==false)) {
				count ++;
				if(count == 0) {
					startIndex = i;
				}
			}
			//	else if(str.charAt(i)==(']')&&isInsideQuote[i]==false){
			else if((str.charAt(i)==(']')&&in[i]==false)){
				count --;
				if(count == 0) {
					endIndex = i;
					//System.out.println(str.substring(startIndex,endIndex+1));
					list.add(str.substring(startIndex,endIndex+1));
					startIndex = i+1;
				}
			}
		}
		if(count != 0)
			return null;
		return list;
	}


	private ArrayList<String> parenthesesisMatching2(String str) {
		//System.out.println(str);
		boolean[] in = new boolean[str.length()];
		in = determineInsideQuote(str);
		if(in.equals(null))
			return null;
		int count = 0, startIndex= 0 , endIndex= 0;
		ArrayList<String> list = new ArrayList<String>();
		for (int i =0 ;i<str.length(); i++) {
			//	if(str.charAt(i)==('[')&&isInsideQuote[i]==false) {
			////System.out.println(str.charAt(i)+" "+in[i]);
			if((str.charAt(i)==('(')&&in[i]==false)) {
				count ++;
				if(count == 0) {
					startIndex = i;
				}
			}
			//	else if(str.charAt(i)==(']')&&isInsideQuote[i]==false){
			else if((str.charAt(i)==(')')&&in[i]==false)){
				count --;
				if(count == 0) {
					endIndex = i;
					//System.out.println(str.substring(startIndex,endIndex+1));
					list.add(str.substring(startIndex,endIndex+1));
					startIndex = i+1;
				}
			}
		}
		if(count != 0)
			return null;
		return list;
	}

	public boolean[] evaluate(Document d) { 
		//System.out.println(d.getDocumentElement().getNodeName());
		boolean[] b = new boolean[xpaths.length];
		for(int i = 0; i<xpaths.length; i++) {
			if(isValid(i)==false){
				b[i]=false;
				continue;
			}
			//System.out.println("trying "+xpaths[i]);
			xpaths[i]= trimWhiteSpaces(xpaths[i]);
			Node rootNode = d.getLastChild();
			//System.out.println("Here"+d.getLastChild().getNodeName());

			b[i] = takeStepE(xpaths[i].substring(1),rootNode);
			//System.out.println(b[i]);
		}
/*		System.out.println("");
		for(boolean x: b)
			System.out.print(x);*/
		return b;
	}

	public Document parseFile(String xpath, String Url) {

		String[] xpaths = xpath.split(";");
		this.setXPaths(xpaths);
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
				//finalHeaders = finalHeaders + "User-Agent: cis555\r\n";
				finalHeaders = finalHeaders + "Connection: close\r\n\r\n";

				// i SEND THIS REQUEST
				out.write(finalHeaders);
				out.flush();


				//System.out.println("Reading buffer");
				while((readBuffer=in.readLine())!=null)
				{
					//if(readBuffer.contains("<?xml"))
					
					if(readBuffer.trim().length()==0)
						break;
					//System.out.println(readBuffer);
				}
				String content = "";

				//System.out.println("Body");
				while((readBuffer=in.readLine())!=null)
				{
					if(readBuffer.contains("doctype"))
							continue;
					content += readBuffer;
					content += "\n";
					//System.out.println(readBuffer);
				}
				File file = new File("this.xml");

				// if file doesnt exists, then create it
				if (!file.exists()) {
					file.createNewFile();
				}

				FileWriter fw = new FileWriter(file.getAbsoluteFile());
				BufferedWriter bw = new BufferedWriter(fw);
				bw.write(content);
				bw.close();

				//System.out.println("Done");

				if(!urlLink.endsWith("xml"))
				{
					//System.out.println("inside tidy block");
					Tidy tidy = new Tidy(); 
					InputStream is = new ByteArrayInputStream(content.getBytes());
					Document doc = tidy.parseDOM(is,null);
					doc.getDocumentElement().normalize();
					//System.out.println("now"+doc.getDocumentElement().getNodeName());
					return doc;
				}

				File fXmlFile = new File("this.xml"); 
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				dbFactory.setIgnoringComments(true);
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				try {
				Document doc = dBuilder.parse(fXmlFile);
				
				
				doc.getDocumentElement().normalize();
				//return evaluate(doc)[0]; 


				return doc;
				}
				catch(Exception e) {
					return null;
				}
			}
			else {
				//System.out.println("\n\n\n\nRead File");
				File fXmlFile = new File(urlLink); 
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				dbFactory.setIgnoringComments(true);
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				//return evaluate(doc)[0];
				return doc;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}


		return null;
	}
}