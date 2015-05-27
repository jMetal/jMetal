package org.uma.jmetal.parameter;

import java.util.Collection;

/**
 * A {@link ParameterManager} aims at managing a set of {@link Parameter}s.
 * Typically, a {@link Parameterable} entity would create a single
 * {@link ParameterManager} to store all the {@link Parameter}s it would like to
 * use, each of them being identified by a key. The available keys are provided
 * by {@link #getParameterKeys()} while retrieving a specific {@link Parameter}
 * would be done through {@link #getParameter(Object)}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface ParameterManager extends Iterable<Parameter<?>> {
	/**
	 * 
	 * @return the set of {@link Parameter}s managed by this
	 *         {@link ParameterManager}
	 */
	public Collection<Parameter<?>> getParameters();
}