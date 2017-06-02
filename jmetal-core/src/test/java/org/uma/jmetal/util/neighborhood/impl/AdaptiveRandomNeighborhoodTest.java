package org.uma.jmetal.util.neighborhood.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.IntegerSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class AdaptiveRandomNeighborhoodTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldConstructorThrowAnExceptionWhenTheNumberOfNeighboursIsNegative() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The number of neighbors is negative: -1"));

    new AdaptiveRandomNeighborhood<DoubleSolution>(4, -1) ;
  }

  @Test
  public void shouldConstructorThrowAnExceptionWhenTheNumberOfNeighboursIsEqualThanTheListSize() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The population size: 4 is " +
            "less or equal to the number of requested neighbors: 4"));

    new AdaptiveRandomNeighborhood<IntegerSolution>(4, 4) ;
  }

  @Test
  public void shouldConstructorCreateAnInstanceIfTheParamtersAreValid() {
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(6, 4) ;

    assertNotNull(neighborhood) ;
  }

  @Test
  public void shouldConstructorThrowAnExceptionWhenTheNumberOfNeighboursIsGreaterThanTheListSize() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The population size: 4 is " +
            "less or equal to the number of requested neighbors: 6"));

    new AdaptiveRandomNeighborhood<IntegerSolution>(4, 6) ;
  }

  @Test
  public void shouldGetNeighborsWithANullListOfSolutionsThrowAnException() {
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(4, 2)  ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    neighborhood.getNeighbors(null, 1) ;
  }

  @Test
  public void shouldGetNeighborsWithANegativeSolutionIndexThrowAnException() {
    int solutionListSize = 4 ;
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, 2) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution position value is negative: -1"));

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

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution position value: 6 " +
            "is equal or greater than the solution list size: " + solutionListSize));

    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0; i < solutionListSize; i++) {
      list.add(mock(IntegerSolution.class));
    }

    neighborhood.getNeighbors(list, 6) ;
  }

  @Test
  public void shouldGetNeighborsThrowAnExceptionIfTheListSizeIsNotCorrect() {
    int solutionListSize = 4 ;
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, 2) ;

    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list size: 3 " +
            "is different to the value: " + solutionListSize));

    List<IntegerSolution> list = new ArrayList<>() ;
    for (int i = 0; i < solutionListSize - 1; i++) {
      list.add(mock(IntegerSolution.class));
    }

    neighborhood.getNeighbors(list, 1) ;
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
    AdaptiveRandomNeighborhood<IntegerSolution> neighborhood =
            new AdaptiveRandomNeighborhood<IntegerSolution>(solutionListSize, numberOfNeighbors) ;

    @SuppressWarnings("unchecked")
	BoundedRandomGenerator<Integer> randomGenerator = mock(BoundedRandomGenerator.class) ;
    when(randomGenerator.getRandomValue(0, solutionListSize-1)).thenReturn(2, 0, 0) ;

    ReflectionTestUtils.setField(neighborhood, "randomGenerator", randomGenerator);

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