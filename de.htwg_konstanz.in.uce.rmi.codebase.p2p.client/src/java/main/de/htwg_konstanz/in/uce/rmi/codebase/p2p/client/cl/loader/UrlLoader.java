package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.common.io.ByteStreams;

/**
 * {@link ILoader} implementation using an URL connection
 * as source.
 * 
 * @author Marco Krammer
 *
 */
public class UrlLoader implements ILoader { 
	private URL host;

	/**
	 * Constructor of UrlLoader
	 */
	public UrlLoader() {
		super();
	}
	
	/**
	 * Constructor of UrlLoader,
	 * sets the host parameter
	 * 
	 * @param host the URL to use as host
	 */
	public UrlLoader(URL host) {
		super();
		this.host = host;
	}

	/**
	 * Retrieves the byte[] from the URL
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.ILoader#toByteArray(java.lang.String)
	 */
	@Override
	public byte[] toByteArray(String file) throws IOException {
		
		URL url = null;
		if (host != null) {
			url = new URL(host.getProtocol(), host.getHost(), host.getPort(), "/" + file);
		} else {
			url = new URL(file);
		}
		
		InputStream is = url.openConnection().getInputStream();
		byte[] bytes = ByteStreams.toByteArray(is);
		is.close();
		
		return bytes;
	}
}
