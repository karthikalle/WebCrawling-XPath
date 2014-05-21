package servletTest;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.junit.Before;
import org.junit.Test;

import edu.upenn.cis455.webserver.HttpServer;
import testHarness.MyContainer;
import testHarness.MyServletConfig;
import testHarness.MyServletContext;

public class ServletConfigTest {

	@Test
	public void testContext() {
		MyServletContext  c = new MyServletContext();
		MyServletConfig config = new MyServletConfig("test", c);
		assertEquals(c,config.getServletContext());

	}

	@Test
	public void testSetInitParams() throws IOException, ServletException, Exception {
		MyServletConfig config = new MyServletConfig("test", null);
		config.setInitParam("t", "1");
		assertEquals(config.getInitParameter("t"),"1");
	}

	@Test
	public void testName() {

		MyServletConfig config = new MyServletConfig("test", null);
		assertEquals(config.getServletName(),"test");
		assertEquals(null,config.getServletContext());
	}


}
