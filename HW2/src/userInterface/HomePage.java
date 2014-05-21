package userInterface;


import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;

public class HomePage extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Home Page</TITLE></HEAD><BODY>");
		out.println("<h2>Welcome to Home Page</h2>");
		out.println("<h3>");

		out.println("<a href=\"/createAccount\">Create a new account</a><br/><br/>");
		out.println("<a href=\"/loginpage\">Login</a><br/><br/>");
		out.println("<a href=\"/listchannels\">List Channels</a><br/><br/><br/>");
		
		out.println("<a href=\"/loginadmin\">Login as Admin</a><br/>");
		out.println("</h3>");

		out.println("</BODY></HTML>");		

	}
}


