package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.SubproblemUpdateCriterion;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;

/**
 * Constraint handling criterion based on the adaptive threshold penalty scheme used in the
 * constrained MOEA/D described in: M. A. Jan and R. A. Khanum, "A study of two penalty-parameterless
 * constraint handling techniques in the framework of MOEA/D", Applied Soft Computing 13(1), 2013.
 *
 * <p>The violation of a solution is measured as the product of the number of violated constraints
 * and the overall constraint violation degree. If the violation of any of the two compared
 * solutions exceeds a threshold, solutions are compared by violation; otherwise, they are compared
 * by their aggregation function values. The threshold is recomputed every generation as the ratio
 * of infeasible solutions in the population multiplied by their mean violation.
 *
 * @param <S> Solution type
 */
public class ViolationThresholdCriterion<S extends Solution<?>>
    implements SubproblemUpdateCriterion<S> {

  private double threshold = 0.0;
  private int updateCounter = 0;

  @Override
  public boolean replaces(
      S newSolution,
      double newSolutionAggregationValue,
      S currentSolution,
      double currentSolutionAggregationValue) {
    double newViolation = violation(newSolution);
    double currentViolation = violation(currentSolution);

    boolean aboveThreshold = (newViolation > threshold) || (currentViolation > threshold);
    if (aboveThreshold && (newViolation != currentViolation)) {
      return newViolation < currentViolation;
    }
    return newSolutionAggregationValue < currentSolutionAggregationValue;
  }

  @Override
  public void update(List<S> population, S newSolution) {
    if (updateCounter % population.size() == 0) {
      threshold = infeasibilityRatio(population) * meanViolation(population);
    }
    updateCounter++;
  }

  private double violation(S solution) {
    return Math.abs(
        ConstraintHandling.numberOfViolatedConstraints(solution)
            * ConstraintHandling.overallConstraintViolationDegree(solution));
  }

  private double infeasibilityRatio(List<S> population) {
    return 1.0 - ConstraintHandling.feasibilityRatio(population);
  }

  private double meanViolation(List<S> population) {
    return population.stream().mapToDouble(this::violation).average().orElse(0.0);
  }
}
