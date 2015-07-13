package org.uma.jmetal.parameter.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.ParameterManager;

/**
 * This {@link SimpleParameterManager} provides a basic implementation to manage
 * a collection of {@link Parameter}s. One can use the
 * {@link #setParameter(Object, Parameter)} method to configure the
 * {@link Parameter}s individually, or exploit the massive
 * {@link #setAllParameters(Map)} to register a set of {@link Parameter}s at
 * once. The corresponding removeXxx and getXxx methods are also available for
 * each case.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class SimpleParameterManager implements ParameterManager {

	/**
	 * The {@link Parameter}s registered to this {@link SimpleParameterManager}.
	 */
	private Map<String, Parameter<?>> parameters = new HashMap<>();

	@Override
	public Collection<Parameter<?>> getParameters() {
		return parameters.values();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <Value> Parameter<Value> getParameter(String name) {
		return (Parameter<Value>) parameters.get(name);
	}

	/**
	 * 
	 * @param parameter
	 *            the {@link Parameter} to register
	 */
	public void addParameter(Parameter<?> parameter) {
		if (parameters.containsKey(parameter.getName())) {
			throw new RuntimeException(
					"The new parameter "
							+ parameter
							+ " has the same name than another parameter already registered: "
							+ parameters.get(parameter.getName()));
		} else {
			parameters.put(parameter.getName(), parameter);
		}
	}

	/**
	 * 
	 * @param parameter
	 *            the {@link Parameter} to remove
	 */
	public void removeParameter(Parameter<?> parameter) {
		this.parameters.values().remove(parameter);
	}

	/**
	 * 
	 * @param name
	 *            the name of the {@link Parameter} to remove
	 */
	public void removeParameter(String name) {
		this.parameters.remove(name);
	}

	/**
	 * Massive equivalent of {@link #addParameter(Parameter)}.
	 * 
	 * @param parameters
	 *            the {@link Parameter}s to register with their corresponding
	 *            keys
	 */
	public void addAllParameters(Collection<? extends Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			addParameter(parameter);
		}
	}

	/**
	 * Massive equivalent to {@link #removeParameter(Parameter)}.
	 * 
	 * @param parameters
	 *            the {@link Parameter}s to remove
	 */
	public void removeAllParameters(
			Collection<? extends Parameter<?>> parameters) {
		for (Parameter<?> parameter : parameters) {
			removeParameter(parameter);
		}
	}

	@Override
	public Iterator<Parameter<?>> iterator() {
		return parameters.values().iterator();
	}
}
