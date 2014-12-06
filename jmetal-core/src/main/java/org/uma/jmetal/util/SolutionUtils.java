package org.uma.jmetal.util;

import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;

/**
 * Created by Antonio J. Nebro on 6/12/14.
 */
public class SolutionUtils {
  private static JMetalRandom randomGenerator = JMetalRandom.getInstance() ;

  /**
   * Return the best solution between those passed as arguments. If they are equal or incomparable
   * one of them is chosen randomly.
   * @param solution1
   * @param solution2
   * @return The best solution
   */
  public static Solution getBestSolution(Solution solution1, Solution solution2, Comparator comparator) {
    Solution result ;
    int flag = comparator.compare(solution1, solution2);
    if (flag == -1) {
      result = solution1;
    } else if (flag == 1) {
      result = solution2;
    } else {
      if (randomGenerator.nextDouble() < 0.5) {
        result = solution1;
      } else {
        result = solution2;
      }
    }

    return result ;
  }

}
