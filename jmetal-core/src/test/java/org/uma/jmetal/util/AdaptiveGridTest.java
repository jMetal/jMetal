package org.uma.jmetal.util;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import static org.junit.Assert.*;

import java.util.Random;

/**
 * Created by ajnebro on 16/3/17.
 */
public class AdaptiveGridTest {
  private static final double EPSILON = 0.00000001 ;

  @Test
  public void shouldConstructorCreateAValidInstance() {
    AdaptiveGrid<DoubleSolution> adaptiveGrid ;

    int bisections = 5 ;
    int objectives = 2 ;
    adaptiveGrid = new AdaptiveGrid<>(bisections, objectives) ;
    adaptiveGrid.calculateOccupied(); ;

    assertEquals(bisections, adaptiveGrid.getBisections()) ;
    assertEquals(0, adaptiveGrid.occupiedHypercubes());
    assertEquals((int)Math.pow(2.0, bisections * objectives), adaptiveGrid.getHypercubes().length) ;
  }

  @Test
  public void shouldOccupiedHypercubesReturnZeroIfThereAreNotOccupiedHypercubes() {
    AdaptiveGrid<DoubleSolution> adaptiveGrid ;

    int bisections = 5 ;
    int objectives = 2 ;
    adaptiveGrid = new AdaptiveGrid<>(bisections, objectives) ;

    int[] hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (int i: hypercubes) {
      hypercubes[i] = 0 ;
    }

    ReflectionTestUtils.setField(adaptiveGrid, "hypercubes", hypercubes);
    adaptiveGrid.calculateOccupied();

    assertEquals(0, adaptiveGrid.occupiedHypercubes()) ;
  }

  @Test
  public void shouldOccupiedHypercubesReturnTheNumberOfOccupiedHypercubes() {
    AdaptiveGrid<DoubleSolution> adaptiveGrid ;

    int bisections = 5 ;
    int objectives = 2 ;
    adaptiveGrid = new AdaptiveGrid<>(bisections, objectives) ;

    int[] hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (int i: hypercubes) {
      hypercubes[i] = 0 ;
    }

    hypercubes[0] = 1 ;
    hypercubes[1] = 3 ;
    hypercubes[3] = 5 ;

    ReflectionTestUtils.setField(adaptiveGrid, "hypercubes", hypercubes);
    adaptiveGrid.calculateOccupied();

    assertEquals(3, adaptiveGrid.occupiedHypercubes()) ;
  }

  @Test
  public void shouldGetAverageOccupationReturnZeroIfThereAreNoOccupiedHypercubes() {
    AdaptiveGrid<DoubleSolution> adaptiveGrid ;

    int bisections = 5 ;
    int objectives = 2 ;
    adaptiveGrid = new AdaptiveGrid<>(bisections, objectives) ;

    int[] hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (int i: hypercubes) {
      hypercubes[i] = 0 ;
    }

    ReflectionTestUtils.setField(adaptiveGrid, "hypercubes", hypercubes);

    assertEquals(0.0, adaptiveGrid.getAverageOccupation(), EPSILON) ;
  }

  @Test
  public void shouldGetAverageOccupationReturnTheRightValue() {
    AdaptiveGrid<DoubleSolution> adaptiveGrid ;

    int bisections = 5 ;
    int objectives = 2 ;
    adaptiveGrid = new AdaptiveGrid<>(bisections, objectives) ;

    int[] hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (int i: hypercubes) {
      hypercubes[i] = 0 ;
    }

    hypercubes[0] = 1 ;
    hypercubes[1] = 3 ;
    hypercubes[3] = 5 ;

    ReflectionTestUtils.setField(adaptiveGrid, "hypercubes", hypercubes);

    assertEquals(9.0/3.0, adaptiveGrid.getAverageOccupation(), EPSILON) ;
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvidedInRouletteWheel() {
		// Configuration
		AdaptiveGrid<Solution<?>> grid = new AdaptiveGrid<>(5, 2);

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		grid.rouletteWheel();
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		grid.rouletteWheel((a,b) -> {
			customUses[0]++;
			return new Random().nextDouble()*(b-a)+a;
		});
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
	
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvidedInRandomOccupiedHypercube() {
		// Configuration
		AdaptiveGrid<Solution<?>> grid = new AdaptiveGrid<>(5, 2);
		int[] hypercubes = new int[1024];
		for (int i : hypercubes) {
			hypercubes[i] = 0;
		}
		hypercubes[0] = 1;
		hypercubes[1] = 3;
		hypercubes[3] = 5;
		ReflectionTestUtils.setField(grid, "hypercubes", hypercubes);
		grid.calculateOccupied();

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		grid.randomOccupiedHypercube();
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		grid.randomOccupiedHypercube((a,b) -> {
			customUses[0]++;
			return new Random().nextInt(b+1-a)+a;
		});
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}