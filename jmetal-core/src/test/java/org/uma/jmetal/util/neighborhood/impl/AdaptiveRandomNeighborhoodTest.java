package org.uma.jmetal.util.neighborhood.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.integersolution.IntegerSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

/**
 * @author Antonio J. Nebro
 */
public class AdaptiveRandomNeighborhoodTest {

  @Test
  public void shouldConstructorThrowAnExceptionWhenTheNumberOfNeighboursIsNegative() {
    JMetalException e = assertThrows(JMetalException.class,
        () -> new AdaptiveRandomNeighborhood<DoubleSolution>(4, -1));
    assertTrue(e.getMessage().contains("The number of neighbors is negative: -1"));
  }

  @Test
  public void shouldConstructorThrowAnExceptionWhenTheNumberOfNeighboursIsEqualThanTheListSize() {
    JMetalException e = assertThrows(JMetalException.class,
        () -> new AdaptiveRandomNeighborhood<IntegerSolution>(4, 4));
    assertTrue(e.getMessage().contains("less or equal to the number of requested neighbors: 4"));
  }

  @Test
  public void shouldConstructorCreateAnInstanceIfTheParamtersAreValid() {
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(6, 4) ;

    assertNotNull(neighborhood) ;
  }

  @Test
  public void shouldConstructorThrowAnExceptionWhenTheNumberOfNeighboursIsGreaterThanTheListSize() {
    JMetalException e = assertThrows(JMetalException.class,
        () -> new AdaptiveRandomNeighborhood<IntegerSolution>(4, 6));
    assertTrue(e.getMessage().contains("less or equal to the number of requested neighbors: 6"));
  }

  @Test
  public void shouldGetNeighborsWithANullListOfSolutionsThrowAnException() {
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(4, 2)  ;
    JMetalException e = assertThrows(JMetalException.class, () -> neighborhood.getNeighbors(null, 1));
    assertTrue(e.getMessage().contains("The solution list is null"));
  }

  @Test
  public void shouldGetNeighborsWithANegativeSolutionIndexThrowAnException() {
    int solutionListSize = 4 ;
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, 2) ;

    JMetalException e = assertThrows(JMetalException.class,
        () -> neighborhood.getNeighbors(new ArrayList<>(), -1));
    assertTrue(e.getMessage().contains("The solution position value is negative: -1"));

    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0; i < solutionListSize; i++) {
      list.add(mock(IntegerSolution.class));
    }

    neighborhood.getNeighbors(list, -1) ;
  }

  @Test
  public void shouldGetNeighborsWithATooBigSolutionIndexThrowAnException() {
    int solutionListSize = 4 ;
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, 2) ;

    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0; i < solutionListSize; i++) {
      list.add(mock(IntegerSolution.class));
    }
    JMetalException e = assertThrows(JMetalException.class, () -> neighborhood.getNeighbors(list, 6));
    assertTrue(e.getMessage().contains("is equal or greater than the solution list size"));
  }

  @Test
  public void shouldGetNeighborsThrowAnExceptionIfTheListSizeIsNotCorrect() {
    int solutionListSize = 4 ;
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, 2) ;
    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0; i < solutionListSize - 1; i++) {
      list.add(mock(IntegerSolution.class));
    }
    JMetalException e = assertThrows(JMetalException.class, () -> neighborhood.getNeighbors(list, 1));
    assertTrue(e.getMessage().contains("The solution list size: 3"));
  }

  /**
   * Case 1
   *
   * Solution list size: 4
   * Number of neighbors: 2
   *
   */
  @Test
  public void shouldGetNeighborsReturnTwoNeighborsPlusTheCurrentSolution() {
    int solutionListSize = 4 ;
    int numberOfNeighbors = 2 ;
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, numberOfNeighbors) ;

    List<IntegerSolution> list = new ArrayList<>(solutionListSize) ;
    for (int i = 0 ; i < solutionListSize; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    List<IntegerSolution> result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(numberOfNeighbors + 1, result.size()) ;
    assertThat(result, hasItem(list.get(0))) ;
  }

  /**
   * Case 1
   *
   * Solution list size: 3
   * Number of neighbors: 1
   * Neighbors:
   *  - solution 0: 0, 2
   *  - solution 1: 1, 0
   *  - solution 2: 2, 0
   *
   */
  @Test
  public void shouldGetNeighborsReturnThreeNeighborsPlusTheCurrentSolution() {
    int solutionListSize = 3 ;
    int numberOfNeighbors = 1 ;
        @SuppressWarnings("unchecked")
        BoundedRandomGenerator<Integer> randomGenerator = mock(BoundedRandomGenerator.class) ;
        when(randomGenerator.getRandomValue(0, solutionListSize-1)).thenReturn(2, 0, 0) ;

        AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
          new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, numberOfNeighbors, randomGenerator) ;

    List<IntegerSolution> list = new ArrayList<>(solutionListSize) ;
    for (int i = 0 ; i < solutionListSize; i++) {
      list.add(mock(IntegerSolution.class)) ;
    }

    neighborhood.recompute();

    List<IntegerSolution> result ;
    result = neighborhood.getNeighbors(list, 0) ;
    assertEquals(numberOfNeighbors + 1, result.size()) ;
    assertEquals(list.get(0), result.get(0));
    assertEquals(list.get(2), result.get(1));

    result = neighborhood.getNeighbors(list, 1) ;
    assertEquals(numberOfNeighbors + 1, result.size()) ;
    assertEquals(list.get(1), result.get(0));
    assertEquals(list.get(0), result.get(1));

    result = neighborhood.getNeighbors(list, 2) ;
    assertEquals(numberOfNeighbors + 1, result.size()) ;
    assertEquals(list.get(2), result.get(0));
    assertEquals(list.get(0), result.get(1));
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		int solutionListSize = 3;
		int numberOfRandomNeighbours = 1;

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new AdaptiveRandomNeighborhood<>(solutionListSize, numberOfRandomNeighbours);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new AdaptiveRandomNeighborhood<>(solutionListSize, numberOfRandomNeighbours, (a, b) -> {
			customUses[0]++;
			return new Random().nextInt(b-a+1)+a;
		});
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}