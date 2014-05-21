package userInterface;


import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;

public class Create extends HttpServlet {

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Creation of Account</TITLE></HEAD><BODY>");

		String uname = request.getParameter("username");
		String pwd = request.getParameter("password");
		
		BerkleyDBWrapper bdb = new BerkleyDBWrapper();
		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);
		
		System.out.println(storepath);
		if(bdb.putUser(uname, pwd)) {
			out.println("Successfuly created account<br/><br/>");
			out.println("<a href=\"/loginpage\">Login</a><br/>");
			out.println("<a href=\"/homepage\">Go to Home Page</a><br/>");
			bdb.destroy();		
		}
		
		else {
			out.println("Username Already Taken<br/><br/>");
			out.println("<a href=\"/createAccount\">Go back to creation Page</a><br/>");
		}
		out.println("</BODY></HTML>");	
		
		bdb.initialize(storepath);
		System.out.println("Inserted into bdb "+bdb.getUser(uname));
		bdb.destroy();

	}
}


