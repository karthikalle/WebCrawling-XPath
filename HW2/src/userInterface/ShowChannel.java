package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class ShowChannel extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);

		//	HttpSession session = request.getSession();
		//	session.setAttribute("User", session.getAttribute("User"));

		//System.out.println("Inside show channel"+session.getAttribute("User"));
		String channelname = (String) request.getParameter("id");

		ArrayList<String> candurl = bdb.getChannelandUrls(channelname);
		//	System.out.println("Username:"+session.getAttribute("User"));

		System.out.println("Channelname:"+channelname);
		if(candurl==null)
		{
			response.setContentType("text/html");

			out.println("<HTML><HEAD><TITLE>Channel</TITLE></HEAD><BODY>");
			out.println("<h2>Channel:</h2><h3>"+channelname+"</h3><br/><br/>");
			out.println("<h3>List of matching URL's</h3><br/><br/>");
			out.println("No Matching Channels");
			out.println("</BODY></HTML>");		
			bdb.destroy();
			return;

		}

		try {
			response.setContentType("application/xml");

			out.println("<?xml version=\"1.0\"?>");
			//	out.println("<?xml-stylesheet href=\""+"hw2ms2/hw2ms2/test.xsl"+"\" type=\"text/xsl\" ?>");
			out.println("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">");			
			String content = (bdb.constructXML(channelname));
		//	System.out.println(content);
			out.println(content);
			out.println("</xsl:stylesheet>");
			bdb.destroy();
			return;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		bdb.destroy();
	}
}
