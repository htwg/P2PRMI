package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.HandlerUtil;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb.Handler;

/**
 * This JUnit test verifies the retrieval of data via an {@link URLConnection}
 * using a custom URL protocol
 * 
 * @author Marco Krammer
 * 
 */
public class RetrieveDhtValueFromURLTest {

	@Test
	public void testRetrieve() throws IOException, InterruptedException {
		MockDht dht = new MockDht();
		dht.put(MockDht.TESTKEY1, MockDht.TESTVALUE1.getBytes());

		HandlerUtil.register(Handler.class);
		Handler.setDht(dht);

		URL url = new URL(Handler.protocol, MockDht.TESTKEY1, "");
		URLConnection urlConnection = url.openConnection();
		BufferedReader in = new BufferedReader(new InputStreamReader(
				urlConnection.getInputStream()));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			assertEquals(MockDht.TESTVALUE1, inputLine);
		}
		in.close();
	}

}
