package org.uma.jmetal.util.naming.impl;

import org.junit.Test;
import org.uma.jmetal.util.naming.DescribedEntity;

import static org.junit.Assert.*;

public class DescribedEntitySetTest {

	@Test
	public void testAddingSameEntityModifiesNothing() {
		SimpleDescribedEntity entity = new SimpleDescribedEntity("Entity");

		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		assertTrue(set.add(entity));
		assertFalse(set.add(entity));
		assertFalse(set.add(entity));
		assertFalse(set.add(entity));
		assertEquals(1, set.size());
	}

	@Test
	public void testAddingDifferentEntityWithDifferentNameProperlyAdds() {
		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		assertTrue(set.add(new SimpleDescribedEntity("Entity 1")));
		assertTrue(set.add(new SimpleDescribedEntity("Entity 2")));
		assertTrue(set.add(new SimpleDescribedEntity("Entity 3")));
		assertTrue(set.add(new SimpleDescribedEntity("Entity 4")));
		assertEquals(4, set.size());
	}

	@Test
	public void testGetReturnsCorrectEntity() {
		SimpleDescribedEntity e1 = new SimpleDescribedEntity("Entity 1");
		SimpleDescribedEntity e2 = new SimpleDescribedEntity("Entity 2");
		SimpleDescribedEntity e3 = new SimpleDescribedEntity("Entity 3");
		SimpleDescribedEntity e4 = new SimpleDescribedEntity("Entity 4");

		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		set.add(e1);
		set.add(e2);
		set.add(e3);
		set.add(e4);

		assertEquals(e1, set.get("Entity 1"));
		assertEquals(e2, set.get("Entity 2"));
		assertEquals(e3, set.get("Entity 3"));
		assertEquals(e4, set.get("Entity 4"));
	}

	@Test
	public void testAddingDifferentEntityWithSameNameThrowsException() {
		String name = "Entity";
		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		set.add(new SimpleDescribedEntity(name));

		try {
			set.add(new SimpleDescribedEntity(name));
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testToStringInCaseInsensitiveOrder() {
		DescribedEntitySet<DescribedEntity> set = new DescribedEntitySet<>();
		set.add(new SimpleDescribedEntity("b"));
		set.add(new SimpleDescribedEntity("A"));
		set.add(new SimpleDescribedEntity("a"));
		set.add(new SimpleDescribedEntity("B"));

		assertEquals("[A, a, B, b]", set.toString());
	}
}
