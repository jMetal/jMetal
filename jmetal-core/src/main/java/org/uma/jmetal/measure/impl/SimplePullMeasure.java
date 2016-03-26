package org.uma.jmetal.measure.impl;

import org.uma.jmetal.measure.Measure;
import org.uma.jmetal.measure.PullMeasure;

/**
 * {@link SimplePullMeasure} is a basic implementation of {@link PullMeasure}.
 * As a {@link PullMeasure}, it is intended to be used by external entities
 * through its {@link #get()} method. This method must be implemented by the
 * algorithm to specify how the value can be retrieved.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
@SuppressWarnings("serial")
public abstract class SimplePullMeasure<Value> extends SimpleMeasure<Value>
		implements PullMeasure<Value> {

	/**
	 * Create a {@link SimplePullMeasure} with a given name and a given
	 * description.
	 * 
	 * @param name
	 *            the name of the {@link Measure}
	 * @param description
	 *            the description of the {@link Measure}
	 */
	public SimplePullMeasure(String name, String description) {
		super(name, description);
	}

	/**
	 * Create a {@link SimplePullMeasure} with a given name and a
	 * <code>null</code> description.
	 * 
	 * @param name
	 *            the name of the {@link Measure}
	 */
	public SimplePullMeasure(String name) {
		super(name);
	}

	/**
	 * Create a {@link SimplePullMeasure} with the class name as its name and a
	 * <code>null</code> description.
	 * 
	 */
	public SimplePullMeasure() {
		super();
	}

}
