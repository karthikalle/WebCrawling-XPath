package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.util.ArrayList;

public class Logout extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Logging out</TITLE></HEAD><BODY>");

		//HttpSession session = request.getSession();
		HttpSession session = request.getSession();

		System.out.println("logout:"+session.getAttribute("user"));
		request.getSession(false);

		out.println("<b>Logged Out</b><br/><br/>");
		out.println("<a href=\"/homepage\">Go to Home Page</a><br/>");

		out.println("</body></html>");


	}

}
