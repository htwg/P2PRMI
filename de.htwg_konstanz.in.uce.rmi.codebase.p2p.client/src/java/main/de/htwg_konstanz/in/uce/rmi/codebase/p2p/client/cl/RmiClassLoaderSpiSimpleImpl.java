package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl;

import java.net.MalformedURLException;
import java.rmi.server.RMIClassLoader;
import java.rmi.server.RMIClassLoaderSpi;

/**
 * Simple SPI implementation for RMIClassLoader
 * 
 * The only difference to the default one is,
 * that the getClassAnnotation-Method reads the 
 * System Property java.rmi.server.codebase in every call,
 * instead of returning the cached one.
 * 
 * @author Marco Krammer
 *
 */
public class RmiClassLoaderSpiSimpleImpl extends RMIClassLoaderSpi {

    private static final RMIClassLoaderSpi delegate = RMIClassLoader.getDefaultProviderInstance();

	/**
	 * Delegates to the default provider
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#loadClass(java.lang.String,
	 *      java.lang.String, java.lang.ClassLoader)
	 */
    @Override
    public Class<?> loadClass(String codebase, String name, ClassLoader defaultLoader)
            throws MalformedURLException, ClassNotFoundException {

        return delegate.loadClass(codebase, name, defaultLoader);
    }

	/**
	 * Delegates to the default provider
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#loadProxyClass(java.lang.String,
	 *      java.lang.String[], java.lang.ClassLoader)
	 */
    @Override
    public Class<?> loadProxyClass(String codebase, String[] interfaces, ClassLoader defaultLoader)
            throws MalformedURLException, ClassNotFoundException {
        return delegate.loadProxyClass(codebase, interfaces, defaultLoader);
    }

	/**
	 * Delegates to the default provider
	 * 
	 * @see java.rmi.server.RMIClassLoaderSpi#getClassLoader(java.lang.String)
	 */
    @Override
    public ClassLoader getClassLoader(String codebase) throws MalformedURLException {
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

}
