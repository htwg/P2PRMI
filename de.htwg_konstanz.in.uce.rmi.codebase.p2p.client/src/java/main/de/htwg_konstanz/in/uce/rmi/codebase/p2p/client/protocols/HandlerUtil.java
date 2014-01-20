package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols;


/**
 * Utility class to register an URLStreamHandler
 * 
 * TODO: register method could check if already registered or
 * 		 if given class is a subclass of URLStreamHandler
 * 
 * @author Marco Krammer
 *
 */
public class HandlerUtil {

	/**
	 * Method to register a given URLStreamHandler class
	 * 
	 * @param handlerClass the URLStreamHandler class to register
	 */
	public static void register(Class<?> handlerClass) {	
		final String packageName = handlerClass.getPackage().getName();
		final String pkg = packageName.substring(0, packageName.lastIndexOf('.'));
		final String handlerPkgs = "java.protocol.handler.pkgs";

		String curHandlers = System.getProperty(handlerPkgs, "");
		if (curHandlers.indexOf(pkg) == -1) {
			if (! curHandlers.isEmpty()) {
				curHandlers += "|";
			}
			curHandlers += pkg;
			System.setProperty(handlerPkgs, curHandlers);
		}
	}
}
