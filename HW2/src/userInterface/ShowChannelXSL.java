package userInterface;
import javax.servlet.*;
import javax.servlet.http.*;

import edu.upenn.cis455.storage.BerkleyDBWrapper;

import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;

public class ShowChannelXSL extends HttpServlet {
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException {
		PrintWriter out;
		try {
			out = response.getWriter();

			ServletConfig config = getServletConfig();
			ServletContext context = config.getServletContext();

			BerkleyDBWrapper bdb = new BerkleyDBWrapper();
			String storepath = (String) context.getInitParameter("BDBStore");
			bdb.initialize(storepath);

			//	HttpSession session = request.getSession();
			//	session.setAttribute("User", session.getAttribute("User"));

			//System.out.println("Inside show channel"+session.getAttribute("User"));
			String channelname = (String) request.getParameter("id");

			ArrayList<String> candurl = bdb.getChannelandUrls(channelname);
			//	System.out.println("Username:"+session.getAttribute("User"));

			if(bdb.getChannelandXSLUrl(channelname)==null||candurl==null) {
				response.setContentType("text/html");
				out.println("<html><body>Cannot find xslt");
				out.println("</body></html>");
				return;
			}
			else{
				response.setContentType("application/xml");

				System.out.println("Trying");
				

				String xslurl = bdb.getChannelandXSLUrl(channelname);
				String head = "";
				head += "<?xml version=\"1.0\"?>\n";
				if(xslurl.equals("http://www.w3.org/1999/XSL/Transform")) {
					head += "<?xml-stylesheet href=\""+xslurl+"\" version =\"1.0\" ?>";
				}
				else 
					head = "<?xml version=\"1.0\" ?>\n<?xml-stylesheet href=\""+xslurl+"\" type=\"text/xsl\" ?>";
				System.out.println(head);
				//	out.println("<?xml-stylesheet href=\""+channelName+"\" type=\"text/xsl\" ?>");
				System.out.println("Trying2");
				String content = (bdb.constructXML(channelname));
				System.out.println(new File("xmlfiles").getAbsolutePath());
				File f = new File("xmlfiles");

				f = new File("xmlfiles/"+channelname+".xml");
				PrintWriter pw;
				try {
					if(f.exists())
						f.delete();
					try {
						f.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
					pw = new PrintWriter(f);
					pw.write(head+"\n"+content.trim());
					pw.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}

				out.println(head+"\n"+content.trim());
				//out.println("</xml:stylesheet>");
			}
			bdb.destroy();
			return;

		} catch (ParseException e) {
			e.printStackTrace();
		}
		catch (IOException e1) {
			e1.printStackTrace();
		}

	}
}
