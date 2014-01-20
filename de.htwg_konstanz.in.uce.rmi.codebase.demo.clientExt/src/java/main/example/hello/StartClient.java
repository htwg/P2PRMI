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
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.RmiLoader;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.loader.UrlLoader;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.util.LogHandler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.util.ClassUtil;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi and CustomClassLoader
 * Extended for RemoteClassService
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
		//if false uses class' annotation, else codebase property!
		System.setProperty("java.rmi.server.useCodebaseOnly", "false");
    	System.setProperty("java.rmi.server.RMIClassLoaderSpi", 
    			RmiClassLoaderSpiImpl.class.getName());


		try {
			// get the registry
			System.out.println("Client: get registry ...");
			Registry registry = LocateRegistry.getRegistry();
			
			// check for remote class service
			String serviceName = /*PEER_NAME + */ "ClassFileService";
			System.out.println("Client: Looking up service '" + serviceName + "' ...");
	        Remote stub = (Remote) registry.lookup(serviceName);
	        
	        // set the custom classloader
	        CustomClassLoader ccl = null;
	        if (stub != null && stub instanceof IRemoteCodebaseService) {
	        	// classloader using remote class service
	        	IRemoteCodebaseService cbService = (IRemoteCodebaseService) stub;
	           	ccl = new CustomClassLoader(new RmiLoader(cbService));
	        } else {
	        	// classloader using http via urlconnection
	        	ccl = new CustomClassLoader(new UrlLoader());
	        }
        	RmiClassLoaderSpiImpl.setCustomClassLoader(ccl);
			
        	// get the remote object
			serviceName = /*PEER_NAME + */"WelcomeService";
			System.out.println("Client: Looking up service '" + serviceName + "' ...");
	        stub = (Remote) registry.lookup(serviceName);
	
			ClassUtil.printAnnotation(stub);

			// call the remote method, when the getAllGreetings method is called
			// the runtime retrieves the codebase's class definitions
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
			
			System.out.println("Client done.");
		
		} catch (RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
    }
	
}