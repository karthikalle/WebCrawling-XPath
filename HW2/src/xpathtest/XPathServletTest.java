package xpathtest;

import java.io.IOException;

import javax.servlet.ServletException;

import testHarness.*;
import static org.junit.Assert.*;

import org.junit.Test;

import edu.upenn.cis455.servlet.XPathServlet;

public class XPathServletTest {

	@Test
	public void test() throws ServletException, IOException {
		XPathServlet s = new XPathServlet();
		
		MyContainer t = new MyContainer();
		 MyResponse res = new MyResponse(t);
		 MyRequest req = new MyRequest(null,res);
		 req.setParameter("url","http://www.w3schools.com/xml/simple.xml ");
		 req.setParameter("xpath", "/breakfast_menu/food[price[text()=\"$5.95\"]][calories[text()=\"650\"]][description[contains(text(),\"no\")]]");
		 s.doPost(req,res);
		 
	}

}
