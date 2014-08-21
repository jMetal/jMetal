package org.uma.test.operator.selection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.core.Problem;
import org.uma.jmetal.core.Solution;
import org.uma.jmetal.core.SolutionSet;
import org.uma.jmetal.operator.selection.BestSolutionSelection;
import org.uma.jmetal.problem.Kursawe;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.ObjectiveComparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class BestSolutionSelectionTest {
  static final int POPULATION_SIZE = 10 ;
  SolutionSet solutionSet ;
  BestSolutionSelection	selection;
  Problem problem ;

  @Before
  public void setUp() throws Exception {
    selection = new BestSolutionSelection.Builder(new ObjectiveComparator(0))
            .build() ;

    problem = new Kursawe("Real") ;
    solutionSet = new SolutionSet(POPULATION_SIZE) ;
    for (int i = 0 ; i < POPULATION_SIZE; i++) {
      Solution solution = new Solution(problem) ;
      solution.setObjective(0, i);
      solutionSet.add(solution) ;
    }
  }

  @Test
  public void executeWithCorrectParametersTest() {
    assertNotNull(selection.execute(solutionSet));
    assertEquals(0, selection.execute(solutionSet));
  }

  @Test (expected = JMetalException.class)
  public void executeWithPopulationSizeZeroTest() {
    solutionSet = new SolutionSet(0) ;
    selection.execute(solutionSet) ;
  }

  @Test (expected = JMetalException.class)
  public void wrongParameterClassTest() {
    selection.execute(new Integer(4)) ;
  }

  @Test (expected = JMetalException.class)
  public void nullParameterTest() {
    selection.execute(null) ;
  }

  @After
  public void tearDown() throws Exception {
    solutionSet = null ;
    selection = null ;
  }
}
