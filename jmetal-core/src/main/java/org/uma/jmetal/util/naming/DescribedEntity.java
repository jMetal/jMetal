package org.uma.jmetal.util.naming;

/**
 * A {@link DescribedEntity} is identified through its name ({@link #getName()})
 * and further detailed through its description ({@link #getDescription()}).
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public interface DescribedEntity {
	/**
	 * 
	 * @return the name of the {@link DescribedEntity}
	 */
	default String getName() {return toString();}

	/**
	 * 
	 * @return the description of the {@link DescribedEntity}
	 */
	default String getDescription() {return "<No description yet>";}
}
