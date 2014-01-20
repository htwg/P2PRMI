package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.junit.Before;
import org.junit.Test;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.RmiClassLoaderSpiSimpleImpl;

/**
 * This JUnit test checks the stream annotation for multiple exports with different
 * java.rmi.server.codebase property values set. Each exported stream's annotation
 * will be compared with the expected annotation value.
 * 
 * @author Marco Krammer
 * 
 */
public class SwitchCodebaseTest {

	@Before
	public void setUp() throws Exception {
		System.setProperty("java.rmi.server.RMIClassLoaderSpi",
				RmiClassLoaderSpiSimpleImpl.class.getName());
	}

	@Test
	public void testMarshalAnnotation() throws IOException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		System.setProperty("java.rmi.server.codebase", "rmicb://test.jar");
		export(UnicastRemoteObject.exportObject(
				new Remote() {}, 0), "rmicb...test.jar");

		System.setProperty("java.rmi.server.codebase", "http://localhost:2001");
		export(UnicastRemoteObject.exportObject(
				new Remote() {}, 0), "http...localhost.2001");
	}

	private void export(Object obj, String anno) throws IOException,
			RemoteException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		@SuppressWarnings("restriction")
		sun.rmi.server.MarshalOutputStream mos = 
			new sun.rmi.server.MarshalOutputStream(baos);

		mos.writeObject(obj);
		mos.flush();
		mos.close();

		String stubPrintableChars = StreamUtil.extractPrintables(baos);
		System.out.println(stubPrintableChars);

		if (!stubPrintableChars.contains(anno)) {
			fail("Annotation not found");
		}
	}

}
