package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.util.ArrayList;

public class AddChannel extends HttpServlet {
	protected void doGet(HttpServletRequest request,
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

		out.println("<h2>Create your Channel</h2><br/><br/>");
		out.println("<form name=\"myform\" action = \"/createchannel\" method = \"POST\">");
		out.println("Channel name:            <input type = \"text\" name = \"channelname\"/ value = \"\"/><br/><br/>");
		out.println("Xpaths(seperated by ; ): <input type = \"text\" name = \"xpaths\" value = \"\"/><br/><br/>");
		out.println("Example:/rss/channel/description[contains(text(),\"news\")] ; /rss/channel/language[text()=\"en-us\"] ; /rss/channel/language[text()=\"en-gb\"] ; /rss/channel/language[text()=\"it-IT\"]");
		out.println("</br></br>XSL URL:                 <input type = \"text\" name = \"xslurl\" value = \"\"/><br/><br/>");
		out.println("<input type = \"submit\" value = \"Create Channel\"/></form><br/><br/>");
		out.println("</BODY></HTML>");		
		bdb.destroy();
	}

}
