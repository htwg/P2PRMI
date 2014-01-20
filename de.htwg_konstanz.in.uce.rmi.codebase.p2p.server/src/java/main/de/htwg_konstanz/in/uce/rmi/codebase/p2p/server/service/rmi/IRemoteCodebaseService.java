package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface for the remote codebase service
 * 
 * @author Marco Krammer
 *
 */
public interface IRemoteCodebaseService extends Remote {

	/**
	 * Method returns the byte[] for a given name/id/path
	 * 
	 * @param name id or path to look for
	 * @return the byte[] of the file content
	 * @throws RemoteException if e.g. IOException happens
	 */
	public byte[] get(String name) throws RemoteException;
	
	
	/**
	 * Method to save a file or an id with given content
	 * 
	 * @param name id or path to save content under
	 * @param content the content as byte[]
	 * @throws RemoteException if e.g. IOException happens
	 */
	public void put(String name, byte[] content) throws RemoteException;
}
