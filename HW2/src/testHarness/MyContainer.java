package testHarness;

import edu.upenn.cis455.webserver.HttpServer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author Todd J. Green, modified by Nick Taylor
 */
public class MyContainer {	
	public HashMap<String,HttpServlet> servlets;
	public HashMap<String,String> servletMapping;
	public String pathToWebXML;
	Logger log;

	static class Handler extends DefaultHandler {
		private int m_state = 0;
		private String m_servletName;
		private String m_paramName;
		HashMap<String,String> m_servlets = new HashMap<String,String>();
		HashMap<String,String> m_contextParams = new HashMap<String,String>();
		HashMap<String,HashMap<String,String>> m_servletParams = new HashMap<String,HashMap<String,String>>();
		public HashMap<String,String> servletMapping = new HashMap<String,String>();
		String currentname = "";


		public void startElement(String uri, String localName, String qName, Attributes attributes) {
			if (qName.compareTo("servlet-name") == 0) {
				m_state = (m_state == 4) ? 12 : 1;
			} else if (qName.compareTo("servlet-class") == 0) {
				m_state = 2;
			} else if (qName.compareTo("context-param") == 0) {
				m_state = 3;
			} else if (qName.compareTo("init-param") == 0) {
				m_state = 4;
			} else if (qName.compareTo("param-name") == 0) {
				m_state = (m_state == 3) ? 10 : 20;//If 3 then context, else servlet config
			} else if (qName.compareTo("param-value") == 0) {
				m_state = (m_state == 10) ? 11 : 21;//If param value, then value 11 for context, or 21 for config
			} else if (qName.compareTo("session-config") == 0) {
				m_state = 5;
			} else if (qName.compareTo("session-timeout") == 0) {
				m_state = (m_state == 5) ? 15 : 25;
			} else if(qName.compareTo("servlet-mapping") == 0) {
				m_state = 4;
			} else if(qName.compareTo("servlet-name") == 0) {
				//m_state = (m_state == 4) ? 12 : 25;
			} else if(qName.compareTo("url-pattern") == 0) {
				m_state = (m_state == 12) ? 16 : 25;
			}
		}

		public void characters(char[] ch, int start, int length) {
			String value = new String(ch, start, length);
			if (m_state == 1) {
				m_servletName = value;
				m_state = 0;
			} else if (m_state == 2) {
				m_servlets.put(m_servletName, value);
				m_state = 0;
			} else if (m_state == 10 || m_state == 20) {
				m_paramName = value;
			} else if (m_state == 11) {
				if (m_paramName == null) {
					System.err.println("Context parameter value '" + value + "' without name");
					System.exit(-1);
				}
				m_contextParams.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 21) {
				if (m_paramName == null) {
					System.err.println("Servlet parameter value '" + value + "' without name");
					System.exit(-1);
				}
				HashMap<String,String> p = m_servletParams.get(m_servletName);
				if (p == null) {
					p = new HashMap<String,String>();
					m_servletParams.put(m_servletName, p);
				}
				p.put(m_paramName, value);
				m_paramName = null;
				m_state = 0;
			} else if (m_state == 15) {
				m_contextParams.put("session-timeout", value);
				m_state = 0;
			} else if (m_state == 12) {
				servletMapping.put(value, "");
				currentname = value;
				//m_state = 0;
			} else if (m_state == 16) {
				servletMapping.put(currentname, value);
				m_state = 0;
			}


		}
		@SuppressWarnings("unused")
		public void printEverything() {
			//System.out.println("Printing Servlets:");
			for(String servlet:m_servlets.keySet()){
				//System.out.println(servlet+" "+m_servlets.get(servlet));
			}
			//System.out.println("\n");
			//System.out.println("Printing Servlet Context:");
			for(String servlet:m_contextParams.keySet()){
				//System.out.println(servlet+" "+m_contextParams.get(servlet));
			}
			//System.out.println("\n");

			//System.out.println("Printing Servlet Config:");
			for(String servlet:m_servletParams.keySet()){
				//System.out.println(servlet+" "+m_servletParams.get(servlet));
			}

			//System.out.println("Printing Servlet Mapping:");
			for(String servlet:servletMapping.keySet()){
				//System.out.println(servlet+" "+servletMapping.get(servlet));
			}
			//System.out.println("\n");

			return;
		}

	}

