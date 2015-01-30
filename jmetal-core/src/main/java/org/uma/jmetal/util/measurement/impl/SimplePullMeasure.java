package org.uma.jmetal.util.measurement.impl;

import org.uma.jmetal.util.measurement.PullMeasure;

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
public abstract class SimplePullMeasure<T> implements PullMeasure<T> {

	/**
	 * The name of the measure.
	 */
	private final String name;
	/**
	 * The description of the measure.
	 */
	private final String description;

	/**
	 * Create a {@link SimplePullMeasure} with a given name and a given
	 * description.
	 * 
	 * @param name
	 *            the name of the measure
	 * @param description
	 *            the description of the measure
	 */
	public SimplePullMeasure(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Create a {@link SimplePullMeasure} with a given name and a
	 * <code>null</code> description.
	 * 
	 * @param name
	 *            the name of the measure
	 */
	public SimplePullMeasure(String name) {
		this(name, null);
	}

	/**
	 * Create a {@link SimplePullMeasure} with the class name as its name and a
	 * <code>null</code> description.
	 * 
	 * @param name
	 *            the name of the measure
	 */
	public SimplePullMeasure() {
		this(SimplePullMeasure.class.getSimpleName());
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescription() {
		return description;
	}

}
