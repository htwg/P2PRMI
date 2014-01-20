package example.hello;

import java.rmi.RemoteException;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi and CustomClassLoader
 * 
 * @author Marco Krammer
 *
 */
public class StartServer {

	public static void main(String args[]) throws RemoteException, InterruptedException {
		StartServerNoWait.main(args);
		
		// serve
		Object lock = new Object();
		synchronized (lock) {
			lock.wait();
		}
	}
	
}
