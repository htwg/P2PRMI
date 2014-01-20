package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb;

import java.io.IOException;

/**
 * Container interface to type parameterize the codebase
 * 
 * @author Marco Krammer
 *
 * @param <T>
 */
public interface ICodebase<T> {

	/**
	 * Retrieves the class definition as a byte[] for a
	 * given path.
	 * 
	 * @param name/id or location to look for the resource
	 * @return the file content as byte[]
	 * @throws IOException if e.g. nothing found for path or invalid
	 */
	byte[] get(String name) throws IOException;
	
	/**
	 * Saves a class definition
	 * 
	 * @param name
	 * @param content
	 */
	void put(String name, byte[] content) throws IOException;

	/**
	 * Simple setter for the source
	 * 
	 * @param source to set
	 */
	void setSource(T source);
	
	/**
	 * Simple getter for the source
	 * 
	 * @return the source
	 */
	T getSource();
}