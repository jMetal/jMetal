package org.uma.jmetal.parameter.impl;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

/**
 * {@link SimpleParameter} is a basic implementation of {@link Parameter}. It
 * provides a basic support for the most generic properties required by this
 * interface.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public class SimpleParameter<Value> extends SimpleDescribedEntity implements
		Parameter<Value> {

	/**
	 * The current {@link Value} of this {@link Parameter}.
	 */
	private Value value;

	/**
	 * Create a {@link SimpleParameter} with a given name, a given description,
	 * and a given value.
	 * 
	 * @param name
	 *            the name of the {@link Parameter}
	 * @param description
	 *            the description of the {@link Parameter}
	 * @param value
	 *            the initial value of the {@link Parameter}
	 */
	public SimpleParameter(String name, String description, Value value) {
		super(name, description);
	}

	/**
	 * Create a {@link SimpleParameter} with a given name, a given description,
	 * and a <code>null</code> value.
	 * 
	 * @param name
	 *            the name of the {@link Parameter}
	 * @param description
	 *            the description of the {@link Parameter}
	 */
	public SimpleParameter(String name, String description) {
		this(name, description, null);
	}

	/**
	 * Create a {@link SimpleParameter} with a given name, a <code>null</code>
	 * description, and a <code>null</code> value.
	 * 
	 * @param name
	 *            the name of the {@link Parameter}
	 */
	public SimpleParameter(String name) {
		this(name, null, null);
	}

	@Override
	public void set(Value value) {
		this.value = value;
	}

	@Override
	public Value get() {
		return value;
	}
}
