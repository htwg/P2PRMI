package example.hello;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * The interface of the sample service, extends {@link Remote}
 * 
 * @author Marco Krammer
 *
 */
public interface IWelcomeService extends Remote {
	
	/**
	 * An implementation of this method returns 
	 * a list of {@link IGreeting} implementations
	 * 
	 * @return the list of implementations
	 * @throws RemoteException
	 */
    public List<IGreeting> getAllGreetings() throws RemoteException;
}