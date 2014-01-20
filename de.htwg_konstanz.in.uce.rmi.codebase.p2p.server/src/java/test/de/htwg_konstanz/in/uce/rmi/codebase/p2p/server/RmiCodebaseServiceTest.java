package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;

public class RmiCodebaseServiceTest {

	private static final String TESTKEY = "key";
	private static final String TESTVALUE = "testvalue";
	
	@Test
	public void testServe() throws IOException {
		MockCodebaseImpl codebase = new MockCodebaseImpl();
		codebase.put(TESTKEY, TESTVALUE.getBytes());
		
		Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		RmiCodebaseService<String> cbService = new RmiCodebaseService<>(codebase, true);
		IRemoteCodebaseService stub = (IRemoteCodebaseService) UnicastRemoteObject.exportObject(cbService, 0);
		registry.rebind(RmiCodebaseService.CODEBASE_SERVICE_NAME, stub);
	}

	@Test
	public void testRequest() throws IOException, NotBoundException {
		Registry registry = LocateRegistry.getRegistry();
		Remote stub = registry.lookup(RmiCodebaseService.CODEBASE_SERVICE_NAME);
		IRemoteCodebaseService cbService = (IRemoteCodebaseService) stub;
		assertEquals(TESTVALUE, new String(cbService.get(TESTKEY)));
	}
	
	private class MockCodebaseImpl implements ICodebase<String> {
		Map<String, byte[]> map = new HashMap<>();
				
		@Override
		public byte[] get(String name) {
			return map.get(name);
		}

		@Override
		public void put(String name, byte[] content) throws IOException {
			map.put(name, content);
		}
		
		@Override
		public void setSource(String source) {
		}

		@Override
		public String getSource() {
			return null;
		}
	}
}
