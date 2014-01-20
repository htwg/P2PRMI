package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import java.io.ByteArrayOutputStream;

/**
 * Utility class to extract all printable characters from a
 * ByteArrayOutputStream.
 * 
 * @author Marco Krammer
 *
 */
public class StreamUtil {
	
	/**
	 * Extracts all letter and digits from a given ByteArrayOutputStream
	 * 
	 * @param baos the ByteArrayOutputStream to extract from
	 * @return the extracted characters as a String
	 */
	public static String extractPrintables(ByteArrayOutputStream baos) {
		StringBuffer sb = new StringBuffer();
		byte[] bytes = baos.toByteArray();
		for (byte b : bytes) {
			if (Character.isLetterOrDigit((char) b)) {
				sb.append((char) b);
			} else {
				sb.append(".");
			}
		}
		return sb.toString();
	}
}
