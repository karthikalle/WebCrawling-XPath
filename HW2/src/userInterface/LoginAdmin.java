package userInterface;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;

public class LoginAdmin extends HttpServlet {

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Login to your Account</TITLE></HEAD><BODY>");
		out.println("<h2>Login</h2>");
		
		out.println("<form name=\"myform\" action = \"/adminpage\" method = \"POST\">");
		out.println("Enter Username: <input type = \"text\" value = \"\" name = \"username\"/><br/><br/>");
		out.println("Password:       <input type = \"password\" value = \"\" name = \"password\"/><br/><br/>");
		out.println("<input type = \"submit\"/></form>");
		out.println("</BODY></HTML>");		
	}
	
}


