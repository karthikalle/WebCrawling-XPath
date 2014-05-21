package testHarness;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


/**
 * @author Todd J. Green
 */
public class MyRequest implements HttpServletRequest {

	private Properties m_params = new Properties();
	private Properties m_props = new Properties();
	private MySession m_session = null;
	private String m_method;
	private MyResponse response;
	private boolean fromCookie;
	//private HashMap<String,Object> session = new HashMap<String,Object>();

	public MyRequest() {

	}

	public MyRequest(MySession s, MyResponse r) {
		m_session = s;
		response = r;
	}

	public String getAuthType() {	
		return BASIC_AUTH;
	}

	public Cookie[] getCookies() {

		//System.out.println("Printing params");
		for(Object p:m_props.keySet()){
			//System.out.println(p.toString()+" "+m_props.getProperty(p.toString()));
		}
		if(m_props.get("Cookie") == null)
			return null;
		String cookieString = (String) m_props.get("Cookie");
		//System.out.println("Cookiestring:"+cookieString);
		String[] c = cookieString.split(";");
		Cookie[] cookieArray = new Cookie[c.length];
		for(int i = 0;i<c.length;i++) {
			String[] s = c[i].split("=");

			if(s[0].contains("jsessionid")) {
				//System.out.println(s[0]+" "+s[1]);
				MyServletContext context = (MyServletContext) getAttribute("Servlet-Context");
				MySession contextSession = (MySession) context.getAttribute("Session");
				//System.out.println(contextSession.getId().toString());
				//System.out.println("CS:id:"+contextSession.getAttribute("id"));
				if(contextSession.getAttribute("id").equals(s[1]))
					m_session = contextSession;
			}
			Cookie cookie = new Cookie(s[0].trim(),s[1]);
			cookieArray[i] = cookie;

		}
		return cookieArray;
	}

	public long getDateHeader(String arg0) {
		return (Long)m_props.get(arg0);
	}

	public String getHeader(String arg0) {
		String s = m_props.getProperty(arg0);
		if(s == null)
			return null;
		String[] st = s.split(",");
		return st[0];
	}

	public Enumeration getHeaders(String arg0) {
		String s = m_props.getProperty(arg0);
		if(s == null)
			return null;
		String[] st = s.split(",");
		ArrayList<String> stArray = new ArrayList<String>();
		for(int i = 0;i<st.length;i++)
			stArray.add(i, st[i]);
		return (Enumeration) stArray;
	}

	public Enumeration getHeaderNames() {
		return (Enumeration) m_props.keys();
	}

	public int getIntHeader(String arg0) {
		return Integer.parseInt(m_props.getProperty(arg0));
	}

	public String getMethod() {
		return m_method;
	}

	//Should always return the remainder of the URL request
	//after the portion matched by url-pattern
	public String getPathInfo() {
		return m_props.getProperty("path-info");
	}

	//Not Required
	public String getPathTranslated() {
		return null;
	}

	//Doubtful - Server root?
	public String getContextPath() {
		return "";
	}

	public String getQueryString() {
		return m_props.getProperty("query-string");
	}

	//If no user has been authenticated, the getRemoteUser method returns null
	public String getRemoteUser() {
		return null;
	}

	//Not Required
	public boolean isUserInRole(String arg0) {
		return false;
	}

	//Not Required
	public Principal getUserPrincipal() {
		return null;
	}

	//Associated with cookies
	public String getRequestedSessionId() {
		return m_session.getId();
	}

	public String getRequestURI() {
		String s="";
		if(getContextPath()!=null)
			s+=getContextPath();
		if(getServletPath()!=null)
			s+=getServletPath();
		if(getPathInfo()!=null)
			s+=getPathInfo();
		return s;
	}

	//done
	public StringBuffer getRequestURL() {
		StringBuffer s = new StringBuffer();
		s.append("http://localhost:7777/");
		if(getRequestURI() !=null )
			s.append(getRequestURI());
		if(getQueryString()!=null)
			s.append("?"+getQueryString());
		return s;
	}

	public String getServletPath() {
		return m_props.getProperty("servlet-path");
	}

