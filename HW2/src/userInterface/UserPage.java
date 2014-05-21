package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.util.ArrayList;

public class UserPage extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Your Page</TITLE></HEAD><BODY>");

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);
		String uname = "";
		try {

		HttpSession session = request.getSession();
		uname = (String) session.getAttribute("User");
	//	System.out.println(uname);

		session.setAttribute("User", uname);
		}
		catch(NullPointerException e) {
			out.println("<a href=\"loginpage?></a>");
			out.println("</body></html>");
			return;
		}

		out.println("<h2>Welcome to "+uname+"</h2><br/><br/>");

		out.println("<form name=\"myform\" action = \"/logout\" method = \"GET\">");
		out.println("<input type = \"submit\" value = \"logout\"/></form>");

		out.println("<form name=\"myform2\" action = \"/addchannel\" method = \"GET\">");
		out.println("<input type = \"submit\" value = \"Add A Channel\"/></form>");

		out.println("<form name=\"myform3\" action = \"/listandsubscribe\" method = \"GET\">");
		out.println("<input type = \"submit\" value = \"List Channels and Subscribe\"/></form>");

		out.println("</br></br><b>List of Channels:</b>");


		ArrayList<String> channels = bdb.getChannel(uname);
		if(channels==null) {
			out.println("No Channels");
			out.println("</BODY></HTML>");		
			bdb.destroy();
			return;
		}
		else {
			out.println("<table>");
			for(int i = 0; i<channels.size(); i++) {
				out.println("<tr><td><a href=\"showchannel?"+"id="+channels.get(i)+"\">"+channels.get(i)+" show as xml"+"</a>&nbsp;&nbsp;&nbsp;</td>");
				out.println("<td><a href=\"showchannelxsl?"+"id="+channels.get(i)+"\">"+channels.get(i)+" show using xsl "+"</a>&nbsp;&nbsp;&nbsp;</td>");

				out.println("<td><a href=\"deletechannel?"+"id="+channels.get(i)+"\">"+"delete"+"</a></td></tr></br>");
			}
			out.println("</table>");
			out.println("</BODY></HTML>");		
			bdb.destroy();
		}

	}

}
