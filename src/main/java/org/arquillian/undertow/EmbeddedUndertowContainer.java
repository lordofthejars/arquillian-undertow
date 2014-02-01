package org.arquillian.undertow;

import static io.undertow.servlet.Servlets.defaultContainer;

import java.util.Collection;
import java.util.Map;

import io.undertow.Undertow;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import io.undertow.servlet.api.ServletInfo;

import javax.servlet.ServletException;

import org.jboss.arquillian.container.spi.client.container.DeployableContainer;
import org.jboss.arquillian.container.spi.client.container.DeploymentException;
import org.jboss.arquillian.container.spi.client.container.LifecycleException;
import org.jboss.arquillian.container.spi.client.protocol.ProtocolDescription;
import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.descriptor.api.Descriptor;

public class EmbeddedUndertowContainer implements
		DeployableContainer<UndertowContainerConfiguration> {

	private Undertow undertow;
	private DeploymentManager deploymentManager;
	private UndertowContainerConfiguration configuration;

	public ProtocolMetaData deploy(Archive<?> archive)
			throws DeploymentException {

		// TODO instance of check
		WebUndertowArchive servletBuilder = (WebUndertowArchive) archive;

		final DeploymentInfo deploymentInfo = servletBuilder
				.getDeploymentInfo();
		this.deploymentManager = defaultContainer().addDeployment(
				deploymentInfo);
		this.deploymentManager.deploy();

		try {
			this.undertow = Undertow
					.builder()
					.setHandler(this.deploymentManager.start())
					.addHttpListener(configuration.getBindHttpPort(),
							configuration.getBindAddress()).build();
			this.undertow.start();

		} catch (ServletException e) {
			throw new IllegalArgumentException(e);
		}

		HTTPContext httpContext = new HTTPContext(
				configuration.getBindAddress(), configuration.getBindHttpPort());

		final Map<String, ServletInfo> servlets = deploymentInfo.getServlets();

		final Collection<ServletInfo> servletsInfo = servlets.values();

		for (ServletInfo servletInfo : servletsInfo) {
			httpContext.add(new Servlet(servletInfo.getName(),
					this.deploymentManager.getDeployment().getDeploymentInfo()
							.getContextPath()));
		}

		return new ProtocolMetaData().addContext(null);
	}

	public void deploy(Descriptor descriptor) throws DeploymentException {
		throw new UnsupportedOperationException("Not implemented");
	}

	public Class<UndertowContainerConfiguration> getConfigurationClass() {
		return UndertowContainerConfiguration.class;
	}

	public ProtocolDescription getDefaultProtocol() {
		return new ProtocolDescription("Servlet 3.0");
	}

	public void setup(
			UndertowContainerConfiguration undertowContainerConfiguration) {
		this.configuration = undertowContainerConfiguration;
	}

	public void start() throws LifecycleException {
		// it is done on deployment time.
	}

	public void stop() throws LifecycleException {
		undertow.stop();
	}

	public void undeploy(Archive<?> archive) throws DeploymentException {
		try {
			deploymentManager.stop();
		} catch (ServletException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public void undeploy(Descriptor descriptor) throws DeploymentException {
		throw new UnsupportedOperationException("Not implemented");
	}

}
