package org.uma.jmetal.operator.impl.selection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by ajnebro on 3/5/15.
 */
public class DifferentialEvolutionSelectionTest {
  private DifferentialEvolutionSelection selection ;
  private List<DoubleSolution> population ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Parameter is null"));

    selection = new DifferentialEvolutionSelection() ;

    selection.execute(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsHasOneSolution() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The population has less than four solutions: " + 1));

    selection = new DifferentialEvolutionSelection() ;
    selection.setIndex(0);

    population = Arrays.asList(mock(DoubleSolution.class)) ;

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheListOfSolutionsIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The population has less than four solutions: " + 0));

    selection = new DifferentialEvolutionSelection() ;
    selection.setIndex(0);

    population = Collections.emptyList();

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsNegative() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Index value invalid: " + -1));

    selection = new DifferentialEvolutionSelection() ;
    selection.setIndex(-1);

    population = Arrays.asList(mock(DoubleSolution.class), mock(DoubleSolution.class),
        mock(DoubleSolution.class)) ;

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsNotIndicated() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Index value invalid: " + Integer.MIN_VALUE));

    selection = new DifferentialEvolutionSelection() ;

    population = Arrays.asList(mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class)) ;

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheIndexIsHigherThanTheSolutionListLength() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("Index value invalid: " + 5));

    selection = new DifferentialEvolutionSelection() ;
    selection.setIndex(5);

    population = Arrays.asList(mock(DoubleSolution.class), mock(DoubleSolution.class), mock(DoubleSolution.class)) ;

    selection.execute(population) ;
  }

  @Test
  public void shouldExecuteReturnThreeDifferentSolutionsIfTheListHasFourElements() {
    selection = new DifferentialEvolutionSelection() ;
    selection.setIndex(1);

    population = Arrays.asList(mock(DoubleSolution.class), mock(DoubleSolution.class),
        mock(DoubleSolution.class), mock(DoubleSolution.class)) ;

    List<DoubleSolution> parents = selection.execute(population) ;
    assertEquals(3, parents.size()) ;

    // The index solution must not be in the result
    assertNotSame(population.get(1), parents.get(0));
    assertNotSame(population.get(1), parents.get(1));
    assertNotSame(population.get(1), parents.get(2));

    assertThat(parents, hasItem(population.get(0))) ;
    assertThat(parents, hasItem(population.get(2))) ;
    assertThat(parents, hasItem(population.get(3))) ;
    assertThat(parents, not(hasItem(population.get(1)))) ;
  }
}


