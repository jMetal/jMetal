package org.uma.jmetal.parameter.configuration.impl;

import org.uma.jmetal.parameter.Parameter;
import org.uma.jmetal.parameter.configuration.ConfigurationUnit;

/**
 * An {@link ImmutableConfigurationUnit} is a {@link ConfigurationUnit} which
 * takes its {@link Parameter} and {@link Value} at the instantiation and can't
 * change them afterward. However, the {@link Parameter} can still change its
 * value and the method {@link #apply()} can be called to set the
 * {@link Parameter} to the stored {@link Value}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 */
public class ImmutableConfigurationUnit<Value> implements
		ConfigurationUnit<Value> {

	private final Parameter<Value> parameter;
	private final Value value;

	/**
	 * Create an {@link ImmutableConfigurationUnit} on the given
	 * {@link Parameter} and {@link Value}.
	 * 
	 * @param parameter
	 *            the {@link Parameter} to target
	 * @param value
	 *            the {@link Value} to target
	 */
	public ImmutableConfigurationUnit(Parameter<Value> parameter, Value value) {
		if (parameter == null) {
			throw new NullPointerException("No parameter provided");
		} else {
			this.parameter = parameter;
			this.value = value;
		}
	}

	@Override
	public Parameter<Value> getParameter() {
		return parameter;
	}

	@Override
	public Value getValue() {
		return value;
	}

	/**
	 * Equivalent to <code>unit.getParameter().set(unit.getValue())</code>
	 */
	@Override
	public void apply() {
		parameter.set(value);
	}

	@Override
	public boolean isApplied() {
		Value current = parameter.get();
		return current == value || current != null && current.equals(value);
	}

}
