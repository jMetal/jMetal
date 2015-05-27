package org.uma.jmetal.parameter.configuration;

import java.util.Collection;

import org.uma.jmetal.parameter.Parameter;

/**
 * A {@link Configuration} corresponds to a set of {@link ConfigurationUnit}s,
 * building a mapping between a set of {@link Parameter}s and some values that
 * we can apply to them. The different {@link Parameter}s covered by this
 * {@link Configuration} can be retrieved through {@link #getParameters()} and
 * the complete {@link ConfigurationUnit} associated to each {@link Parameter}
 * can be retrieved through {@link #getUnit(Parameter)}.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface Configuration extends Iterable<ConfigurationUnit<?>> {

	/**
	 * 
	 * @return all the {@link Parameter}s covered by this {@link Configuration}
	 */
	public Collection<Parameter<?>> getParameters();

	/**
	 * 
	 * @param parameter
	 *            the {@link Parameter} to consider
	 * @return the {@link ConfigurationUnit} which associates this
	 *         {@link Parameter} with its {@link Value} in this
	 *         {@link Configuration}
	 */
	public <Value> ConfigurationUnit<Value> getUnit(Parameter<Value> parameter);

	/**
	 * Set all the {@link Parameter}s of this {@link Configuration} to the
	 * values they are associated with.
	 * 
	 * @see #isAllApplied()
	 */
	public void applyAll();

	/**
	 * 
	 * @return <code>true</code> if all the {@link ConfigurationUnit}s are
	 *         applied, <code>false</code> otherwise
	 * @see #applyAll()
	 */
	public boolean isAllApplied();

}
