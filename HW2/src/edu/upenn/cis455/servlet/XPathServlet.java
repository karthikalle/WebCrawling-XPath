package edu.upenn.cis455.servlet;

import edu.upenn.cis455.xpathengine.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.w3c.dom.Document;

@SuppressWarnings("serial")
public class XPathServlet extends HttpServlet {
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>XPATH User Interface</TITLE></HEAD><BODY>");
		out.println("** Karthik Alle: kalle<br/><br/>");
		out.println("Enter Xpath (use ; as delimitors) and Url as shown in the follwing example:</br></br>");
		out.println("/breakfast_menu/food[price[text()=\"$5.95\"]][calories[text()=\"650\"]][description[contains(text(),\"no\")]] ;  /breakfast_menu/food[price[text()=\"$5.95\"]][calories[text()=\"650\"]]</br>");
		out.println("</br>URL: </br>http://www.w3schools.com/xml/simple.xml<br/><br/>");
		out.println("<form name=\"myform\" action = \"/xpath\" method = \"POST\">"
				+ "Enter XPath: <input type = \"text\" value = \"\" name = \"xpath\"/><br/><br/>"
				+ "Enter URL: <input type = \"text\" value = \"\" name = \"url\"/><br/><br/>"
				+ "<input type = \"submit\" value = \"Parse\"/>"
				+ "</form>");
		out.println("You can enter either a URL or a local file path");
		out.println("As we have used ; as delimitors, it should not appear in the XPath");
		out.println("</BODY></HTML>");		
	}
	
	public void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		System.out.println("In post");
		PrintWriter out = response.getWriter();
		String xp = request.getParameter("xpath");
		String u = request.getParameter("url");
		//System.out.println(x+" "+u);		
		String xpath = URLDecoder.decode(xp, "UTF-8");
		System.out.println(xpath+" after decode");
		String url = URLDecoder.decode(u, "UTF-8");
		System.out.println(url+" after decode");

		/*if(!(url.endsWith(".html")||url.endsWith(".htm")||url.endsWith(".xml"))) {
			out.println("<HTML><HEAD><TITLE>XPATH User Interface</TITLE></HEAD><BODY>");
			out.println("** Karthik Alle: kalle<br/><br/><br/>");
			out.println("Invalid inputs");
			out.println("</BODY></HTML>");
			return;
		}*/
				
		XPathEngineImpl x = new XPathEngineImpl();
		Document d = x.parseFile(xpath, url);
		
		if(x.trimWhiteSpaces(xpath).length()==0||x.trimWhiteSpaces(url).length()==0){
			out.println("<HTML><HEAD><TITLE>XPATH User Interface</TITLE></HEAD><BODY>");
			out.println("** Karthik Alle: kalle<br/><br/><br/>");
			out.println("Invalid inputs");
			out.println("</BODY></HTML>");
		}
		
		String[] s = xpath.split(";");
		boolean[] b = x.evaluate(d);
			out.println("<HTML><HEAD><TITLE>XPATH User Interface</TITLE></HEAD><BODY>");
			out.println("** Karthik Alle: kalle<br/><br/><br/>");
			for(int i = 0; i<b.length;i++)
				out.println(b[i]+" for  XPath"+i+"<br/>");
			out.println("</BODY></HTML>");
		out.flush();
			
	}
	
}
