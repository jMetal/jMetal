package org.uma.jmetal.util.observer.impl;

import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

import java.util.Map;

/**
 * This observer prints the current evaluation number of an algorithm. It expects a pair
 * (EVALUATIONS, int) in the map used in the update() method.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class EvaluationObserver implements Observer<Map<String, Object>> {

  private Integer frequency ;

  /**
   * Constructor
   * @param frequency Print frequency in term of times the update method has been invoked
   */
  public EvaluationObserver(Integer frequency) {
    this.frequency = frequency ;
  }

  public EvaluationObserver() {
    this(1) ;
  }

  /**
   * This method gets the evaluation number from the dada map and prints it in the screen.
   * @param data Map of pairs (key, value)
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    Integer evaluations = (Integer)data.get("EVALUATIONS") ;

    if (evaluations!=null) {
      if (evaluations % frequency == 0) {
        System.out.println("Evaluations: " + evaluations);
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName()+
          ": The algorithm has not registered yet any info related to the EVALUATIONS key");
    }
  }

  public String getName() {
    return "Evaluation observer";
  }

  @Override
  public String toString() {
    return getName() ;
  }
}
