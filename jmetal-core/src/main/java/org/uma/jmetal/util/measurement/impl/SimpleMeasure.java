package org.uma.jmetal.util.measurement.impl;

import org.uma.jmetal.util.measurement.Measure;

/**
 * {@link SimpleMeasure} is a basic implementation of {@link Measure}. It
 * provides a basic support for the most generic properties required by this
 * interface.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public class SimpleMeasure<Value> implements Measure<Value> {

	/**
	 * The name of the measure.
	 */
	private String name;
	/**
	 * The description of the measure.
	 */
	private String description;

	/**
	 * Create a {@link SimpleMeasure} with a given name and a given description.
	 * 
	 * @param name
	 *            the name of the {@link Measure}
	 * @param description
	 *            the description of the {@link Measure}
	 */
	public SimpleMeasure(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Create a {@link SimpleMeasure} with a given name and a <code>null</code>
	 * description.
	 * 
	 * @param name
	 *            the name of the {@link Measure}
	 */
	public SimpleMeasure(String name) {
		this(name, null);
	}

	/**
	 * Create a {@link SimpleMeasure} with the class name as its name and a
	 * <code>null</code> description.
	 * 
	 */
	public SimpleMeasure() {
		this(SimpleMeasure.class.getSimpleName());
	}

	/**
	 * 
	 * @param name
	 *            the new name of this measure
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param description
	 *            the new description of this measure
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return getName();
	}
}
