package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader;

import java.io.IOException;
import java.net.URL;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;

/**
 * {@link ILoader} implementation using a RMI codebase service
 * as source.
 * 
 * @author Marco Krammer
 *
 */
public class RmiLoader implements ILoader {

	private IRemoteCodebaseService cbService;
	
	/**
	 * Constructor of RmiLoader,
	 * sets the cbService parameter
	 * 
	 * @param cbService the IRemoteCodebaseService to set
	 */
	public RmiLoader(IRemoteCodebaseService cbService) {
		this.cbService = cbService;		
	}
	
	/**
	 * Delegates to the get method of the IRemoteCodebaseService
	 * 
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.ILoader#toByteArray(java.lang.String)
	 */
	@Override
	public byte[] toByteArray(String path) throws IOException {
		path = new URL(path).getPath();
		return cbService.get(path);
	}


}
