package org.uma.jmetal.util.naming.impl;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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

	class TestedClass extends SimpleDescribedEntity {
	}

	@Test
	public void testClassNameWhenNoName() {
		assertEquals(TestedClass.class.getSimpleName(),
				new TestedClass().getName());
	}

	@Test
	public void testNullDescriptionWhenNoDescription() {
		assertNull(new SimpleDescribedEntity().getDescription());
		assertNull(new SimpleDescribedEntity("name").getDescription());
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

}
