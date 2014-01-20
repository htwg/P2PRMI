package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util;

import java.rmi.server.RMIClassLoader;

/**
 * Utility class for converting a path to a class name
 * and for printing the annotation of a class
 * 
 * @author Marco Krammer
 *
 */
public class ClassUtil {

	/**
	 * Converts a given string to a valid className
	 * (replaces backslashes with .)
	 * 
	 * @param className to replace backslashes
	 * @return the valid version of className
	 */
	public static String toClassName(String className) {
		if (null == className || className.isEmpty()) {
			return className;
		}
		
		if (className.startsWith(".") || className.startsWith("/")) {
			className = className.substring(1);
		}
		return className.replace("/", ".").replace("\\", ".");
	}

	/**
	 * Prints the System Property java.rmi.server.codebase and 
	 * the class annotation for the given object
	 * 
	 * @param obj the object to get the annotation from
	 */
	public static void printAnnotation(Object obj) {
		System.out.println("java.rmi.server.codebase = " + System.getProperty("java.rmi.server.codebase"));
		System.out.println("class annotation = " + RMIClassLoader.getClassAnnotation(obj.getClass()));
	}
}
