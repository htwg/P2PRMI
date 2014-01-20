package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl;

import java.util.List;

/**
 * Interface to extend the custom classloader functionality
 * 
 * @author Marco Krammer
 * 
 * TODO: replace updateCodebase with add and remove methods?
 *
 */
public interface ICodebaseAwareClassLoader {

	/**
	 * Extends the classloader's codebase list
	 * 
	 * @param codebase the entries to extend the list with
	 */
	public void updateCodebase(String codebase); 

	/**
	 * Returns the list of available codebases
	 * 
	 * @return the codebase list
	 */
	public List<String> getCodebases();

	/**
	 * Load a class using the name
	 * 
	 * @param name the class' full qualified name
	 * @return the defined class
	 * @throws ClassNotFoundException if class can not be found for the given name
	 */
	public Class<?> loadClass(String name) throws ClassNotFoundException;
}