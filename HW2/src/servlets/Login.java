package servlets;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;

public class Login extends HttpServlet {
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Login Page</TITLE></HEAD><BODY>");
		if(request.getParameter("login").equals("cis455")&&request.getParameter("password").equals("vm"))
		{
			out.println("<P>"+request.getParameter("login")+"</P>");
			out.println("<P>Correct!</P>");
			out.println("<form name=\"myform\" action = \"/index\" method = \"POST\">");
			out.println("<input type = \"submit\"value =\"Go To Index\"/>");
		}
		else 
			out.println("<P>Wrong name or password</P>");
		out.println("</BODY></HTML>");		
	}
	
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<HTML><HEAD><TITLE>Login Page</TITLE></HEAD><BODY>");
		if(request.getParameter("login").equals("cis455")&&request.getParameter("password").equals("vm"))
		{
			out.println("<P>"+request.getParameter("login")+"</P>");
			out.println("<P>Correct!</P>");
			out.println("<form name=\"myform\" action = \"/index\" method = \"POST\">");
			out.println("<input type = \"submit\"value =\"Go To Index\"/>");
		}
		else 
			out.println("<P>Wrong name or password</P>");
		out.println("</BODY></HTML>");		
	}
}
