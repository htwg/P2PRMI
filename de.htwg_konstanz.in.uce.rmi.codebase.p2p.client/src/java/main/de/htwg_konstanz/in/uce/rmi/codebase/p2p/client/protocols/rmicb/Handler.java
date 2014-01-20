package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.rmicb;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;

/**
 * Implementation of an URLStreamHandler for the rmicb scheme
 * 
 * TODO: the host part of the URL could be used to distinguish 
 * 		 between different peers 
 * 
 * @author Marco Krammer
 *
 */
public class Handler extends URLStreamHandler {
	
	private static final Logger logger = Logger.getLogger(Handler.class.getName());

	public static final String protocol = "rmicb";
	
	private static IRemoteCodebaseService cbService;
	
	/**
	 * Default constructor
	 */
	public Handler() {
		super();
	}

	/**
	 * Returns a new {@link RmiURLConnection}
	 * 
	 * @see java.net.URLStreamHandler#openConnection(java.net.URL)
	 */
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		logger.info("openConnection " + url);
		return new RmiURLConnection(url);
	}

	private class RmiURLConnection extends URLConnection {
		private final String key;
		
		/**
		 * Constructor for RmiURLConnection,
		 * sets the parameter url
		 * 
		 * @param url the URL to use
		 */
		public RmiURLConnection(URL url) {
			super(url);
			String key = url.getHost();
			//TODO sanitize key/specify format
			if (key.startsWith("/")) {
				key = key.substring(1);
			}
			this.key = key;
		}

		/**
		 * Checks if cbService has been set
		 * 
		 * @see java.net.URLConnection#connect()
		 */
		@Override
		public void connect() throws IOException {
			if (null == Handler.cbService) {
				throw new IOException("Source service is not set!");
			}
		}

		/**
		 * Returns the result from the get method of the
		 * {@link IRemoteCodebaseService} converted to a stream
		 * 
		 * @see java.net.URLConnection#getInputStream()
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			byte[] returnValue = null;

			long start = System.currentTimeMillis();
			logger.info("rmi get " + key);
			
			returnValue = Handler.cbService.get(key);
			if (returnValue != null) {
				logger.info("retrieved " + returnValue.length + " bytes in " + (System.currentTimeMillis() - start)
					+ " ms");
			}
			return new ByteArrayInputStream(returnValue);
		}
	}

	/**
	 * Static setter for the IRemoteCodebaseService as source
	 * 
	 * @param dht the IRemoteCodebaseService to read from
	 */
	public static void setCbService(IRemoteCodebaseService cbService) {
		Handler.cbService = cbService;
	}

}
