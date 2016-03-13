package org.uma.jmetal.operator.impl.selection;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

/**
 * Created by ajnebro on 3/5/15.
 */
public class TournamentSelectionTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    TournamentSelection<Solution<?>> selection = new TournamentSelection<Solution<?>>(4) ;
    selection.execute(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    TournamentSelection<DoubleSolution> selection = new TournamentSelection<DoubleSolution>(4) ;
    List<DoubleSolution> list = new ArrayList<>() ;

    selection.execute(list) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectValueToTheNumberOfTournaments() {
    TournamentSelection<Solution<?>> selection = new TournamentSelection<Solution<?>>(5) ;

    int result = (int) ReflectionTestUtils.getField(selection, "numberOfTournaments");
    int expectedResult = 5 ;
    assertEquals(expectedResult, result) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectValueSToTheNumberOfTournamentsAndTheComparator() {
    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class) ;
    TournamentSelection<Solution<?>> selection = new TournamentSelection<Solution<?>>(comparator, 7) ;

    int result = (int) ReflectionTestUtils.getField(selection, "numberOfTournaments");
    Object comp = ReflectionTestUtils.getField(selection, "comparator");

    int expectedResult = 7 ;
    assertEquals(expectedResult, result) ;
    assertSame(comp, comparator) ;
  }

  @Test
  public void shouldExecuteReturnAnElementIfTheListHasOneElement() {
    List<Solution<?>> population = new ArrayList<>(1);

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class) ;
    TournamentSelection<Solution<?>> selection = new TournamentSelection<Solution<?>>(comparator, 2) ;

    BinarySolution solution = mock(BinarySolution.class) ;
    population.add(solution) ;

    assertSame(solution, selection.execute(population)) ;
  }

}
