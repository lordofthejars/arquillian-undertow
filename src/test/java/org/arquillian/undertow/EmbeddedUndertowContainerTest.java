package org.arquillian.undertow;

import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;

import java.net.URL;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class EmbeddedUndertowContainerTest {

	@Deployment(testable = false)
	public static Archive<WebArchive> createDeployment() {
		return ShrinkWrap.create(WebUndertowArchive.class).from(
				deployment().setContextPath("/myapp").setDeploymentName(
						"test.war").setClassLoader(EmbeddedUndertowContainerTest.class.getClassLoader()).addServlet(servlet("MessageServlet", MessageServlet.class).addMapping("/messageservlet")));
	}

	
	@Test
	public void shouldBeAbleToInvokeServletInDeployedWebApp(@ArquillianResource URL url) throws Exception {

		System.out.println(url);

	}

}
