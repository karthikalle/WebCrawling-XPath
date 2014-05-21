package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.util.ArrayList;

public class ListChannels extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Channel</TITLE></HEAD><BODY>");
		
		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);
				
		ArrayList<String> channels = bdb.getAllChannels();

		out.println("<h2>Channels List:</h2><br/><br/>");
		if(channels==null)
		{
			out.println("No Channels");
			out.println("</BODY></HTML>");		
			return;
		}
		for(int i = 0; i<channels.size(); i++) {
			out.println("<tr><td><a href=\"showchannel?"+"id="+channels.get(i)+"\">"+channels.get(i)+" show as xml"+"</a>&nbsp;&nbsp;&nbsp;</td>");
			out.println("<td><a href=\"showchannelxsl?"+"id="+channels.get(i)+"\">"+channels.get(i)+" show using xsl "+"</a>&nbsp;&nbsp;&nbsp;</td>");
		}
						
		out.println("</BODY></HTML>");		
		bdb.destroy();

	}
	
}
