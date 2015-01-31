package org.uma.jmetal.util.measurement;

import org.uma.jmetal.algorithm.Algorithm;

/**
 * A {@link Measure} aims at providing the {@link Value} of a specific property,
 * typically of an {@link Algorithm}. As such, the {@link Measure} is identified
 * through its name ({@link #getName()}) and further detailed through its
 * description ( {@link #getDescription()}).
 * 
 * @author Created by Antonio J. Nebro on 21/10/14 based on the ideas of
 *         Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of value the {@link Measure} can provide
 */
public interface Measure<Value> {
	/**
	 * 
	 * @return the name of the {@link Measure}, e.g. "population size"
	 */
	public String getName();

	/**
	 * 
	 * @return the description of the {@link Measure}, e.g.
	 *         "Provide the current size of the population of individuals."
	 */
	public String getDescription();
}
