package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.http.HttpCodebaseServer;

public class HttpCodebaseServerTest {
	
	private final String testPath = "package/Test.class";
	private final String testContent = "justatest";

	@Test
	public void testServe() throws IOException {
		int port = HttpCodebaseServer.DefaultServerPort;
		
		new Thread(new HttpCodebaseServer<String>(port, new MockCodebaseImpl())).start();
		
		byte[] bytes = toByteArray(new URL("http", "localhost", port, testPath));
		assertEquals(testContent, new String(bytes));
	}

	private byte[] toByteArray(URL url) throws IOException {
		InputStream is = url.openConnection().getInputStream();
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		byte[] bytes = new byte[1024];
		int numRead;
		while ((numRead = is.read(bytes, 0, bytes.length)) != -1) {
			buffer.write(bytes, 0, numRead);
		}

		buffer.flush();

		return buffer.toByteArray();
	}
	
	private class MockCodebaseImpl implements ICodebase<String> {

		@Override
		public byte[] get(String path) {
			if (testPath.equals(path)) {
				return testContent.getBytes();
			}
			return null;
		}

		@Override
		public void put(String name, byte[] content) throws IOException {
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
