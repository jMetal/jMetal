package org.uma.jmetal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
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

      var bisections = 5 ;
      var objectives = 2 ;
      var adaptiveGrid = new AdaptiveGrid<DoubleSolution>(bisections, objectives);
    adaptiveGrid.calculateOccupied(); ;

    assertEquals(bisections, adaptiveGrid.getBisections()) ;
    assertEquals(0, adaptiveGrid.occupiedHypercubes());
    assertEquals((int)Math.pow(2.0, bisections * objectives), adaptiveGrid.getHypercubes().length) ;
  }

  @Test
  public void shouldOccupiedHypercubesReturnZeroIfThereAreNotOccupiedHypercubes() {

      var bisections = 5 ;
      var objectives = 2 ;
      var adaptiveGrid = new AdaptiveGrid<DoubleSolution>(bisections, objectives);

      var hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (var i: hypercubes) {
      hypercubes[i] = 0 ;
    }

    ReflectionTestUtils.setField(adaptiveGrid, "hypercubes", hypercubes);
    adaptiveGrid.calculateOccupied();

    assertEquals(0, adaptiveGrid.occupiedHypercubes()) ;
  }

  @Test
  public void shouldOccupiedHypercubesReturnTheNumberOfOccupiedHypercubes() {

      var bisections = 5 ;
      var objectives = 2 ;
      var adaptiveGrid = new AdaptiveGrid<DoubleSolution>(bisections, objectives);

      var hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (var i: hypercubes) {
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

      var bisections = 5 ;
      var objectives = 2 ;
      var adaptiveGrid = new AdaptiveGrid<DoubleSolution>(bisections, objectives);

      var hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (var i: hypercubes) {
      hypercubes[i] = 0 ;
    }

    ReflectionTestUtils.setField(adaptiveGrid, "hypercubes", hypercubes);

    assertEquals(0.0, adaptiveGrid.getAverageOccupation(), EPSILON) ;
  }

  @Test
  public void shouldGetAverageOccupationReturnTheRightValue() {

      var bisections = 5 ;
      var objectives = 2 ;
      var adaptiveGrid = new AdaptiveGrid<DoubleSolution>(bisections, objectives);

      var hypercubes = new int[(int)Math.pow(2.0, bisections * objectives)] ;
    for (var i: hypercubes) {
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
        var grid = new AdaptiveGrid<Solution<?>>(5, 2);

		// Check configuration leads to use default generator by default
		final var defaultUses = new int[]{0};
        var defaultGenerator = JMetalRandom.getInstance();
        var auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		grid.rouletteWheel();
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final var customUses = new int[]{0};
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
        var grid = new AdaptiveGrid<Solution<?>>(5, 2);
        var hypercubes = new int[1024];
		for (var i : hypercubes) {
			hypercubes[i] = 0;
		}
		hypercubes[0] = 1;
		hypercubes[1] = 3;
		hypercubes[3] = 5;
		ReflectionTestUtils.setField(grid, "hypercubes", hypercubes);
		grid.calculateOccupied();

		// Check configuration leads to use default generator by default
		final var defaultUses = new int[]{0};
        var defaultGenerator = JMetalRandom.getInstance();
        var auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		grid.randomOccupiedHypercube();
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final var customUses = new int[]{0};
		grid.randomOccupiedHypercube((a,b) -> {
			customUses[0]++;
			return new Random().nextInt(b+1-a)+a;
		});
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}