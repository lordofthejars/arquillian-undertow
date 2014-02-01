package org.arquillian.undertow;

import static io.undertow.servlet.Servlets.deployment;
import io.undertow.servlet.api.DeploymentInfo;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;

public class ShrinkTest {

	@Test
	public void test() {

		ShrinkWrap.create(WebUndertowArchive.class).from(deployment());  
		
	}

}
