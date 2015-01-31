package org.uma.jmetal.parameter;

/**
 * A {@link Parameterable} entity is an entity which provides one or several
 * {@link Parameter}s. To keep it simple, these {@link Parameter}s are provided
 * through a {@link ParameterManager}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface Parameterable {
	/**
	 * 
	 * @return the {@link ParameterManager} which stores all the
	 *         {@link Parameter}s supported by this {@link Parameterable} entity
	 */
	public ParameterManager getParameterManager();
}