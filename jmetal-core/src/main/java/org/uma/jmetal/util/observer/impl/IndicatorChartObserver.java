package org.uma.jmetal.util.observer.impl;

import static org.uma.jmetal.util.SolutionListUtils.getMatrixWithObjectiveValues;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.knowm.xchart.XYChart;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.plot.SingleValueChart;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

/**

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class IndicatorChartObserver<S extends Solution<?>> implements Observer<Map<String, Object>> {
  private final SingleValueChart chart;
  private Integer evaluations ;
  private final int plotUpdateFrequency ;
  double[][] referenceFront ;
  double[][] normalizedReferenceFront ;
  private QualityIndicator qualityIndicator ;
  /**
   * Constructor
   */
  public IndicatorChartObserver(String title, QualityIndicator qualityIndicator,
      String referenceFrontFileName, int plotUpdateFrequency) throws IOException {
    chart = new SingleValueChart(title, "Evaluations", qualityIndicator.name(), qualityIndicator.name()) ;
    chart.delay(200) ;
    this.plotUpdateFrequency = plotUpdateFrequency ;
    this.qualityIndicator = qualityIndicator ;

    referenceFront = VectorUtils.readVectors(referenceFrontFileName, ",");
    normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    qualityIndicator.referenceFront(normalizedReferenceFront);
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
        double[][] normalizedFront =
            NormalizeUtils.normalize(
                getMatrixWithObjectiveValues(population),
                NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

        chart.updateChart(evaluations, qualityIndicator.compute(normalizedFront));
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
