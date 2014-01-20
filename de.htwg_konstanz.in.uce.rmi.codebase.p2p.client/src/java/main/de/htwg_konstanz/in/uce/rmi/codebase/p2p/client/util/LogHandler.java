package de.htwg_konstanz.in.uce.rmi.codebase.p2p.client.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * Implementation of an {@link InvocationHandler}
 * Just logs the method name to invoke and
 * the result of the method.
 * 
 * @author Marco Krammer
 *
 */
public class LogHandler implements InvocationHandler {
	private static final Logger logger = Logger.getLogger(LogHandler.class.getName());
	private Object object;

	/**
	 * Constructor of LogHandler,
	 * sets the parameter object
	 * 
	 * @param object the object to invoke the method on
	 */
	public LogHandler(Object object) {
		this.object = object;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		logger.info("Invoking method " + method.getName());
		Object result = method.invoke(this.object, args);

		logger.info("Result: " + result);
		return result;
	}
}