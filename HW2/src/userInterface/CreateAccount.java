package userInterface;


import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;

public class CreateAccount extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Create Your Account</TITLE></HEAD><BODY>");
		out.println("<h2>Create Your Account</h2>");
		
		out.println("<form name=\"myform\" action = \"/create\" method = \"POST\">");
		out.println("Enter Username: <input type = \"text\" value = \"\" name = \"username\"/><br/><br/>");
		out.println("Password:       <input type = \"password\" value = \"\" name = \"password\"/><br/><br/>");
		out.println("<input type = \"submit\"/></form>");
		out.println("</BODY></HTML>");		
	}
	
}


