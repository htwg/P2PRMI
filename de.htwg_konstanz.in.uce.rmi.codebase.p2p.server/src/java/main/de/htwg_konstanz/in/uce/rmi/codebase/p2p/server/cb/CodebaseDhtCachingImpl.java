package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;

/**
 * An implementation of the {@link ICodebase} for serving from
 * an UceDht using a Map as cache
 * 
 * @author Marco Krammer
 *
 * TODO: cacheMap should write files to a temporary directory
 * 		 (or should have at least a size limit per entry)
 */
public class CodebaseDhtCachingImpl implements ICodebase<UceDht> {
	
	private Logger logger = Logger.getLogger(CodebaseDhtCachingImpl.class.toString());	
	private UceDht dht;
	private Map<String, byte[]> cacheMap = new HashMap<>();
	private boolean allow_save;

	/**
	 * Constructor for CodebaseDhtCachingImpl,
	 * sets the allow_save parameter
	 * 
	 * @param allow_save if true allows writing to the UceDht
	 */
	public CodebaseDhtCachingImpl(boolean allow_save) {
		this.allow_save = allow_save;
	}
	
	/**
	 * Gets the content for the given name from the UceDht,
	 * also (if successful) puts the content in the cache map
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#get(java.lang.String)
	 */
	@Override
	public byte[] get(String name) throws IOException {
		if (null == dht) { //TODO || ! dht.booted
			throw new IllegalStateException("Source not set!"); 
		}
		
		byte[] bytes = null;
		logger.info("requesting " + name);
		
		if (cacheMap.get(name) != null) {
			logger.info("found file for path in cache");
			bytes = cacheMap.get(name);
		} else {
			try {
				final long start = System.currentTimeMillis();	
				bytes = dht.get(name);
				long duration = System.currentTimeMillis() - start;
				logger.info("dht get " + name + " duration " + duration + " ms\n");
				if (bytes != null && bytes.length > 0) {
					cacheMap.put(name, bytes);
				}
			} catch (InterruptedException e) {
				throw new IOException("dht 'get' interrupted", e);
			}
		}
		return bytes;
	}
	
	/**
	 * Writes the given content under given name as key to the UceDht
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#put(java.lang.String, byte[])
	 */
	@Override
	public void put(String name, byte[] content) throws IOException {
		if (allow_save) {
			final long start = System.currentTimeMillis();
			boolean success;
			try {
				success = dht.put(name, content);

				long duration = System.currentTimeMillis() - start;
				logger.info("dht put '" + name + "' duration " + duration
						+ " ms\n");

				if (success) {
					cacheMap.put(name, content);
				}
			} catch (InterruptedException e) {
				throw new IOException("dht 'put' interrupted", e);
			}
		} else {
			logger.info("Writing files is not allowed!");
			throw new IOException("Writing files is not allowed!");
		}
	}

	/**
	 * Setter for the source
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#setSource(java.lang.Object)
	 */
	@Override
	public void setSource(UceDht dht) {
		this.dht = dht;
	}

	/**
	 * Getter for the source
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#getSource()
	 */
	@Override
	public UceDht getSource() {
		return dht;
	}
	
	/**
	 * Intended for debugging
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CodebaseDhtImpl serving from " + dht;
	}
}
