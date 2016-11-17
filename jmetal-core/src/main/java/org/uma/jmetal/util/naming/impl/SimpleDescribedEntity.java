package org.uma.jmetal.util.naming.impl;

import org.uma.jmetal.util.naming.DescribedEntity;

/**
 * {@link SimpleDescribedEntity} is a basic implementation of
 * {@link DescribedEntity}. It provides a basic support for the most generic
 * properties required by this interface.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 * 
 */
public class SimpleDescribedEntity implements DescribedEntity {

	/**
	 * The name of the {@link DescribedEntity}.
	 */
	private String name;
	/**
	 * The description of the {@link DescribedEntity}.
	 */
	private String description;

	/**
	 * Create a {@link SimpleDescribedEntity} with a given name and a given
	 * description.
	 * 
	 * @param name
	 *            the name of the {@link DescribedEntity}
	 * @param description
	 *            the description of the {@link DescribedEntity}
	 */
	public SimpleDescribedEntity(String name, String description) {
		this.name = name;
		this.description = description;
	}

	/**
	 * Create a {@link SimpleDescribedEntity} with a given name and a default
	 * description.
	 * 
	 * @param name
	 *            the name of the {@link DescribedEntity}
	 */
	public SimpleDescribedEntity(String name) {
		this(name, null);
		setDescription(DescribedEntity.super.getDescription());
	}

	/**
	 * Create a {@link SimpleDescribedEntity} with a default name and
	 * description.
	 */
	public SimpleDescribedEntity() {
		this(null, null);
		setName(DescribedEntity.super.getName());
		setDescription(DescribedEntity.super.getDescription());
	}

	/**
	 * 
	 * @param name
	 *            the new name of this {@link DescribedEntity}
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
	 *            the new description of this {@link DescribedEntity}
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
