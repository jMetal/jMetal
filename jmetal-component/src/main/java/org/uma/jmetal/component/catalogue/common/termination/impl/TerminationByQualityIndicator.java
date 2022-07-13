package org.uma.jmetal.component.catalogue.common.termination.impl;

import java.util.List;
import java.util.Map;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Class that allows to check the termination condition when current front is above a given
 * percentage of the value of a quality indicator applied to a reference front. An evaluations limit
 * is used to avoid an infinite loop if the value is never gotten.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class TerminationByQualityIndicator implements Termination {

  private final QualityIndicator qualityIndicator;
  private final double[][] referenceFront;
  private final double percentage;
  private final double referenceFrontIndicatorValue;
  private final int evaluationsLimit;
  private int evaluations;
  private boolean evaluationsLimitReached;
  private double computedIndicatorValue;

  public TerminationByQualityIndicator(
      QualityIndicator qualityIndicator, double[][] referenceFront, double percentage,
      int evaluationsLimit) {
    Check.notNull(qualityIndicator);
    Check.notNull(referenceFront);
    Check.valueIsNotNegative(percentage);
    Check.valueIsNotNegative(evaluationsLimit);
    Check.that(referenceFront.length > 1,
        "The reference front must have at least two points instead of " + referenceFront.length);

    this.qualityIndicator = qualityIndicator;
    this.percentage = percentage;
    this.referenceFront = referenceFront;
    this.evaluationsLimit = evaluationsLimit;
    evaluationsLimitReached = false;

    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    qualityIndicator.setReferenceFront(normalizedReferenceFront);
    referenceFrontIndicatorValue = qualityIndicator.compute(normalizedReferenceFront);
  }

  @Override
  public boolean isMet(Map<String, Object> algorithmStatusData) {
    Check.notNull(algorithmStatusData.get("POPULATION"));
    Check.notNull(algorithmStatusData.get("EVALUATIONS"));

    List<Solution<?>> population = (List<Solution<?>>) algorithmStatusData.get("POPULATION");
    evaluations = (int) algorithmStatusData.get("EVALUATIONS");
    Check.notNull(population);
    Check.collectionIsNotEmpty(population);

    double[][] front = SolutionListUtils.getMatrixWithObjectiveValues(population);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    computedIndicatorValue = qualityIndicator.compute(normalizedFront);

    boolean unsuccessfulStopCondition = evaluationsLimit <= evaluations;
    boolean successfulStopCondition =
        computedIndicatorValue >= percentage * referenceFrontIndicatorValue;

    if (unsuccessfulStopCondition) {
      evaluationsLimitReached = true;
    }

    return (successfulStopCondition || unsuccessfulStopCondition);
  }

  public double getComputedIndicatorValue() {
    return computedIndicatorValue;
  }

  public double getReferenceFrontIndicatorValue() {
    return referenceFrontIndicatorValue;
  }

  public double getEvaluations() {
    return evaluations;
  }

  public boolean evaluationsLimitReached() {
    return evaluationsLimitReached;
  }

  public QualityIndicator getQualityIndicator() {
    return qualityIndicator;
  }

  public int getEvaluationsLimit() {
    return evaluationsLimit ;
  }
}
