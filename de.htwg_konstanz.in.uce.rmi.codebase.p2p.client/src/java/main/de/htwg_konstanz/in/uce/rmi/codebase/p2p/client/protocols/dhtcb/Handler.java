package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;

/**
 * Implementation of an URLStreamHandler for the dhtcb scheme
 * 
 * TODO: the host part of the URL could be used to distinguish 
 * 		 between different peers
 * 
 * @author Marco Krammer
 *
 */
public class Handler extends URLStreamHandler {
	
	private static final Logger logger = Logger.getLogger(Handler.class.getName());

	public static final String protocol = "dhtcb";	//DHT CodeBase
	
	private static UceDht dht;
	
	/**
	 * Default constructor of Handler
	 */
	public Handler() {
		super();
	}

	/**
	 * Returns a new {@link DhtUrlConnection}
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		logger.info("openConnection " + url);
		return new DhtUrlConnection(url);
	}

	private class DhtUrlConnection extends URLConnection {
		private final String key;

		/**
		 * Constructor for DhtUrlConnection,
		 * sets the parameter url
		 * 
		 * @param url the URL to use
		 */
		public DhtUrlConnection(URL url) {
			super(url);
			this.key = url.getHost();
		}

		/**
		 * Checks if dht has been set
		 * 
		 * TODO: should also check or wait for the DHT 
		 * 		 to become ready
		 * 
		 * @see java.net.URLConnection#connect()
		 */
		@Override
		public void connect() throws IOException {
			if (null == Handler.dht) {
				throw new IOException("Source dht is not set!");
			}
		}

		/**
		 * Returns the result from the get method of the
		 * {@link UceDht} converted to a stream
		 * 
		 * @see java.net.URLConnection#getInputStream()
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			byte[] returnValue = null;
			
			try {
				long start = System.currentTimeMillis();
				logger.info("dht get " + key);
				returnValue = Handler.dht.get(key);
				logger.info("retrieved in " + (System.currentTimeMillis() - start) + " ms");
			} catch (InterruptedException e) {
				logger.severe("failed to retrieve key " + key + ": " + e.getMessage());
				throw new IOException("dht 'get' interrupted", e);
			}
			return new ByteArrayInputStream(returnValue);
		}
	}

	/**
	 * Static setter for the UceDht as source
	 * 
	 * @param dht the UceDht to read from
	 */
	public static void setDht(UceDht dht) {
		Handler.dht = dht;
	}
}
