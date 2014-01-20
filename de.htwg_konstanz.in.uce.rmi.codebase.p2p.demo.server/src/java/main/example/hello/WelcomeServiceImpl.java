package example.hello;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import example.hello.IGreeting;
import example.hello.IWelcomeService;

/**
 * This class implements the {@link IWelcomeService} interface,
 * the service method, returns three implementations
 * of the {@link IGreeting} interface
 * 
 * @author Marco Krammer
 *
 */
public class WelcomeServiceImpl implements IWelcomeService {

	/**
	 * Default constructor
	 */
	public WelcomeServiceImpl() {
		super();
	}
	
	/*
	 * (non-Javadoc)
	 * @see example.hello.IWelcomeService#getAllGreetings()
	 */
	@Override
	public List<IGreeting> getAllGreetings() throws RemoteException {
		List<IGreeting> list = new ArrayList<>();
		list.add(new GreetingImpl());
		list.add(new BegruessungImpl());
		list.add(new BienvenueImpl());
		return list;
	}

}
