package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;

public class CreateChannel extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Add Channel</TITLE></HEAD><BODY>");

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);

		HttpSession session = request.getSession();
		String uname = (String) session.getAttribute("User");
		System.out.println(uname);

		session.setAttribute("User", uname);

		String channelname = request.getParameter("channelname");


		if(!bdb.putChannel(uname, channelname)) {
			out.println("Channel Name Already Exists<br/><br/>");
		}

		else {

			String xpaths = request.getParameter("xpaths");
			String xslurl = request.getParameter("xslurl");	

			String xpath = URLDecoder.decode(xpaths, "UTF-8");
			bdb.putChannelandXpathsString(channelname, xpath);
			bdb.putChannelandXSLUrl(channelname, xslurl);
			out.println("Channel Successfully Created<br/><br/>");
			out.println("<a href=\"/userpage\">Go to your Page</a><br/>");
			
			XPathCrawler c = new XPathCrawler();
			c.initParams = new HashMap<String, String>();
			c.initParams.put("bdbDirectory", storepath);
			c.matchToAllUrls(channelname);
		}

		out.println("</BODY></HTML>");		
		bdb.destroy();

	}

}
