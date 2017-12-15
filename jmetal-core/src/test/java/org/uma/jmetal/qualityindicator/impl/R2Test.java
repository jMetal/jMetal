package org.uma.jmetal.qualityindicator.impl;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class R2Test {

	String name = "R2";
	String description = "R2 quality indicator";
	
	@Test
	public void testR2HasProperNameAndDescriptionWithEmptyConstructor() {
		R2<List<? extends Solution<?>>> r2 = new R2<>();
		assertEquals(name, r2.getName());
		assertEquals(description, r2.getDescription());
	}

	@Test
	public void testR2HasProperNameAndDescriptionWithVectorConstructor() {
		int nVectors = 10;
		R2<List<? extends Solution<?>>> r2 = new R2<>(nVectors);
		assertEquals(name, r2.getName());
		assertEquals(description, r2.getDescription());
	}

	@Test
	public void testR2HasProperNameAndDescriptionWithFrontConstructor() {
		Front front = new ArrayFront();
		R2<List<? extends Solution<?>>> r2 = new R2<>(front);
		assertEquals(name, r2.getName());
		assertEquals(description, r2.getDescription());
	}

	@Test
	public void testR2HasProperNameAndDescriptionWithVectorFrontConstructor() {
		int nVectors = 10;
		Front front = new ArrayFront();
		R2<List<? extends Solution<?>>> r2 = new R2<>(nVectors, front);
		assertEquals(name, r2.getName());
		assertEquals(description, r2.getDescription());
	}

	@Test
	public void testR2HasProperNameAndDescriptionWithFileConstructor() throws IOException {
		String file = "src/test/resources/lambda/fake10x2Lambda.txt";
		R2<List<? extends Solution<?>>> r2 = new R2<>(file);
		assertEquals(name, r2.getName());
		assertEquals(description, r2.getDescription());
	}

	@Test
	public void testR2HasProperNameAndDescriptionWithFileFrontConstructor() throws IOException {
		String file = "src/test/resources/lambda/fake10x2Lambda.txt";
		Front front = new ArrayFront();
		R2<List<? extends Solution<?>>> r2 = new R2<>(file, front);
		assertEquals(name, r2.getName());
		assertEquals(description, r2.getDescription());
	}

}
