package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;

public class Authenticate extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Login Page</TITLE></HEAD><BODY>");

		String uname = request.getParameter("username");
		String pwd = request.getParameter("password");

		BerkleyDBWrapper bdb = new BerkleyDBWrapper();

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();

		String storepath = (String) context.getInitParameter("BDBStore");
		bdb.initialize(storepath);

		if(bdb.getUser(uname)==null||uname==null||pwd==null) {
			System.out.println("Authenticate "+uname);
			out.println("<P>Wrong name or password</P>");
			out.println("<a href=\"/loginpage\">Try Again</a><br/>");
			out.println("<a href=\"/createAccount\">Create a new Account</a><br/>");
			out.println("</BODY></HTML>");		
			return;
		}

		String checkPwd = bdb.getUser(uname);
		if(checkPwd.equals(pwd)) {
			HttpSession session = request.getSession();
			session.setAttribute("User", uname);
			
			out.println("<P><h2>Welcome To: </h2><br/><br/>"+request.getParameter("username")+"</P>");
			out.println("<form name=\"myform\" action = \"/userpage\" method = \"GET\">");
			out.println("<input type = \"submit\"value =\"Go To My Page\"/>");
		}
		else { 
			out.println("<P>Wrong name or password</P>");
			out.println("<a href=\"/loginpage\">Try Again</a><br/>");
			out.println("<a href=\"/createAccount\">Create a new Account</a><br/>");
		}

		out.println("</BODY></HTML>");		
		bdb.destroy();
	}

}
