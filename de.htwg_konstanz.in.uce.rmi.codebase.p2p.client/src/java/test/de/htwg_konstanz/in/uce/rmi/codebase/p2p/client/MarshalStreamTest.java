package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.server.UnicastRemoteObject;

import org.junit.Test;

/**
 * This JUnit test checks the stream annotation for an exports with
 * java.rmi.server.codebase property value set. The stream annotation
 * is then compared with the expected annotation value.
 * 
 * @author Marco Krammer
 * 
 */
public class MarshalStreamTest {

	@Test
	public void testMarshalAnnotation() throws IOException {											
		System.setProperty("java.rmi.server.codebase", "rmicb://test.jar");

     	ByteArrayOutputStream baos = new ByteArrayOutputStream();
		@SuppressWarnings("restriction")
		sun.rmi.server.MarshalOutputStream mos = new sun.rmi.server.MarshalOutputStream(baos);
		
		Remote stub =  UnicastRemoteObject.exportObject(new Remote() {}, 0); 
		mos.writeObject(stub);
		mos.flush();
		mos.close();


		String stubPrintableChars = StreamUtil.extractPrintables(baos);
		
		if (! stubPrintableChars.contains("rmicb...test.jar")) {
			fail("Annotation not found");
		}
		//System.out.println(stubPrintableChars);
	}	
}
