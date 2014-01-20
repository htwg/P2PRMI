package example.hello;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.util.List;

import com.aelitis.azureus.core.dht.transport.DHTTransportException;

import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.cl.RmiClassLoaderSpiSimpleImpl;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.HandlerUtil;
//import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.dhtcb.Handler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.protocols.rmicb.Handler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.util.LogHandler;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.IRemoteCodebaseService;
import de.htwg_konstanz.in.uce.rmi.codebase.p2p.server.service.rmi.RmiCodebaseService;
import de.htwg_konstanz.in.uce.rmi.registry.p2p.peer.UrlRmiRegistry;

/**
 * Solution via java.rmi.server.RMIClassLoaderSpi WITHOUT CustomClassLoader,
 * but with URLStreamHandler
 * 
 * @author Marco Krammer
 *
 */
public class StartClientPeer {

	private static String STUB_SERVER_URL = "http://192.168.0.10:2001/";

    public static void main(String[] args) throws SecurityException, DHTTransportException, InterruptedException, ClassNotFoundException, IOException, NotBoundException {
    			    		
		System.setProperty("java.security.policy", StartClientPeer.class.getClassLoader().getResource("allow_all.policy").toExternalForm());
		System.setSecurityManager(new RMISecurityManager());											
    	
		System.setProperty("sun.rmi.client.logCalls", "true");
		System.setProperty("sun.rmi.loader.logLevel", "VERBOSE"); 
		
		System.setProperty("java.rmi.server.useCodebaseOnly", "false"); //if false uses class' annotation, else codebase property!
    	System.setProperty("java.rmi.server.RMIClassLoaderSpi", RmiClassLoaderSpiSimpleImpl.class.getName());
		
    	
    	if (args.length == 1) {
    		STUB_SERVER_URL = args[0];
    	}
    	
		long start, duration;

		start = System.currentTimeMillis();
		
		// create the UrlRmiRegistry
		UrlRmiRegistry registry = UrlRmiRegistry.getRegistry(new URL(STUB_SERVER_URL));
    	    	
		// get the remote codebase service
		String serviceName = RmiCodebaseService.CODEBASE_SERVICE_NAME;
		System.out.println("[ClientPeer.main]  Looking up service '" + serviceName + "' ...");
        IRemoteCodebaseService cbService = (IRemoteCodebaseService) registry.lookup(serviceName);
		duration = System.currentTimeMillis() - start;
		System.out.printf("[ClientPeer.main] Received stub after %5d ms\n", duration);

		Handler.setCbService(cbService);
    	HandlerUtil.register(Handler.class);
    	
		// get the demo service
    	serviceName = "WelcomeService";
		System.out.println("[ClientPeer.main] Looking up service '" + serviceName + "' ...");
		start = System.currentTimeMillis();
		IWelcomeService service = (IWelcomeService) registry.lookup(serviceName);
		duration = System.currentTimeMillis() - start;
		System.out.printf("[ClientPeer.main] Received stub after %5d ms\n", duration);

		// call the remote getAllGreetings method and 
		// the runtime retrieves the class definitions from the codebase
		List<? extends IGreeting> greetingImplList = service.getAllGreetings();
		for (IGreeting greetingImpl : greetingImplList) {
			ClassLoader loader = greetingImpl.getClass().getClassLoader();
			Class<?>[] interfaces = new Class<?>[]{ IGreeting.class };
			LogHandler logHandler = new LogHandler(greetingImpl);

			IGreeting greetingProxy = (IGreeting) Proxy.newProxyInstance(loader, interfaces, logHandler);
			System.out.println(greetingProxy.greet());
		}
		
		System.out.println("Client: done.");
    }
    	
}