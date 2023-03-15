package org.uma.jmetal.util.observer.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.knowm.xchart.XYChart;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.plot.FrontScatterPlot;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

/**

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class FrontPlotObserver<S extends Solution<?>> implements Observer<Map<String, Object>> {
  private final FrontScatterPlot chart;
  private Integer evaluations ;
  private final int plotUpdateFrequency ;

  private String plotTitle ;
  /**
   * Constructor
   */
  public FrontPlotObserver(String title, String xAxisTitle, String yAxisTitle, String legend,
  int plotUpdateFrequency) {
    chart = new FrontScatterPlot(title, xAxisTitle, yAxisTitle, legend) ;
    this.plotUpdateFrequency = plotUpdateFrequency ;
    this.plotTitle = title ;
  }

  public void setFront(double[][] front, String frontName) {
    chart.setFront(front, frontName);
  }

  public void setPoint(double x, double y, String pointName) {
    chart.addPoint(x, y, pointName);
  }

  /**
   * This method displays a front (population)
   * @param data Map of pairs (key, value)
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    evaluations = (Integer)data.get("EVALUATIONS") ;
    List<S> population = (List<S>) data.get("POPULATION");

    if (evaluations!=null && population!=null) {
      if (evaluations%plotUpdateFrequency == 0){
        List<Double> objective1 = population.stream().map(s -> s.objectives()[0]).collect(Collectors.toList()) ;
        List<Double> objective2 = population.stream().map(s -> s.objectives()[1]).collect(Collectors.toList()) ;
        chart.chartTitle(plotTitle + ". Evaluations: " + evaluations);
        chart.updateChart(objective1, objective2);
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName()+
        " : insufficient for generating real time information." +
        " Either EVALUATIONS or POPULATION keys have not been registered yet by the algorithm");
    }
  }

  public XYChart chart() {
    return chart.chart() ;
  }
}
