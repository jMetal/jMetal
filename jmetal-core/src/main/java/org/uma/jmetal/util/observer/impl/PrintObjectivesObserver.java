package org.uma.jmetal.util.observer.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

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

  public String getName() {
    return "Print objectives observer";
  }

  @Override
  public String toString() {
    return getName() ;
  }
}
