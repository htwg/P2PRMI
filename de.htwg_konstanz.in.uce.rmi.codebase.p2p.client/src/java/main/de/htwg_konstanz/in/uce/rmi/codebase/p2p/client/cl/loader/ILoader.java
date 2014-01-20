package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader;

import java.io.IOException;

/**
 * Interface used by the codebase service facades to get the byte[]
 * for a given name, id or path
 * 
 * @author Marco Krammer
 *
 */
public interface ILoader {
	
	/**
	 * Returns the byte[] for a given name
	 * 
	 * @param name the id or path
	 * @return the content of the file
	 * @throws IOException if file not found or unreadable
	 */
	byte[] toByteArray(String name) throws IOException;
}
