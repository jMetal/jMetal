package org.uma.jmetal.util;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ReflectionUtils;
import org.uma.jmetal.solution.DoubleSolution;

import static org.junit.Assert.*;

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
}