package org.uma.jmetal.parameter.impl;

import java.util.Collection;
import java.util.HashSet;
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
	private Collection<Parameter<?>> parameters = new HashSet<>();

	@Override
	public Collection<Parameter<?>> getParameters() {
		return parameters;
	}

	/**
	 * 
	 * @param parameter
	 *            the {@link Parameter} to register
	 */
	public void addParameter(Parameter<?> parameter) {
		parameters.add(parameter);
	}

	/**
	 * 
	 * @param parameter
	 *            the {@link Parameter} to remove
	 */
	public void removeParameter(Parameter<?> parameter) {
		this.parameters.remove(parameter);
	}

	/**
	 * Massive equivalent of {@link #addParameter(Parameter)}.
	 * 
	 * @param parameters
	 *            the {@link Parameter}s to register with their corresponding
	 *            keys
	 */
	public void addAllParameters(Collection<? extends Parameter<?>> parameters) {
		this.parameters.addAll(parameters);
	}

	/**
	 * Massive equivalent to {@link #removeParameter(Parameter)}.
	 * 
	 * @param parameters
	 *            the {@link Parameter}s to remove
	 */
	public void removeAllParameters(
			Collection<? extends Parameter<?>> parameters) {
		this.parameters.removeAll(parameters);
	}

	@Override
	public Iterator<Parameter<?>> iterator() {
		return parameters.iterator();
	}
}
