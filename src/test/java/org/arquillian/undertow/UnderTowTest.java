package org.arquillian.undertow;

import static io.undertow.servlet.Servlets.defaultContainer;
import static io.undertow.servlet.Servlets.deployment;
import static io.undertow.servlet.Servlets.servlet;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.util.Headers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.servlet.ServletException;

import org.junit.Test;

public class UnderTowTest {

	@Test
	public void startServlet() throws IOException, Exception {

		try {

			DeploymentInfo servletBuilder = deployment()
			        .setClassLoader(UnderTowTest.class.getClassLoader())
			        .setContextPath("/myapp")
			        .setDeploymentName("test.war")
			        .addServlets(
			                servlet("MessageServlet", MessageServlet.class)
			                        .addInitParam("message", "Hello World")
			                        .addMapping("/*"),
			                servlet("MyServlet", MessageServlet.class)
			                        .addInitParam("message", "MyServlet")
			                        .addMapping("/myservlet"));

			DeploymentManager manager = defaultContainer().addDeployment(servletBuilder);
			manager.deploy();

			Undertow server = Undertow.builder()
			        .addHttpListener(8080, "localhost")
			        .setHandler(manager.start())
			        .build();
			server.start();
			
			URL url = new URL("http://localhost:8080/myapp/myservlet");
			System.out.println(readAllAndClose(url.openStream()));
			server.stop();
		} catch (ServletException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void startHttpHandler() throws IOException, Exception {
		
		 Undertow server = Undertow.builder()
	                .addHttpListener(8080, "localhost")
	                .setHandler(new HttpHandler() {
	                    @Override
	                    public void handleRequest(final HttpServerExchange exchange) throws Exception {
	                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
	                        exchange.getResponseSender().send("Hello World");
	                    }
	                }).build();
	        server.start();
		
	        URL url = new URL("http://localhost:8080");
			System.out.println(readAllAndClose(url.openStream()));
	        server.stop();
	}
	
	private String readAllAndClose(InputStream is) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			int read;
			while ((read = is.read()) != -1) {
				out.write(read);
			}
		} finally {
			try {
				is.close();
			} catch (Exception e) {
			}
		}
		return out.toString();
	}

}
