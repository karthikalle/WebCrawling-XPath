package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.util.ArrayList;

public class StopCrawl extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Stop Crawling</TITLE></HEAD><BODY>");

		out.println("Stopped Crawling</br></br>");
		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		String storepath = (String) context.getInitParameter("BDBStore"); 

		String args[] = {"http://crawltest.cis.upenn.edu/",storepath,"50","100"};
		
		XPathCrawler.stopCrawling();

		out.println("</BODY></html>");

	}

}
