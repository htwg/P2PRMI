package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi;

import java.io.IOException;
import java.rmi.RemoteException;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.cb.ICodebase;

/**
 * Implementation of IRemoteCodebaseService,
 * serves from a given codebase, also saving can be allowed.
 * 
 * @author Marco Krammer
 *
 * @param <T>
 */
public class RmiCodebaseService<T> implements IRemoteCodebaseService {
	
	public static final String CODEBASE_SERVICE_NAME = "CodebaseService";
	private ICodebase<T> codebase;
	private boolean allow_upload;

	/**
	 * Constructor for RmiCodebaseService,
	 * sets the codebase and allow_upload parameter
	 * 
	 * @param codebase to use as container
	 * @param allow_upload if true the put method of the codebase will
	 * 	be called, the codebase must support this operation
	 */
	public RmiCodebaseService(ICodebase<T> codebase, boolean allow_upload) {
		super();
		this.codebase = codebase;
		this.allow_upload = allow_upload;
	}

	/**
	 * Delegates to the codebase's get method
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService#get(java.lang.String)
	 */
	@Override
	public byte[] get(String path) throws RemoteException {
		try {
			return codebase.get(path);
		} catch (IOException e) {
			throw new RemoteException("IOException while getting file " + path, e);
		}
	}

	/**
	 * Delegates to the codebase's put method
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService#put(java.lang.String, byte[])
	 */
	@Override
	public void put(String name, byte[] content) throws RemoteException {
		if (allow_upload) {
			try {
				codebase.put(name, content);
			} catch (IOException e) {
				throw new RemoteException("IOException while saving file " + name, e);
			}
		} else {
			throw new RemoteException("Upload is not allowed!");
		}
	}
}
