package org.uma.jmetal.util.naming.impl;

import org.junit.Test;
import org.uma.jmetal.util.naming.DescribedEntity;

import static org.junit.Assert.*;

public class SimpleDescribedEntityTest {

	@Test
	public void testSetGetName() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setName("test");
		assertEquals("test", entity.getName());

		entity.setName("abc");
		assertEquals("abc", entity.getName());
	}

	@Test
	public void testSetGetDescription() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity();

		entity.setDescription("test");
		assertEquals("test", entity.getDescription());

		entity.setDescription("abc");
		assertEquals("abc", entity.getDescription());
	}

	@Test
	public void testCorrectNameWhenProvided() {
		String name = "named measure";
		assertEquals(name, new SimpleDescribedEntity(name).getName());
		assertEquals(name,
				new SimpleDescribedEntity(name, "description").getName());
	}

	@Test
	public void testCorrectDescriptionWhenProvided() {
		String description = "My measure description is awesome!";
		assertEquals(description, new SimpleDescribedEntity("measure",
				description).getDescription());
	}

	@Test
	public void testDefaultDescriptionUsedForNamleOnlyConstructor() {
		String expected = new DescribedEntity() {}.getDescription();
		String actual = new SimpleDescribedEntity("Test").getDescription();
		assertEquals(expected, actual);
	}

	@Test
	public void testDefaultDescriptionUsedForEmptyConstructor() {
		String expected = new DescribedEntity() {}.getDescription();
		String actual = new SimpleDescribedEntity().getDescription();
		assertEquals(expected, actual);
	}

	@Test
	public void testDefaultNameUsedForEmptyConstructor() {
		/*
		 * This test is simplified one. If someone find out how to get exactly
		 * the expected value without copy-pasting the default code (which can
		 * evolve), please consider improving this test.
		 */
		String name = new SimpleDescribedEntity().getName();
		assertNotNull(name);
		assertFalse(name.isEmpty());
	}
}
