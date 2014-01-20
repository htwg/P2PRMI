package example.hello;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.Logger;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util.ServiceUtil;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi and CustomClassLoader
 * 
 * @author Marco Krammer
 *
 */
public class StartServerNoWait {
	private static final Logger logger = Logger.getLogger(StartServerNoWait.class.getName());
	
	public static void main(String args[]) throws RemoteException, InterruptedException {
	//		System.setProperty("java.rmi.server.logCalls", "true");
	//		System.setProperty("sun.rmi.server.exceptionTrace", "true");
	//		
	//		System.setProperty("sun.rmi.server.logLevel", "VERBOSE");
	//		System.setProperty("sun.rmi.transport.logLevel", "VERBOSE");
	//		System.setProperty("sun.rmi.transport.proxy.logLevel", "VERBOSE");
	//		System.setProperty("sun.rmi.loader.logLevel", "VERBOSE");
	//		System.setProperty("sun.rmi.dgc.logLevel", "VERBOSE");
	//		
	//		System.setProperty("sun.rmi.server.suppressStackTraces", "false");
	//		System.setProperty("sun.rmi.server.exceptionTrace", "true");

		String[] serviceClassNames;
		if (args.length != 0) {
			serviceClassNames = args;
		} else {
			serviceClassNames = new String[]{ "example.hello.WelcomeServiceImpl" };
		}
		
		for (String serviceClassName : serviceClassNames) {
			try {
				Remote stub = ServiceUtil.startService(serviceClassName);
				logger.info(stub.toString());
			} catch (AlreadyBoundException | RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				logger.severe("Not a valid service class: " + serviceClassName);
				e.printStackTrace();
			}
		}
	}
	
}
