package org.uma.jmetal.util.pseudorandom.impl;

import org.junit.Test;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator.Audit;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator.RandomMethod;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

public class AuditableRandomGeneratorTest {

	@Test
	public void testAuditableRandomGeneratorProvidesCorrectAuditWhenGettingDouble() {
		JavaRandomGenerator generator = new JavaRandomGenerator();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(generator);
		List<Audit> audits = new LinkedList<>();
		auditor.addListener((a) -> audits.add(a));
		
		auditor.nextDouble();
		assertEquals(1, audits.size());
		Audit audit = audits.get(0);
		assertEquals(RandomMethod.DOUBLE, audit.getMethod());
		assertFalse(audit.getBounds().isPresent());
		assertNotNull(audit.getResult());
	}

	@Test
	public void testAuditableRandomGeneratorProvidesCorrectAuditWhenGettingBoundedDouble() {
		JavaRandomGenerator generator = new JavaRandomGenerator();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(generator);
		List<Audit> audits = new LinkedList<>();
		auditor.addListener((a) -> audits.add(a));
		
		auditor.nextDouble(10.0, 20.0);
		assertEquals(1, audits.size());
		Audit audit = audits.get(0);
		assertEquals(RandomMethod.BOUNDED_DOUBLE, audit.getMethod());
		assertTrue(audit.getBounds().isPresent());
		assertEquals(10.0, audit.getBounds().get().lower);
		assertEquals(20.0, audit.getBounds().get().upper);
		assertNotNull(audit.getResult());
	}

	@Test
	public void testAuditableRandomGeneratorProvidesCorrectAuditWhenGettingBoundedInteger() {
		JavaRandomGenerator generator = new JavaRandomGenerator();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(generator);
		List<Audit> audits = new LinkedList<>();
		auditor.addListener((a) -> audits.add(a));
		
		auditor.nextInt(10, 20);
		assertEquals(1, audits.size());
		Audit audit = audits.get(0);
		assertEquals(RandomMethod.BOUNDED_INT, audit.getMethod());
		assertTrue(audit.getBounds().isPresent());
		assertEquals(10, audit.getBounds().get().lower);
		assertEquals(20, audit.getBounds().get().upper);
		assertNotNull(audit.getResult());
	}

}
