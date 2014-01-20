package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.ILoader;

/**
 * Custom classloader implementation 
 * 
 * @author Marco Krammer
 * 
 * TODO: getClass method could use some heuristics to load not only in FIFO order 
 * 		 from the codebaseList but e.g. to compare files from various sources etc.
 *
 */
public class CustomClassLoader extends ClassLoader implements ICodebaseAwareClassLoader {

	private static final Logger logger = Logger.getLogger(CustomClassLoader.class.getName());
	
	private List<String> codebaseList = new ArrayList<>();

	private final ILoader loader;
	
	/**
	 * Constructor for CustomClassLoader,
	 * sets the parameter loader
	 * 
	 * @param loader the ILoader implementation to use as source
	 */
	public CustomClassLoader(ILoader loader) {
		super();
		this.loader = loader;
	}
	
	/**
	 * Constructor for CustomClassLoader,
	 * sets the parameters parent and loader
	 * 
	 * @param parent the parent classloader
	 * @param loader the ILoader implementation to use as source
	 */
	public CustomClassLoader(ClassLoader parent, ILoader loader) {
		super(parent);
		this.loader = loader;
	}
		
	private Class<?> getClass(String name) throws ClassNotFoundException {
		Class<?> c = null;
		try {
			//first try if class is already available
			c = Class.forName(name);
			logger.info("found " + name + " locally");
		} catch (ClassNotFoundException e) {

			//2nd try from (all) codebase entries
			String file = name.replace('.', '/') + ".class";
			for (String s : codebaseList) {
				try {
					byte[] b = loader.toByteArray(s + "/" + file);
					if (b.length > 0) {
						c = super.defineClass(name, b, 0, b.length);
						if (c != null) { //TODO check?
							logger.info("FOUND " + name + " via " + s);
							break;
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			//last try via parent classloader
			if (null == c) {
				c = super.loadClass(name);	//TODO check
			}
		}
		
		if (c != null) {
			super.resolveClass(c);	//TODO not needed if super.loadClass
			return c;
		} else {
			throw new ClassNotFoundException();
		}
	}

	/**
	 * Delegates to the getClass method
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.ICodebaseAwareClassLoader#loadClass(java.lang.String)
	 */
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		logger.info("loading class '" + name + "'");
		return getClass(name);
	}
		
	/**
	 * Updates the codebase list
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.ICodebaseAwareClassLoader#updateCodebase(java.lang.String)
	 */
	@Override
	public void updateCodebase(String codebase) {	
		if (null == codebase || codebase.isEmpty()) {
			return;
		}

		String[] urls = codebase.split("\\s");

		for (String url : urls) {
			if (!codebaseList.contains(url)) {
				codebaseList.add(url);
				logger.info(url + " added to codebase list.");
			}
		}
	}

	/**
	 * Simple getter for the codebase list
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.ICodebaseAwareClassLoader#getCodebases()
	 */
	@Override
	public List<String> getCodebases() {
		return codebaseList;
	}
	
}