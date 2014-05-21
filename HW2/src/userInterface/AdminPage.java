package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class AdminPage extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Login Page</TITLE></HEAD><BODY>");

		String uname = request.getParameter("username");
		String pwd = request.getParameter("password");


		if(!uname.equals("admin")||!pwd.equals("admin")||uname==null||pwd==null) {
			out.println("<P>Wrong name or password</P>");
			out.println("<a href=\"/loginpage\">Try Again</a><br/>");
			out.println("<a href=\"/createAccount\">Create a new Account</a><br/>");
			out.println("</BODY></HTML>");		
			return;
		}

		out.println("<P><h2>Welcome To Admin </h2><br/><br/></P>");


		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);


		String numofhtml = bdb.getParams("numofhtmlfiles");
		String numofxml = bdb.getParams("numofxmlfiles");
		String numofs = bdb.getParams("numofservers");
		String datasize = bdb.getParams("datasize");

		out.println("Num of html files: "+numofhtml);
		out.println("</br></br>");
		out.println("Num of xml files: "+numofxml);
		out.println("</br></br>");
		out.println("Num of Servers: "+numofs);
		out.println("</br></br>");
		out.println("Data downloaded: "+datasize);

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
		out.println("</br></br>");
		bdb.destroy();


		out.println("<form name=\"myform\" action = \"/startcrawl\" method = \"GET\">");
		out.println("<input type = \"submit\"value =\"Start Crawl\"/>");
		out.println("<form name=\"myform2\" action = \"/stopcrawl\" method = \"GET\">");
		out.println("<input type = \"submit\"value =\"Stop Crawl\"/>");

		out.println("</BODY></HTML>");		
	}

}
