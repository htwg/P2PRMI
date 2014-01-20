package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An implementation of the RMIClassLoaderSpi
 * 
 * Uses a custom classloader and updates the classloader's codebase before
 * loading a class. Also returns the System Property java.rmi.server.codebase in
 * the getClassAnnotation method instead of the delegate's return value.
 * 
 * @author Marco Krammer
 * 
 */
public class RmiClassLoaderSpiImpl extends RMIClassLoaderSpi {

	private static final Logger logger = Logger
			.getLogger(RmiClassLoaderSpiImpl.class.getName());

	private static final RMIClassLoaderSpi delegate = RMIClassLoader
			.getDefaultProviderInstance();
	private static ICodebaseAwareClassLoader customClassLoader;

	/**
	 * Updates the codebase of the custom classloader and tries to load the
	 * class using this class loader, if this fails, it delegates to the default
	 * provider
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#loadClass(java.lang.String,
	 *      java.lang.String, java.lang.ClassLoader)
	 */
	@Override
	public Class<?> loadClass(String codebase, String name,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {

		if (customClassLoader != null) {
			if (codebase != null && !codebase.isEmpty()) {
				logger.info("loadClass " + name + ", " + codebase + ", "
						+ defaultLoader);

				customClassLoader.updateCodebase(codebase);

				try {
					return customClassLoader.loadClass(name);
				} catch (ClassNotFoundException e) {
					logger.log(Level.SEVERE,
							"CustomClassLoader failed to load class " + name, e);
				}
			}
		}
		return delegate.loadClass(codebase, name, defaultLoader);
	}

	/**
	 * Delegates to the default provider
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#loadProxyClass(java.lang.String,
	 *      java.lang.String[], java.lang.ClassLoader)
	 */
	@Override
	public Class<?> loadProxyClass(String codebase, String[] interfaces,
			ClassLoader defaultLoader) throws MalformedURLException,
			ClassNotFoundException {
		return delegate.loadProxyClass(codebase, interfaces, defaultLoader);
	}

	/**
	 * Delegates to the default provider
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#getClassLoader(java.lang.String)
	 */
	@Override
	public ClassLoader getClassLoader(String codebase)
			throws MalformedURLException {
		return delegate.getClassLoader(codebase);
	}

	/**
	 * Does not delegate, returns the value of the System Property
	 * java.rmi.server.codebase
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#getClassAnnotation(java.lang.Class)
	 */
	@Override
	public String getClassAnnotation(Class<?> type) {
		return System.getProperty("java.rmi.server.codebase");
	}

	/**
	 * Static setter for the custom classloader
	 * 
	 * @param customClassLoader
	 *            the classloader to be set
	 */
	public static void setCustomClassLoader(
			ICodebaseAwareClassLoader customClassLoader) {
		RmiClassLoaderSpiImpl.customClassLoader = customClassLoader;
	}

}
