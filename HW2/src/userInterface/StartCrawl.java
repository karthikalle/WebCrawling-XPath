package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.crawler.XPathCrawler;
import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class StartCrawl extends HttpServlet {
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Start Crawling</TITLE></HEAD><BODY>");

		out.println("Started Crawling</br></br>");
		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		String storepath = (String) context.getInitParameter("BDBStore"); 
		String args[] = {"http://crawltest.cis.upenn.edu/",storepath, "50","100"};
		try {
			out.println(XPathCrawler.startCrawl(args));
			
			BerkleyDBWrapper bdb = new BerkleyDBWrapper();
			bdb.initialize(storepath);
			if(bdb.getAllChannels()!=null) {
				for(String s: bdb.getAllChannels())
				{
					out.println("</br></br>");

					ArrayList<String> u = bdb.getChannelandUrls(s);
					out.println("For channel:"+s+ " Num of Files matched: "+u.size());
					HashMap<String, Integer> hostmap = new HashMap<String, Integer>();
					for(String url: u) {
						String host = new URL(url).getHost();

						if(hostmap.get(url)==null)
							hostmap.put(url, 1);
						else {
							hostmap.put(url, hostmap.get(url)+1);
						}
					}
					String maxserver = "";
					int size = 0;
					for(String h: hostmap.keySet()) {
						if(hostmap.get(h)>=size){
							size = hostmap.get(h);
							maxserver = h;
						}
					}
					out.println("</br></br>");
					out.println("Server with most XML docs matching one of this channel: "+new URL(maxserver).getHost());

				}
			}
			bdb.destroy();
			out.println("</br></br>");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		out.println("</BODY></html>");

	}

}
