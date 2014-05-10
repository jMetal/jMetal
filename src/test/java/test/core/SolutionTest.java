package test.core;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.problems.Kursawe;
import jmetal.util.JMException;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.assertFalse;

/**
 * Created by Antonio J. Nebro on 10/05/14.
 */
public class SolutionTest {

  @Test
  public void setDecisionVariablesTest() throws JMException, ClassNotFoundException {
    Problem problem = new Kursawe("Real", 3) ;
    Solution solution1 = new Solution(problem) ;
    Solution solution2 = new Solution(problem) ;

    assertFalse(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

    solution2.setDecisionVariables(solution1.getDecisionVariables()) ;
    //assertArrayEquals(solution1.getDecisionVariables(), solution2.getDecisionVariables()) ;
    assertTrue(Arrays.equals(solution1.getDecisionVariables(), solution2.getDecisionVariables())) ;

  }
}
