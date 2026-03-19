package org.uma.jmetal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

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
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0 ;
    }

    int[] internal = adaptiveGrid.getHypercubes();
    for (int i = 0; i < Math.min(internal.length, hypercubes.length); i++) {
      internal[i] = hypercubes[i];
    }
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
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0 ;
    }

    hypercubes[0] = 1 ;
    hypercubes[1] = 3 ;
    hypercubes[3] = 5 ;

    int[] internal2 = adaptiveGrid.getHypercubes();
    for (int i = 0; i < Math.min(internal2.length, hypercubes.length); i++) {
      internal2[i] = hypercubes[i];
    }
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
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0 ;
    }

    int[] internal3 = adaptiveGrid.getHypercubes();
    for (int i = 0; i < Math.min(internal3.length, hypercubes.length); i++) {
      internal3[i] = hypercubes[i];
    }

    assertEquals(0.0, adaptiveGrid.getAverageOccupation(), EPSILON) ;
  }

  @Test
  public void shouldGetAverageOccupationReturnTheRightValue() {
    AdaptiveGrid<DoubleSolution> adaptiveGrid ;

    int bisections = 5 ;
    int objectives = 2 ;
    adaptiveGrid = new AdaptiveGrid<>(bisections, objectives) ;

    int[] hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0 ;
    }

    hypercubes[0] = 1 ;
    hypercubes[1] = 3 ;
    hypercubes[3] = 5 ;

    int[] internal4 = adaptiveGrid.getHypercubes();
    for (int i = 0; i < Math.min(internal4.length, hypercubes.length); i++) {
      internal4[i] = hypercubes[i];
    }

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
    assertTrue(defaultUses[0] > 0, "No use of the default generator");

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		grid.rouletteWheel((a,b) -> {
			customUses[0]++;
			return new Random().nextDouble()*(b-a)+a;
		});
    assertTrue(defaultUses[0] == 0, "Default random generator used");
    assertTrue(customUses[0] > 0, "No use of the custom generator");
	}
	
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvidedInRandomOccupiedHypercube() {
		// Configuration
		AdaptiveGrid<Solution<?>> grid = new AdaptiveGrid<>(5, 2);
    int[] hypercubes = new int[1024];
    for (int i = 0; i < hypercubes.length; i++) {
      hypercubes[i] = 0;
    }
    hypercubes[0] = 1;
    hypercubes[1] = 3;
    hypercubes[3] = 5;

    int[] internal = grid.getHypercubes();
    for (int i = 0; i < Math.min(internal.length, hypercubes.length); i++) {
      internal[i] = hypercubes[i];
    }
    grid.calculateOccupied();

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

    grid.randomOccupiedHypercube();
    assertTrue(defaultUses[0] > 0, "No use of the default generator");

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		grid.randomOccupiedHypercube((a,b) -> {
			customUses[0]++;
			return new Random().nextInt(b+1-a)+a;
		});
    assertTrue(defaultUses[0] == 0, "Default random generator used");
    assertTrue(customUses[0] > 0, "No use of the custom generator");
	}
}