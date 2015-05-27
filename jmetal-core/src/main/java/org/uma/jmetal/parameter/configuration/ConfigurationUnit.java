package org.uma.jmetal.parameter.configuration;

import org.uma.jmetal.parameter.Parameter;

/**
 * A {@link ConfigurationUnit} identify a specific {@link Parameter} and a
 * specific {@link Value} which can be applied to this {@link Parameter}. When
 * {@link #apply()} is called, the {@link Parameter} is set to the {@link Value}
 * .
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of {@link Value} the {@link Parameter} supports
 */
public interface ConfigurationUnit<Value> {

	/**
	 * 
	 * @return the {@link Parameter} targeted by this {@link ConfigurationUnit}
	 */
	public Parameter<Value> getParameter();

	/**
	 * 
	 * @return the {@link Value} targeted by this {@link ConfigurationUnit}
	 */
	public Value getValue();

	/**
	 * Set the {@link Parameter} returned by {@link #getParameter()} to the
	 * {@link Value} returned by {@link #getValue()}.
	 * 
	 * @see #isApplied()
	 */
	public void apply();

	/**
	 * 
	 * @return <code>true</code> if the {@link Parameter} returned by
	 *         {@link #getParameter()} is set to the {@link Value} returned by
	 *         {@link #getValue()}, <code>false</code> otherwise
	 * @see #apply()
	 */
	public boolean isApplied();
}
