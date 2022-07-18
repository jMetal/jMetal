package org.uma.jmetal.util.pseudorandom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import org.junit.Test;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator.Audit;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator.RandomMethod;

public class AuditableRandomGeneratorTest {

	@Test
	public void testAuditableRandomGeneratorProvidesCorrectAuditWhenGettingDouble() {
		var generator = new JavaRandomGenerator();
		var auditor = new AuditableRandomGenerator(generator);
		List<Audit> audits = new LinkedList<>();
		auditor.addListener(audits::add);
		
		auditor.nextDouble();
		assertEquals(1, audits.size());
		var audit = audits.get(0);
		assertEquals(RandomMethod.DOUBLE, audit.getMethod());
		assertFalse(audit.getBounds().isPresent());
		assertNotNull(audit.getResult());
	}

	@Test
	public void testAuditableRandomGeneratorProvidesCorrectAuditWhenGettingBoundedDouble() {
		var generator = new JavaRandomGenerator();
		var auditor = new AuditableRandomGenerator(generator);
		List<Audit> audits = new LinkedList<>();
		auditor.addListener(audits::add);
		
		auditor.nextDouble(10.0, 20.0);
		assertEquals(1, audits.size());
		var audit = audits.get(0);
		assertEquals(RandomMethod.BOUNDED_DOUBLE, audit.getMethod());
		assertTrue(audit.getBounds().isPresent());
		assertEquals(10.0, audit.getBounds().get().lower);
		assertEquals(20.0, audit.getBounds().get().upper);
		assertNotNull(audit.getResult());
	}

	@Test
	public void testAuditableRandomGeneratorProvidesCorrectAuditWhenGettingBoundedInteger() {
		var generator = new JavaRandomGenerator();
		var auditor = new AuditableRandomGenerator(generator);
		List<Audit> audits = new LinkedList<>();
		auditor.addListener(audits::add);
		
		auditor.nextInt(10, 20);
		assertEquals(1, audits.size());
		var audit = audits.get(0);
		assertEquals(RandomMethod.BOUNDED_INT, audit.getMethod());
		assertTrue(audit.getBounds().isPresent());
		assertEquals(10, audit.getBounds().get().lower);
		assertEquals(20, audit.getBounds().get().upper);
		assertNotNull(audit.getResult());
	}

}
