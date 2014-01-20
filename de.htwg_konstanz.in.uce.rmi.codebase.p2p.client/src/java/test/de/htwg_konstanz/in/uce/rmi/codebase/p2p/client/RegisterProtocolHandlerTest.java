package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.HandlerUtil;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb.Handler;

/**
 * This JUnit test verifies the registration of the custom URL protocol dhtcb
 * 
 * @author Marco Krammer
 * 
 */
public class RegisterProtocolHandlerTest {

	@Test
	public void testRegister() throws MalformedURLException {
		URL url;
		try {
			url = new URL(Handler.protocol, "test", "");
			fail("Should not be registered yet.");
		} catch (MalformedURLException e) {
		}

		HandlerUtil.register(Handler.class);
		Handler.setDht(new MockDht());

		String key = "testkey";
		url = new URL(Handler.protocol, key, "");
		assertEquals(Handler.protocol, url.getProtocol()); // TODO ?
		assertEquals(key, url.getHost());
	}

}