	private static Handler parseWebdotxml(String webdotxml) throws Exception {
		Handler h = new Handler();
		File file = new File(webdotxml);
		if (file.exists() == false) {
			System.err.println("error: cannot find " + file.getPath());
			System.exit(-1);
		}
		SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
		parser.parse(file, h);
		h.printEverything();
		return h;
	}

	private static MyServletContext createContext(Handler h) {
		MyServletContext fc = new MyServletContext();
		for (String param : h.m_contextParams.keySet()) {
			fc.setInitParam(param, h.m_contextParams.get(param));
		}
		return fc;
	}

	public static HashMap<String,String> createServletMapping(Handler h) {
		return h.servletMapping;
	}

	@SuppressWarnings("rawtypes")
	public static HashMap<String,HttpServlet> createServlets(Handler h, MyServletContext fc) throws Exception {
		HashMap<String,HttpServlet> servlets = new HashMap<String,HttpServlet>();
		for (String servletName : h.m_servlets.keySet()) {
			MyServletConfig config = new MyServletConfig(servletName, fc);
			String className = h.m_servlets.get(servletName);
			Class servletClass = Class.forName(className);
			HttpServlet servlet = (HttpServlet) servletClass.newInstance();
			HashMap<String,String> servletParams = h.m_servletParams.get(servletName);
			if (servletParams != null) {
				for (String param : servletParams.keySet()) {
					config.setInitParam(param, servletParams.get(param));
				}
			}
			servlet.init(config);
			servlets.put(servletName, servlet);
		}

		return servlets;
	}

	private static void usage() {
		System.err.println("usage: java TestHarness <path to web.xml> " 
				+ "[<GET|POST> <servlet?params> ...]");

	}

	public boolean initialize(String pathtowebxml, Logger l) throws Exception {

		if (pathtowebxml.length() == 0) {
			usage();
			return false;
		}

		Handler h = parseWebdotxml(pathtowebxml);
		MyServletContext context = HttpServer.servletContext;
		if(context==null){
			context = createContext(h);
			HttpServer.servletContext = context;
		}
		servlets = createServlets(h, context);
		servletMapping = createServletMapping(h);
		pathToWebXML = pathtowebxml;
		log = l;
		return true;
	}

