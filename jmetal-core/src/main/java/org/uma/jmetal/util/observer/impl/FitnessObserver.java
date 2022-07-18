package org.uma.jmetal.util.observer.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

/**
 * This observer prints the current best fitness of an algorithm. It requires a pair
 * (EVALUATIONS, int) and another pair (BEST_SOLUTION, solution) in the map used in the update() method.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FitnessObserver implements Observer<Map<String, Object>> {

  private Integer frequency ;
  /**
   * Constructor
   * value.
   */

  public FitnessObserver(Integer frequency) {
    this.frequency = frequency ;
  }

  public FitnessObserver() {
    this(1) ;
  }

  /**
   * This method gets the evaluation number from the dada map and prints it in the screen.
   * @param data Map of pairs (key, value)
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    Solution<?> solution = (Solution<?>)data.get("BEST_SOLUTION") ;
    Integer evaluations = (Integer)data.get("EVALUATIONS") ;

    if (solution!=null && evaluations != null) {
      if (evaluations % frequency == 0) {
          StringBuilder sb = new StringBuilder();
          for (double objective : solution.objectives()) {
              @NotNull String s = objective + " ";
              sb.append(s);
          }
          String objectiveValues = sb.toString();
          JMetalLogger.logger.info("Evaluations: " + evaluations + ". Fitness: " + objectiveValues);
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName()+
          ": The algorithm has not registered yet any info related to the EVALUATIONS and BEST_SOLUTION keys");
    }
  }

  public String getName() {
    return "Print objectives observer";
  }

  @Override
  public String toString() {
    return getName() ;
  }
}
