package org.uma.jmetal.operator.selection;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.test.util.ReflectionTestUtils;
import org.uma.jmetal.operator.selection.impl.RankingAndCrowdingSelection;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RankingAndCrowdingSelectionTest {
  @Rule
  public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsNull() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is null"));

    var selection = new RankingAndCrowdingSelection<Solution<?>>(4) ;
    selection.execute(null) ;
  }

  @Test
  public void shouldExecuteRaiseAnExceptionIfTheSolutionListIsEmpty() {
    exception.expect(JMetalException.class);
    exception.expectMessage(containsString("The solution list is empty"));

    var selection = new RankingAndCrowdingSelection<DoubleSolution>(4) ;
    List<DoubleSolution> list = new ArrayList<>() ;

    selection.execute(list) ;
  }

  @Test
  public void shouldDefaultConstructorReturnASingleSolution() {
    var selection = new RankingAndCrowdingSelection<Solution<?>>(1) ;

    var result = (int) ReflectionTestUtils.getField(selection, "solutionsToSelect");
    var expectedResult = 1 ;
    assertEquals(expectedResult, result) ;
  }

  @Test
  public void shouldNonDefaultConstructorReturnTheCorrectNumberOfSolutions() {
    var solutionsToSelect = 4 ;
    var selection = new RankingAndCrowdingSelection<Solution<?>>(solutionsToSelect) ;

    var result = (int)ReflectionTestUtils.getField(selection, "solutionsToSelect");
    assertEquals(solutionsToSelect, result) ;
  }
}
