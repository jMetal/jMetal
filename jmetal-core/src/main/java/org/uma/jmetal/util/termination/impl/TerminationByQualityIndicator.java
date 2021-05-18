package org.uma.jmetal.util.termination.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.termination.Termination;

import java.util.List;
import java.util.Map;

/**
 * Class that allows to check the termination condition when current front is above a given
 * percentage of the value of a quality indicator applied to a reference front
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TerminationByQualityIndicator implements Termination {
  private QualityIndicator qualityIndicator;
  private double[][] referenceFront;
  private double percentage;
  private double referenceFrontIndicatorValue;

  private double computedIndicatorValue ;

  public TerminationByQualityIndicator(
      QualityIndicator qualityIndicator, double[][] referenceFront, double percentage) {
    this.qualityIndicator = qualityIndicator;
    this.percentage = percentage;
    this.referenceFront = referenceFront;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    qualityIndicator.setReferenceFront(normalizedReferenceFront);
    referenceFrontIndicatorValue = qualityIndicator.compute(normalizedReferenceFront);
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    List<Solution<?>> population = (List<Solution<?>>) algorithmStatusData.get("POPULATION");
    Check.notNull(population);

    double[][] front = SolutionListUtils.getMatrixWithObjectiveValues(population);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    computedIndicatorValue = qualityIndicator.compute(normalizedFront);

    return computedIndicatorValue >= percentage * referenceFrontIndicatorValue;
  }

  public double getComputedIndicatorValue() {
    return computedIndicatorValue ;
  }
}
