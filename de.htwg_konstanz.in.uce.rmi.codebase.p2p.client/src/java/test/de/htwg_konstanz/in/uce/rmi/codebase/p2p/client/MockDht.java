package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import java.util.HashMap;
import java.util.Map;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;

/**
 * Mock implementation of the {@link UceDht} interface
 * 
 * @author Marco Krammer
 * 
 */
public class MockDht implements UceDht {

	public static final String TESTKEY1 = "testkey1";
	public static final String TESTVALUE1 = "testvalue1";

	public static final String TESTKEY2 = "testkey2";
	public static final String TESTVALUE2 = "testvalue2";

	private Map<String, byte[]> map = new HashMap<>();

	@Override
	public byte[] remove(String key) throws InterruptedException {
		return map.remove(key);
	}

	@Override
	public boolean put(String key, byte[] value) throws InterruptedException {
		return map.put(key, value) != null;
	}

	@Override
	public byte[] get(String key) throws InterruptedException {
		return map.get(key);
	}

	@Override
	public void destroy() {
	}

	@Override
	public void bootstrap() {
	}
}
