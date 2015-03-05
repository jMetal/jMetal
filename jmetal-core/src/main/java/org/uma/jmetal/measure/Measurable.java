package org.uma.jmetal.measure;

/**
 * A {@link Measurable} entity is an entity which provides one or several
 * {@link Measure}s. To keep it simple, these {@link Measure}s are provided
 * through a {@link MeasureManager}.
 * 
 * @author Created by Antonio J. Nebro on 21/10/14 based on the ideas of
 *         Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface Measurable {
	/**
	 * 
	 * @return the {@link MeasureManager} which stores all the {@link Measure}s
	 *         supported by this {@link Measurable} entity
	 */
	public MeasureManager getMeasureManager();
}