	public HttpSession getSession(boolean arg0) {
		if(response.isCommitted())
			throw new IllegalStateException();

		getCookies();
		if (arg0) {
			if (hasSession()) {
				fromCookie = true;
				m_session.putValue("isNew", false);
				return m_session;
				/*
				MyServletContext context = (MyServletContext) getAttribute("Servlet-Context");
				MySession s = (MySession) context.getAttribute("Session");
				if(s.getAttribute("id").equals(m_session.getAttribute("id")))
						return s;
				 */
			}
			else {
				if (! hasSession()) {
					fromCookie = false;
					m_session = new MySession();
					m_session.putValue("isNew", true);
					//System.out.println(UUID.randomUUID());
					m_session.setAttribute("id",UUID.randomUUID().toString());
					//System.out.println("Inserted into session:"+m_session.getId().toString());

				}
			}

			Cookie c = new Cookie("jsessionid",m_session.getId());

			MyServletContext context = (MyServletContext) getAttribute("Servlet-Context");
			if(context.getInitParameter("session-timeout")!=null)
			{
				int timeout = Integer.parseInt((String) context.getInitParameter("session-timeout"));
		//		System.out.println("max age set to "+timeout*60);
				c.setMaxAge(timeout*4*60);
			}
			else
				c.setMaxAge(9600);
			response.addCookie(c);
			return m_session;
		}
		else {
			if(!hasSession())
				return null;
		}
		return m_session;
	}

	public HttpSession getSession() {
		return getSession(true);
	}

	public boolean isRequestedSessionIdValid() {
		return m_session.isValid();
	}

	public boolean isRequestedSessionIdFromCookie() {
		return fromCookie;
	}

	public boolean isRequestedSessionIdFromURL() {
		return !fromCookie;
	}


	public boolean isRequestedSessionIdFromUrl() {
		return !fromCookie;
	}

	public Object getAttribute(String arg0) {
		return m_props.get(arg0);
	}

	public Enumeration getAttributeNames() {
		return m_props.keys();
	}

	public String getCharacterEncoding() {
		if (m_props.getProperty("Character-Encoding")==null)
			return "ISO-8859-1";
		return m_props.getProperty("Character-Encoding");
	}

	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		m_props.setProperty("Character-Encoding", arg0);
	}

	public int getContentLength() {
		return (Integer)m_props.get("Content-Length");
	}

	public String getContentType() {
		return m_props.getProperty("Content-Type");
	}

	//Not Required
	public ServletInputStream getInputStream() throws IOException {
		return null;
	}

	public String getParameter(String arg0) {
		return m_params.getProperty(arg0);
	}

	public Enumeration getParameterNames() {
		return m_params.keys();
	}

	public String[] getParameterValues(String arg0) {
		String s = m_params.getProperty(arg0);
		if(s==null)
			return null;
		String st[] = s.split(",");
		return st;
	}

	public Map getParameterMap() {
		Map<String,String> m = new HashMap<String,String>();
		Enumeration<Object> st = m_params.keys();

		while(st.hasMoreElements())
			m.put(st.nextElement().toString(), m_params.getProperty(st.nextElement().toString()));

		return m;
	}

	//Protocol as in HTTP/1.1
	public String getProtocol() {
		return m_props.getProperty("requestVersion");
	}

	//like http,https,ftp
	public String getScheme() {
		return m_props.getProperty("Scheme");
	}

	//Like localhost
	public String getServerName() {
		return m_props.getProperty("Server");
	}

	//like 8000
	public int getServerPort() {
		return Integer.parseInt(m_props.getProperty("Port"));
	}

	//bufferedreader
	public BufferedReader getReader() throws IOException {
		return (BufferedReader)m_props.get("Reader");
	}

	//127.0.0.1
	public String getRemoteAddr() {
		return m_props.getProperty("Remote-Addr");
	}

	//remote-host
	public String getRemoteHost() {
		return m_props.getProperty("Remote-Host");
	}

	public void setAttribute(String arg0, Object arg1) {
		//	//System.out.println("Putting "+arg0+" "+arg1.toString());
		m_props.put(arg0, arg1);
	}

	public void removeAttribute(String arg0) {
		m_props.remove(arg0);
	}

	public Locale getLocale() {
		return (Locale) m_props.get("Locale");
	}

	//Not Required
	public Enumeration getLocales() {
		return null;
	}

	public boolean isSecure() {
		return false;
	}

	//Not Required
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return null;
	}

	//Deprecated, use ServletContext.getRealPath(String) instead
	public String getRealPath(String arg0) {
		return null;
	}

	//8000
	public int getRemotePort() {
		return Integer.parseInt((String) m_props.get("port"));
	}

	//localhost
	public String getLocalName() {
		return (String)m_props.get("localhost");
	}

	//local address: 127.0.0.1
	public String getLocalAddr() {
		return "127.0.0.1";
	}

	public int getLocalPort() {
		return Integer.parseInt(m_props.getProperty("port"));
	}

	public void setMethod(String method) {
		m_method = method;
	}

	public void setParameter(String key, String value) {
		m_params.setProperty(key, value);
	}

	void clearParameters() {
		m_params.clear();
	}

	boolean hasSession() {
		return ((m_session != null) && m_session.isValid());
	}

}
