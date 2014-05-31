package test.qualityIndicator;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.SolutionSet;
import jmetal.problems.Kursawe;
import jmetal.qualityIndicator.SetCoverage;
import jmetal.util.JMException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Antonio J. Nebro on 31/05/14.
 */
public class SetCoverageTest {
  public static final double EPSILON = 0.0000000000001 ;
  SolutionSet solutionSet1_ ;
  SolutionSet solutionSet2_ ;
  Problem problem_ ;

  @Before
  public void startup() throws JMException {
    problem_ = new Kursawe("Real", 3) ;
    solutionSet1_ = new SolutionSet(10) ;
    solutionSet2_ = new SolutionSet(10) ;
  }

  @Test
  public void emptySetsTest() {
    assertEquals(0.0, new SetCoverage().setCoverage(solutionSet2_, solutionSet1_), EPSILON) ;
  }


  @Test
  public void coverageTest() throws ClassNotFoundException {
    Solution solution ;
    for (int i = 0 ; i< 4; i++) {
      solution = new Solution(problem_);
      for (int j = 0; j < problem_.getNumberOfObjectives(); j++) {
        solution.setObjective(j, j);
      }
      solutionSet1_.add(solution);
    }

    for (int i = 0 ; i< 4; i++) {
      solution = new Solution(problem_);
      for (int j = 0; j < problem_.getNumberOfObjectives(); j++) {
        solution.setObjective(j, j * 2);
      }
      solutionSet2_.add(solution);
    }

    assertEquals(1.0, new SetCoverage().setCoverage(solutionSet1_, solutionSet2_), EPSILON) ;
    assertEquals(0.0, new SetCoverage().setCoverage(solutionSet2_, solutionSet1_), EPSILON) ;
  }


  @After
  public void teardown() throws JMException {
    problem_ = null ;
    solutionSet1_ = null ;
    solutionSet2_ = null ;
  }

}
