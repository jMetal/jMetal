package test.core;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.problems.Kursawe;
import jmetal.util.JMException;
import org.junit.Test;

/**
 * Created by Antonio J. Nebro on 10/05/14.
 */
public class SolutionTest {

  @Test
  public void setDecisionVariablesTest() throws JMException, ClassNotFoundException {
    Problem problem = new Kursawe("Real", 3) ;
    Solution solution1 = new Solution(problem) ;
    Solution solution2 = new Solution(problem) ;

    solution2.setDecisionVariables(solution1.getDecisionVariables()) ;


  }
}
