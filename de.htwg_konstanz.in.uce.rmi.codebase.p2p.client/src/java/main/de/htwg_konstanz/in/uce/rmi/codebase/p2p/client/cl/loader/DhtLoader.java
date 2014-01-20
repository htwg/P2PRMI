package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;

/**
 * {@link ILoader} implementation using an UceDht as source
 * 
 * @author Marco Krammer
 *
 */
public final class DhtLoader implements ILoader {

	private static final Logger logger = Logger.getLogger(DhtLoader.class.getName());
	private UceDht dht;

	/**
	 * Constructor for DhtLoader,
	 * sets the dht parameter
	 * 
	 * @param dht the UceDht container
	 */
	public DhtLoader(UceDht dht) {
		this.dht = dht;
	}

	/**
	 * Delegates to the UceDht's get method
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.ILoader#toByteArray(java.lang.String)
	 */
	@Override
	public byte[] toByteArray(String name) throws IOException {
		long getStart = System.currentTimeMillis();
		 
       byte[] returnValue;
		try {
			returnValue = dht.get(name);
			
			logger.info("findClass get duration: " + (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - getStart)));
			logger.info("returned " + returnValue.length + " bytes");

		} catch (InterruptedException e) {
			throw new IOException("dht get interrupted", e);
		}
		
		if (null == returnValue || returnValue.length == 0) { 
			throw new IOException("dht get returned null for " + name);
		} else {
			return returnValue;
		}
		
	}
	

}