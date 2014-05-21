package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.util.ArrayList;

public class DeleteChannel extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Delete Channel</TITLE></HEAD><BODY>");

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);
		
		HttpSession session = request.getSession();
		String username = (String) session.getAttribute("User");

		String channelname = (String) request.getParameter("id");
		if(bdb.removeChannel(username,channelname))
		{
			out.println("Channel Deleted");
			out.println("<a href=\"/userpage\">Go To Your Page</a><br/>");

			out.println("</BODY></HTML>");		
		}
		else {
			out.println("Channel Cannot be Deleted");
			out.println("<a href=\"/userpage\">Go To Your Page</a><br/>");

			out.println("</BODY></HTML>");		
		}
		bdb.destroy();

	}

}
