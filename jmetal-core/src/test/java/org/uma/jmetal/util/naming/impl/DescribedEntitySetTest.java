package org.uma.jmetal.util.naming.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.uma.jmetal.util.naming.DescribedEntity;

public class DescribedEntitySetTest {

	@Test
	public void testAddingSameEntityModifiesNothing() {
		var entity = new SimpleDescribedEntity("Entity");

		var set = new DescribedEntitySet<DescribedEntity>();
		assertTrue(set.add(entity));
		assertFalse(set.add(entity));
		assertFalse(set.add(entity));
		assertFalse(set.add(entity));
		assertEquals(1, set.size());
	}

	@Test
	public void testAddingDifferentEntityWithDifferentNameProperlyAdds() {
		var set = new DescribedEntitySet<DescribedEntity>();
		assertTrue(set.add(new SimpleDescribedEntity("Entity 1")));
		assertTrue(set.add(new SimpleDescribedEntity("Entity 2")));
		assertTrue(set.add(new SimpleDescribedEntity("Entity 3")));
		assertTrue(set.add(new SimpleDescribedEntity("Entity 4")));
		assertEquals(4, set.size());
	}

	@Test
	public void testGetReturnsCorrectEntity() {
		var e1 = new SimpleDescribedEntity("Entity 1");
		var e2 = new SimpleDescribedEntity("Entity 2");
		var e3 = new SimpleDescribedEntity("Entity 3");
		var e4 = new SimpleDescribedEntity("Entity 4");

		var set = new DescribedEntitySet<DescribedEntity>();
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
		var name = "Entity";
		var set = new DescribedEntitySet<DescribedEntity>();
		set.add(new SimpleDescribedEntity(name));

		try {
			set.add(new SimpleDescribedEntity(name));
			fail("No exception thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void testToStringInCaseInsensitiveOrder() {
		var set = new DescribedEntitySet<DescribedEntity>();
		set.add(new SimpleDescribedEntity("b"));
		set.add(new SimpleDescribedEntity("A"));
		set.add(new SimpleDescribedEntity("a"));
		set.add(new SimpleDescribedEntity("B"));

		assertEquals("[A, a, B, b]", set.toString());
	}
}
