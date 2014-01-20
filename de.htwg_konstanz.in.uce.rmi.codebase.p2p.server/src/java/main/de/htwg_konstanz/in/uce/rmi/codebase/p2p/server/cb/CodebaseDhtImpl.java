package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb;

import java.io.IOException;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.dht.dht_access.UceDht;

/**
 * An implementation of the {@link ICodebase},
 * using an UceDht as container
 * 
 * @author Marco Krammer
 *
 */
public class CodebaseDhtImpl implements ICodebase<UceDht> {

	private Logger logger = Logger.getLogger(CodebaseDhtImpl.class.toString());
	private UceDht dht;
	private boolean allow_save;	
	
	/**
	 * Constructor for CodebaseDhtImpl,
	 * sets the parameter allow_save
	 * 
	 * @param allow_save if true allows writing to the UceDht
	 */
	public CodebaseDhtImpl(boolean allow_save) {
		this.allow_save = allow_save;
	}
	
	/**
	 * Gets the content for the given name from the UceDht
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#get(java.lang.String)
	 */
	@Override
	public byte[] get(String name) throws IOException {
		if (null == dht) { // TODO || ! dht.booted
			throw new IllegalStateException("Source not set!");
		}

		byte[] bytes = null;
		logger.info("requesting " + name);

		final long start = System.currentTimeMillis();

		try {
			bytes = dht.get(name);
			long duration = System.currentTimeMillis() - start;
			logger.info("dht get " + name + " duration " + duration + " ms\n");
		} catch (InterruptedException e) {
			throw new IOException("dht get interrupted", e);
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
			try {
				final long start = System.currentTimeMillis();
				dht.put(name, content);	//TODO !success throw IOE
				long duration = System.currentTimeMillis() - start;
				logger.info("dht put " + name + " duration " + duration
						+ " ms\n");
			} catch (InterruptedException e) {
				throw new IOException("dht put interrupted", e);
			}
		} else {
			logger.info("put not allowed!");
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
