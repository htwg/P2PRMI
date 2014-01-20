package example.hello;

import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.CustomClassLoader;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.RmiClassLoaderSpiImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.UrlLoader;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.util.LogHandler;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi and CustomClassLoader
 * 
 * @author Marco Krammer
 *
 */
public class StartClient {

    public static void main(String[] args) throws MalformedURLException {
		System.setProperty("java.security.policy", StartClient.class
				.getClassLoader().getResource("allow_all.policy").toExternalForm());
		System.setSecurityManager(new RMISecurityManager());											
    	
		System.setProperty("sun.rmi.client.logCalls", "true");
		// if false uses class' annotation, else codebase property!
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
		// set the RMIClassLoaderSpi and the custom classloader
    	System.setProperty("java.rmi.server.RMIClassLoaderSpi", 
    			RmiClassLoaderSpiImpl.class.getName());
		RmiClassLoaderSpiImpl.setCustomClassLoader(new CustomClassLoader(new UrlLoader()));
		    	
		try {
			// get the registry
			System.out.println("Client: get registry ...");
			Registry registry = LocateRegistry.getRegistry();
			
        	// get the remote object
			String serviceName = /*PEER_NAME + */ "WelcomeService";
			System.out.println("Client: Looking up service '" + serviceName + "' ...");
	        Remote stub = (Remote) registry.lookup(serviceName);
			
	    	// call the remote getAllGreetings method and 
			// the runtime retrieves the class definitions from the codebase
			IWelcomeService service = (IWelcomeService) stub;
			
			List<? extends IGreeting> greetingImplList = service.getAllGreetings();
			for (IGreeting greetingImpl : greetingImplList) {
				ClassLoader loader = greetingImpl.getClass().getClassLoader();
				Class<?>[] interfaces = new Class<?>[]{ IGreeting.class };
				LogHandler logHandler = new LogHandler(greetingImpl);

				IGreeting greetingProxy = 
						(IGreeting) Proxy.newProxyInstance(loader, interfaces, logHandler);
				System.out.println(greetingProxy.greet());
			}
			
			System.out.println("Client: done.");
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}

    }
	
}