package example.hello;

import example.hello.IGreeting;

/**
 * This implementation greets in French
 * 
 * @author Marco Krammer
 *
 */
public class BienvenueImpl implements IGreeting {

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
		return "Bienvenue!";
	}

}
