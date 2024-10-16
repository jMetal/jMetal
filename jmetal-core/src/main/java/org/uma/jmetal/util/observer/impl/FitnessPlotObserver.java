package org.uma.jmetal.util.observer.impl;

import java.util.Map;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.plot.SingleValueScatterPlot;

/**

 * @author Antonio J. Nebro
 */
public class FitnessPlotObserver<S extends Solution<?>> implements Observer<Map<String, Object>> {
  private final SingleValueScatterPlot chart;
  private Integer evaluations ;
  private final int plotUpdateFrequency ;
  private String valueName ;
  private String plotTitle ;

  /**
   * Constructor
   */
  public FitnessPlotObserver(String title, String xAxisTitle, String yAxisTitle, String valueName,
  int plotUpdateFrequency) {
    chart = new SingleValueScatterPlot(title, xAxisTitle, yAxisTitle, valueName) ;
    this.plotUpdateFrequency = plotUpdateFrequency ;
    this.valueName = valueName ;
    this.plotTitle = title ;
  }

  /**
   * This method displays a front (population)
   * @param data Map of pairs (key, value)
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    evaluations = (Integer)data.get("EVALUATIONS") ;
    Solution<?> solution = (Solution<?>)data.get("BEST_SOLUTION") ;

    if (evaluations!=null && solution!=null) {
      if (evaluations%plotUpdateFrequency == 0){
        chart.updateChart(evaluations, solution.objectives()[0]);
        String plotTitle = this.plotTitle + ". Current fitness: " +
            String.format("%.5e", solution.objectives()[0]) ;
        chart.chartTitle(plotTitle);
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName()+
        " : insufficient for generating real time information." +
        " Either EVALUATIONS or BEST_SOLUTION keys have not been registered yet by the algorithm");
    }
  }

  @Override
  public String toString() {
    return valueName ;
  }
}
