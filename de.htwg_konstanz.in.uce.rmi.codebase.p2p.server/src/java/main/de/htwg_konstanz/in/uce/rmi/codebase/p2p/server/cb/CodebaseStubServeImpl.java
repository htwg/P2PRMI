package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * An implementation of the {@link ICodebase} for serving
 * serialized stubs
 * 
 * @author Marco Krammer
 *
 */
public class CodebaseStubServeImpl implements ICodebase<Map<String, byte[]>> {
	
	private static final String FILE_SUFFIX = ":STUB";
	private Logger logger = Logger.getLogger(CodebaseStubServeImpl.class.toString());	
	private Map<String, byte[]> sourceMap = new HashMap<>(); //ByteBuffer or to file?
	private boolean allow_save;	
	
	/**
	 * Constructor for CodebaseStubServeImpl,
	 * sets the allow_save parameter
	 */
	public CodebaseStubServeImpl(boolean allow_save) {
		this.allow_save = allow_save;
	}

	/**
	 * Returns the stub for the given name (if existent)
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#get(java.lang.String)
	 */
	@Override
	public byte[] get(String name) throws IOException {
		logger.info("requesting " + name);
		
		if (! name.endsWith(FILE_SUFFIX)) {
			throw new IOException("Not a valid request: " + name);
		}
		
		String stubName = name.substring(1, name.indexOf(FILE_SUFFIX));
		byte[] bytes = sourceMap.get(stubName);		
		if (bytes != null) {
			logger.info("Found stub for " + stubName + " in cache");
		} 
		return bytes;
	}

	/**
	 * Saves a stub to the map
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#put(java.lang.String, byte[])
	 */
	@Override
	public void put(String name, byte[] content) throws IOException {
		if (allow_save) {
			logger.info("Put " + name);
			sourceMap.put(name, content);
		} else {
			logger.info("Put is not allowed!");
			throw new IOException("Put is not allowed!");
		}
	}
	
	/**
	 * Setter for the source
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#setSource(java.lang.Object)
	 */
	@Override
	public void setSource(Map<String, byte[]> sourceMap) {
		this.sourceMap = sourceMap;
	}

	/**
	 * Getter for the source
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase#getSource()
	 */
	@Override
	public Map<String, byte[]> getSource() {
		return sourceMap;
	}
	
	/**
	 * Intended for debugging
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CodebaseStubServeImpl serving from " + sourceMap;
	}

}
