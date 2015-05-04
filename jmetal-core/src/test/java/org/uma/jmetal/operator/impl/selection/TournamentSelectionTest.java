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
  private TournamentSelection selection ;

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    selection = new TournamentSelection(4) ;
    selection.execute(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    selection = new TournamentSelection(4) ;
    List<DoubleSolution> list = new ArrayList<>() ;

    selection.execute(list) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectValueToTheNumberOfTournaments() {
    selection = new TournamentSelection(5) ;

    int result = (int) ReflectionTestUtils.getField(selection, "numberOfTournaments");
    int expectedResult = 5 ;
    assertEquals(expectedResult, result) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectValueSToTheNumberOfTournamentsAndTheComparator() {
    Comparator comparator = mock(Comparator.class) ;
    selection = new TournamentSelection(comparator, 7) ;

    int result = (int) ReflectionTestUtils.getField(selection, "numberOfTournaments");
    Comparator comp = (Comparator) ReflectionTestUtils.getField(selection, "comparator");

    int expectedResult = 7 ;
    assertEquals(expectedResult, result) ;
    assertSame(comp, comparator) ;
  }

  @Test
  public void shouldExecuteReturnAnElementIfTheListHasOneElement() {
    List<Solution> population = new ArrayList<>(1);

    Comparator comparator = mock(Comparator.class) ;
    selection = new TournamentSelection(comparator, 2) ;

    BinarySolution solution = mock(BinarySolution.class) ;
    population.add(solution) ;

    assertSame(solution, selection.execute(population)) ;
  }

}
