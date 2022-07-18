package org.uma.jmetal.operator.selection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.selection.impl.TournamentSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.EmptyCollectionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

/**
 * Created by ajnebro on 3/5/15.
 */
@Deprecated
public class TournamentSelectionTest {

  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(NullParameterException.class);

    var selection = new TournamentSelection<Solution<?>>(4) ;
    selection.execute(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(EmptyCollectionException.class);

    var selection = new TournamentSelection<DoubleSolution>(4) ;
    List<DoubleSolution> list = new ArrayList<>() ;

    selection.execute(list) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectValueToTheNumberOfTournaments() {
    var selection = new TournamentSelection<Solution<?>>(5) ;

    var result = (int) ReflectionTestUtils.getField(selection, "n_arity");
    var expectedResult = 5 ;
    assertEquals(expectedResult, result) ;
  }

  @Test
  public void shouldConstructorAssignTheCorrectValueSToTheNumberOfTournamentsAndTheComparator() {
    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class) ;
    var selection = new TournamentSelection<Solution<?>>(comparator, 7) ;

    var result = (int) ReflectionTestUtils.getField(selection, "n_arity");
    var comp = ReflectionTestUtils.getField(selection, "comparator");

    var expectedResult = 7 ;
    assertEquals(expectedResult, result) ;
    assertSame(comp, comparator) ;
  }

  @Test
  public void shouldExecuteReturnAnElementIfTheListHasOneElement() {
    List<Solution<?>> population = new ArrayList<>(1);

    @SuppressWarnings("unchecked")
    Comparator<Solution<?>> comparator = mock(Comparator.class) ;
    var selection = new TournamentSelection<Solution<?>>(comparator, 2) ;

    var solution = mock(BinarySolution.class) ;
    population.add(solution) ;

    assertSame(solution, selection.execute(population)) ;
  }

}
