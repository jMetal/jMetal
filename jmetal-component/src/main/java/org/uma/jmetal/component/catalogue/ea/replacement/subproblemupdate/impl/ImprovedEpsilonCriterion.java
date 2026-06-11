package org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.impl;

import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.component.catalogue.ea.replacement.subproblemupdate.SubproblemUpdateCriterion;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Constraint handling criterion based on the improved epsilon constraint-handling method presented
 * in: Z. Fan, W. Li, X. Cai, H. Huang, Y. Fang, Y. You, J. Mo, C. Wei, and E. D. Goodman, "An
 * improved epsilon constraint-handling method in MOEA/D for CMOPs with large infeasible regions",
 * Soft Computing, 2019. https://doi.org/10.1007/s00500-019-03794-x
 *
 * <p>If the constraint violation degrees of the two compared solutions are within the current
 * epsilon level, or if they are equal, solutions are compared by their aggregation function values;
 * otherwise, the one with the lowest violation wins. The epsilon level starts from the violation of
 * the 5% most violated solution of the initial population and is updated every generation: it is
 * reduced geometrically while the feasibility ratio of the population is below a threshold,
 * enlarged proportionally to the maximum violation found so far otherwise, and set to zero after
 * {@code tc} generations.
 *
 * @param <S> Solution type
 */
public class ImprovedEpsilonCriterion<S extends Solution<?>>
    implements SubproblemUpdateCriterion<S> {

  private final double tao;
  private final double feasibilityRatioThreshold;
  private final int tc;

  private double epsilonK = 0.0;
  private double phiMax = 0.0;
  private int generationCounter = 0;
  private int updateCounter = 0;
  private boolean initialized = false;

  public ImprovedEpsilonCriterion(int tc) {
    this(0.1, 0.95, tc);
  }

  public ImprovedEpsilonCriterion(double tao, double feasibilityRatioThreshold, int tc) {
    Check.that(tao > 0 && tao < 1, "tao must be in (0, 1): " + tao);
    Check.that(tc > 0, "tc must be positive: " + tc);
    this.tao = tao;
    this.feasibilityRatioThreshold = feasibilityRatioThreshold;
    this.tc = tc;
  }

  @Override
  public boolean replaces(
      S newSolution,
      double newSolutionAggregationValue,
      S currentSolution,
      double currentSolutionAggregationValue) {
    double newViolation = violation(newSolution);
    double currentViolation = violation(currentSolution);

    boolean bothWithinEpsilon = (currentViolation < epsilonK) && (newViolation <= epsilonK);
    if (bothWithinEpsilon || (newViolation == currentViolation)) {
      return newSolutionAggregationValue < currentSolutionAggregationValue;
    }
    return newViolation < currentViolation;
  }

  @Override
  public void update(List<S> population, S newSolution) {
    if (!initialized) {
      initialize(population);
    }
    phiMax = Math.max(phiMax, violation(newSolution));

    updateCounter++;
    if (updateCounter % population.size() == 0) {
      generationCounter++;
      updateEpsilonLevel(ConstraintHandling.feasibilityRatio(population));
    }
  }

  private void initialize(List<S> population) {
    List<Double> violations =
        population.stream().map(this::violation).sorted(Comparator.reverseOrder()).toList();

    int index = (int) Math.ceil(0.05 * population.size());
    epsilonK = violations.get(index);
    phiMax = violations.get(0);

    initialized = true;
  }

  private void updateEpsilonLevel(double feasibilityRatio) {
    if (generationCounter >= tc) {
      epsilonK = 0;
    } else if (feasibilityRatio < feasibilityRatioThreshold) {
      epsilonK = (1 - tao) * epsilonK;
    } else {
      epsilonK = phiMax * (1 + tao);
    }
  }

  private double violation(S solution) {
    return Math.abs(ConstraintHandling.overallConstraintViolationDegree(solution));
  }
}
