package de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util;

import java.lang.reflect.Field;
import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Helper class for services
 * 
 * @author Marco Krammer
 * 
 * TODO: add {@link Registry} parameter to startService and
 *       rebindService method
 * 
 */
public class ServiceUtil {

	/**
	 * Method (re)binds a service class to the registry a service class must
	 * have two public fields serviceName and codebase
	 * 
	 * @param serviceClassName the fully qualified class name to load and bind
	 * @return the proxy object returned from rebindService method
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 * @throws NoSuchFieldException
	 */
	public static Remote startService(String serviceClassName)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, RemoteException, AlreadyBoundException,
			NoSuchFieldException {
		Class<?> aclass = Class.forName(serviceClassName);
		Class<? extends Remote> serviceClass = aclass.asSubclass(Remote.class);
		Remote obj = serviceClass.newInstance();

		String serviceName = (String) getFieldValue(serviceClass, obj,
				"serviceName");
		String codebase = (String) getFieldValue(serviceClass, obj, "codebase");

		return rebindService(serviceName, codebase, obj);
	}
	
	/**
	 * Method exports and (re)binds a remote object with the registry
	 * @param name
	 * @param obj
	 * @return
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 * @throws AccessException
	 */
	public static Remote rebindService(String name, Remote obj)
			throws RemoteException, AlreadyBoundException, AccessException {

		Remote stub = UnicastRemoteObject.exportObject(obj, 0);
		checkStartRegistry().rebind(name, stub);
		System.out.println("ServiceUtil: Service '" + name + "' bound.");
		return stub;
	}
	
	private static Remote rebindService(String name, String codebase, Remote obj)
			throws RemoteException, AlreadyBoundException, AccessException {
		if (codebase != null && !codebase.isEmpty()) {
			String setCodebase = System.getProperty("java.rmi.server.codebase", "");
			if (setCodebase.isEmpty()) {
				System.setProperty("java.rmi.server.codebase", codebase);
			} else {
				System.setProperty("java.rmi.server.codebase", setCodebase
						+ " " + codebase);
			}
		}
		return rebindService(name, obj);
	}

	private static Object getFieldValue(Class<? extends Remote> serviceClass,
			Remote obj, String fieldName) throws NoSuchFieldException,
			IllegalAccessException {
		Field field = serviceClass.getDeclaredField(fieldName);
		field.setAccessible(true);
		return field.get(obj);
	}
	
	private static Registry checkStartRegistry() throws RemoteException {
		Registry registry;
		try {
			registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
		} catch (ExportException e) {
			// getRegistry() always returns != null, so we just try
			// createRegistry()
			registry = LocateRegistry.getRegistry();
		}
		return registry;
	}
}
