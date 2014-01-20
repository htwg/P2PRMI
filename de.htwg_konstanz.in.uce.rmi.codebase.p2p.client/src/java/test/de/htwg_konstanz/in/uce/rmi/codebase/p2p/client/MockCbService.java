package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;

/**
 * Mock implementation of an IRemoteCodebaseService
 * 
 * @author Marco Krammer
 *
 */
public class MockCbService implements IRemoteCodebaseService {

	private Map<String, byte[]> map = new HashMap<>();
	
	/*
	 * (non-Javadoc)
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService#get(java.lang.String)
	 */
	@Override
	public byte[] get(String name) throws RemoteException {
		return map.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService#put(java.lang.String, byte[])
	 */
	@Override
	public void put(String name, byte[] content) throws RemoteException {
		map.put(name, content);
	}

}
