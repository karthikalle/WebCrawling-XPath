package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class Subscribe extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);

		HttpSession session = request.getSession();
		String username=(String) session.getAttribute("User");

		//	session.setAttribute("User", session.getAttribute("User"));

		//System.out.println("Inside show channel"+session.getAttribute("User"));
		String channelname = (String) request.getParameter("id");

		ArrayList<String> channelList = bdb.getChannel(username);

		System.out.println("Channelname:"+channelname);
		System.out.println("User Name:"+username);
		if(channelList!=null){
			if(channelList.contains(channelname))
			{
				out.println("<HTML><BODY>");
				out.println("<h2>Subscribe to Channel:</h2><h2>"+channelname+"</h2><br/><br/>");
				out.println("<h3>You are already Subscribed to this channel</h3><br/><br/>");
				out.println("<a href=\"/userpage\">Go to your Page</a>");
				out.println("</BODY></HTML>");		
				bdb.destroy();
				return;
			}
			else {
				bdb.putChannelSubscription(username, channelname);
				out.println("<HTML><BODY>");
				out.println("<h2>Subscribe to Channel:</h2><h2>"+channelname+"</h2><br/><br/>");
				out.println("<h3>Subscribed to channel!</h3><br/><br/>");
				out.println("<a href=\"/userpage\">Go to your Page</a>");
				out.println("</BODY></HTML>");		
				bdb.destroy();
				return;
			}
		}
		else {
			bdb.putChannel(username, channelname);
			bdb.putChannelSubscription(username, channelname);
			out.println("<HTML><BODY>");
			out.println("<h2>Subscribe to Channel:</h2><h2>"+channelname+"</h2><br/><br/>");
			out.println("<h3>Subscribed to channel!</h3><br/><br/>");
			out.println("<a href=\"/userpage\">Go to your Page</a>");
			out.println("</BODY></HTML>");		
			bdb.destroy();
			return;
		}
	}
}
