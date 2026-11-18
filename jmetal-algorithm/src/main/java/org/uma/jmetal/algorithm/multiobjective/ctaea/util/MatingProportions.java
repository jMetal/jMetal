package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Proportions that the restricted mating selection of C-TAEA uses to choose the archive of each
 * parent, computed as in the main loop of C-TAEA in PlatEMO (all the sorting uses Pareto
 * dominance on the objectives, without constraints):
 *
 * @param convergenceShare the proportion Pc of non-dominated solutions of the union Hm = CA + DA
 *     in its first half (the CA), relative to the size of Hm
 * @param diversityShare the proportion Pd of non-dominated solutions of Hm in its second half
 *     (the DA), relative to the size of Hm
 * @param nonDominatedShareOfConvergenceArchive the proportion PC of non-dominated solutions of
 *     the CA, sorted alone
 */
public record MatingProportions(
    double convergenceShare, double diversityShare, double nonDominatedShareOfConvergenceArchive) {

  /**
   * Computes the proportions of two archives.
   *
   * @param convergenceArchive the convergence archive (CA), not empty
   * @param diversityArchive the diversity archive (DA)
   * @param <S> the type of the solutions
   * @return the proportions
   */
  public static <S extends Solution<?>> MatingProportions of(
      List<S> convergenceArchive, List<S> diversityArchive) {
    Check.collectionIsNotEmpty(convergenceArchive, "convergenceArchive");
    Check.notNull(diversityArchive, "diversityArchive");

    List<S> union = new ArrayList<>(convergenceArchive);
    union.addAll(diversityArchive);
    int[] fronts =
        NonDominatedSorting.frontNumbers(SolutionListUtils.getMatrixWithObjectiveValues(union));
    int half = (union.size() + 1) / 2;
    int nonDominatedInFirstHalf = 0;
    int nonDominatedInSecondHalf = 0;
    for (int i = 0; i < union.size(); i++) {
      if (fronts[i] == 1) {
        if (i < half) {
          nonDominatedInFirstHalf++;
        } else {
          nonDominatedInSecondHalf++;
        }
      }
    }

    int[] frontsOfConvergenceArchive =
        NonDominatedSorting.frontNumbers(
            SolutionListUtils.getMatrixWithObjectiveValues(convergenceArchive));
    int nonDominatedInConvergenceArchive = 0;
    for (int front : frontsOfConvergenceArchive) {
      if (front == 1) {
        nonDominatedInConvergenceArchive++;
      }
    }

    return new MatingProportions(
        (double) nonDominatedInFirstHalf / union.size(),
        (double) nonDominatedInSecondHalf / union.size(),
        (double) nonDominatedInConvergenceArchive / convergenceArchive.size());
  }
}
