package org.uma.jmetal.util.naming;

/**
 * A {@link DescribedEntity} is identified through its name ({@link #name()})
 * and further detailed through its description ({@link #description()}).
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface DescribedEntity {
	/**
	 * 
	 * @return the name of the {@link DescribedEntity}
	 */
	public String name();

	/**
	 * 
	 * @return the description of the {@link DescribedEntity}
	 */
	public String description();
}