	public boolean doWork(String[] args, Socket sock, HashMap<String,Object> att)
			throws Exception, IOException, ServletException {
		if (args.length < 3 || args.length % 2 == 0) {
			usage();
			log.warning("Wrong number of inputs to container");
			return false;
		}
		Socket s;

		s = sock;
		HashMap<String, Object> requestParams = att;

		MySession fs = null;


		for (int i = 1; i < args.length - 1; i += 2) {
			MyResponse response = new MyResponse(this);
			MyRequest request = new MyRequest(fs,response);

			String[] strings = args[i+1].split("\\?|&|=");

			/*
			 * Added for getting Servlet Name 
			 * and path info
			 * and requestURL
			 */
			String[] requestURL = args[i+1].split("[?]");
			request.setAttribute("requestURL", requestURL[0]);
			String totalPath = strings[0];

			String[] urlpath = totalPath.split("/");

			if(urlpath.length>=2)
				request.setAttribute("path-info", totalPath.substring(urlpath[0].length()));
			if(strings.length>=2)
				request.setAttribute("query-string", args[i+1].substring(totalPath.length()+1));

			String queryString = (String)request.getAttribute("query-string");
			if(queryString!=null)
			{
				String[] q = queryString.split("&");
				for(int in = 0; in<q.length; in++) {
					String[] qAtt = q[in].split("=");
				//	System.out.println("QUATTT:"+qAtt[0]+" "+qAtt[1]);
					request.setParameter(qAtt[0],qAtt[1]);
				}
			}

			String shortest = "";
			String req = args[i+1];
			for(String name:servletMapping.keySet()){
				if(servletMapping.get(name).contains("*")) {
					if(req.contains(servletMapping.get(name))){
						if(shortest.length()<servletMapping.get(name).length())
							shortest = name;
					}
				}
				else {
					String r=req;
					if(req.contains("?"))
						r = req.substring(0,req.indexOf("?"));
					if(r.equals(servletMapping.get(name))){
						if(shortest.length()<servletMapping.get(name).length())
							shortest = name;
					}
				}
			}

			HttpServlet servlet = servlets.get(shortest);

			//There is no servlet for that request
			if (servlet == null) {
				System.err.println("error: cannot find mapping for servlet " + strings[0]);
				return false;
			}
			response.setHeader("requestVersion", requestParams.get("requestVersion").toString());
			response.addSocket(s);


			parseRequest(sock.getInputStream(),request, requestParams);

			MyServletContext context = HttpServer.servletContext;

			request.setAttribute("Servlet-Context", context);

			for (int j = 1; j < strings.length - 1; j += 2) {
				request.setParameter(strings[j], strings[j+1]);
			}

			if (args[i].compareTo("GET") == 0 || args[i].compareTo("POST") == 0) {

				request.setMethod(args[i]);
				servlet.service(request, response);

				if(!response.isCommitted()) {
					if(request.hasSession()) {
						context.setAttribute("Session", request.getSession());
					}
					writeHeader(response);
					writeBody(response);
				}

			} else {
				System.err.println("error: expecting 'GET' or 'POST', not '" + args[i] + "'");
				usage();
				return false;
			}
		}
		return true;
	}

	public void writeHeader(MyResponse response)
			throws IOException {
		Socket s = response.getSocket();
		OutputStream out = s.getOutputStream();
		if(response.statuscodes.isEmpty()) {
			out.write((response.m_props.get("requestVersion")+" 200 OK\r\n").getBytes()); 
			response.m_props.remove("requestVersion");
		}
		else {
			Set<Integer> key = response.statuscodes.keySet();
			Iterator<Integer> a = key.iterator();
			int k = a.next();
		//	System.out.println((response.m_props.get("requestVersion")+" "+k+response.statuscodes.get(k)+"\r\n"));
			out.write((response.m_props.get("requestVersion")+" "+k+response.statuscodes.get(k)+"\r\n").getBytes());
			response.m_props.remove("requestVersion");

		}
		for(String k: response.m_props.keySet()) {
	//		System.out.println((k+": "+response.m_props.get(k)+"\r\n"));
			out.write((k+": "+response.m_props.get(k)+"\r\n").getBytes());
		}
		out.write("\r\n".getBytes());
		response.isCommitted = true;
		out.flush();
	}

	public void writeBody(MyResponse response) throws IOException {
		Socket s = response.getSocket();
		OutputStream out = s.getOutputStream();
		BufferedOutputStream o = new BufferedOutputStream(out);
		PrintWriter pw = new PrintWriter(o,true);
		pw.print(response.buffer.toString());
		pw.flush();
	}

	public void parseRequest(InputStream input, MyRequest request, HashMap<String, Object> requestParams) {
		for(String p:requestParams.keySet()) {
			request.setAttribute(p, requestParams.get(p).toString());
		}
		if(request.getAttribute("postparams")!=null){
			String params = (String)request.getAttribute("postparams");
			String[] p = params.split("&");
			for(int ind = 0 ; ind<p.length;ind++)
			{
				String[] r = p[ind].split("=");
				if(r.length==2){
				//System.out.println("Params:"+r[0]+r[1]);
				request.setParameter(r[0], r[1]);
				}
			}
		}
		request.removeAttribute("postparams");
	}

	public void performShutDown() {
		for(Servlet s: servlets.values()) {
			s.destroy();
		}
		servlets.clear();
	}

}

