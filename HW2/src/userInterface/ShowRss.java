package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class ShowRss extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xsl");
		PrintWriter out = response.getWriter();

		ServletConfig config = getServletConfig();
		ServletContext context = config.getServletContext();
		File f = new File("./rss/rss.xsl");
		BufferedReader br = new BufferedReader(new FileReader(f));
		String c = "", str;
		while((str=br.readLine())!=null) {
			c = c + str;
		}
		System.out.println("inside rss");
		out.println(c);
	}
}
