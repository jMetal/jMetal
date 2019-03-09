package org.uma.jmetal.auto.observer.impl;

import org.uma.jmetal.auto.observable.Observable;
import org.uma.jmetal.auto.observer.Observer;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;

import java.util.Map;

/**
 * This observer prints the current evaluation number of an algorithm. It requires a pair
 * (EVALUATIONS, int) in the map used in the update() method.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class PrintObjectivesObserver implements Observer<Map<String, Object>> {

  private Integer frequency ;
  /**
   * Constructor
   * value.
   */

  public PrintObjectivesObserver(Integer frequency) {
    this.frequency = frequency ;
  }

  public PrintObjectivesObserver() {
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
        String objectiveValues = "" ;
        for (Double objective: solution.getObjectives()) {
          objectiveValues += objective + " " ;
        }
        System.out.println("Evaluations: " + evaluations + ". Fitness: " + objectiveValues);
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName()+
          ": The algorithm has not registered yet any info related to the EVALUATIONS and POPULATIONS keys");
    }
  }

  @Override
  public String getName() {
    return "Print objectives observer";
  }

  @Override
  public String getDescription() {
    return "Observer of the objectives of the first solution in a population provided by an algorithm";
  }
}
