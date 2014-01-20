package example.hello;

import example.hello.IGreeting;

/**
 * This implementation greets in English
 * 
 * @author Marco Krammer
 *
 */
public class GreetingImpl implements IGreeting {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/*
	 * (non-Javadoc)
	 * @see example.hello.IGreeting#greet()
	 */
	@Override
	public String greet() {
		return "Welcome!";
	}
}
