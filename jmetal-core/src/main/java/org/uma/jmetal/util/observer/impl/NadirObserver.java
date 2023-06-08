package org.uma.jmetal.util.observer.impl;

import java.util.List;
import java.util.Map;
import org.knowm.xchart.XYChart;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;
import org.uma.jmetal.util.plot.SingleValueScatterPlot;
import org.uma.jmetal.util.point.impl.NadirPoint;

/**
 * @author Antonio J. Nebro
 */
public class NadirObserver<S extends Solution<?>> implements
    Observer<Map<String, Object>> {

  private final SingleValueScatterPlot chart;
  private Integer evaluations;
  private final int plotUpdateFrequency;

  private NadirPoint nadirPoint = null;

  private String plotTitle;


  /**
   * Constructor
   */
  public NadirObserver(String title, int plotUpdateFrequency, long delay) {
    chart = new SingleValueScatterPlot(title, "Evaluations", "Nadir", "Nadir") ;

    this.plotUpdateFrequency = plotUpdateFrequency;
    this.plotTitle = title;

    chart.delay(delay);
  }


  /**
   * This method displays a front (population)
   *
   * @param data Map of pairs (key, value)
   */
  @Override
  public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
    evaluations = (Integer) data.get("EVALUATIONS");
    List<S> population = (List<S>) data.get("POPULATION");

    Check.notNull(population);

    if (evaluations != null) {
      if (evaluations % plotUpdateFrequency == 0) {
        var nonDominatedSolutionListArchive = new NonDominatedSolutionListArchive<S>() ;
        nonDominatedSolutionListArchive.addAll(population);

        nadirPoint = new NadirPoint(population.get(0).objectives().length);
        nadirPoint.update(nonDominatedSolutionListArchive.solutions());

        chart.chartTitle(plotTitle + ". Evaluations: " + evaluations);
        chart.updateChart(nadirPoint.value(0), nadirPoint.value(1));
      }
    } else {
      JMetalLogger.logger.warning(getClass().getName() +
          " : insufficient for generating real time information." +
          " Either EVALUATIONS or POPULATION keys have not been registered yet by the algorithm");
    }
  }

  public XYChart chart() {
    return chart.chart();
  }
}
