package org.uma.jmetal.parameter;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.util.naming.DescribedEntity;

/**
 * A {@link Parameter} aims at describing the {@link Value} of a specific
 * property, typically of an {@link Algorithm}, such that changing this
 * {@link Value} would affect its behavior. As such, the {@link Parameter}
 * allows to retrieve the current {@link Value} through {@link #get()} and
 * change it through {@link #set(Object)}. It is also identified through its
 * name ({@link #getName()}) and further detailed through its description (
 * {@link #getDescription()}).
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 * @param <Value>
 *            the type of value the {@link Parameter} can manage
 */
public interface Parameter<Value> extends DescribedEntity {
	/**
	 * 
	 * @param value
	 *            the {@link Value} to give to this {@link Parameter}
	 */
	public void set(Value value);

	/**
	 * 
	 * @return the current {@link Value} of the {@link Parameter}
	 */
	public Value get();
}
