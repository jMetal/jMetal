package org.uma.jmetal.algorithm.multiobjective.ctaea.util;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Update of the diversity archive (DA) of C-TAEA, transcribed from the {@code UpdateDA} function
 * of PlatEMO. The DA ignores the constraints: it is filled with the solutions of the current DA
 * and the offspring that best complement the (already updated) convergence archive (CA) in the
 * subregions where the CA has few solutions.
 *
 * <p>Both the candidates and the CA are associated with the subregions on their raw objectives.
 * In iteration {@code itr} (starting at 1), every subregion i (in order) with fewer than {@code
 * itr} solutions of the CA receives up to {@code itr} minus that number of candidates of the same
 * subregion: each time, the candidate with the smallest Tchebycheff value among the
 * non-dominated candidates of the subregion is moved to the new DA (the first one in case of a
 * tie), computed with the weight vector of the subregion and the ideal point of those
 * non-dominated candidates. The iterations go on until the DA has as many solutions as weight
 * vectors.
 *
 * <p>Solutions are removed from the candidates by identity, never with {@code equals}.
 *
 * @param <S> the type of the solutions
 */
public class DiversityArchiveUpdate<S extends Solution<?>> {
  private final double[][] weights;

  /**
   * Creates the update.
   *
   * @param weights the weight vectors; their number is the size of the archive
   */
  public DiversityArchiveUpdate(double[][] weights) {
    Check.notNull(weights, "weights");
    Check.that(weights.length > 0, "There must be at least one weight vector");
    this.weights = weights;
  }

  /**
   * Computes the new diversity archive.
   *
   * @param convergenceArchive the convergence archive, already updated
   * @param diversityArchive the current diversity archive (empty at the beginning)
   * @param offspring the new solutions, already evaluated
   * @return the new archive, with as many solutions as weight vectors if there are enough
   *     candidates (or all of them otherwise)
   */
  public List<S> update(List<S> convergenceArchive, List<S> diversityArchive, List<S> offspring) {
    Check.notNull(convergenceArchive, "convergenceArchive");
    Check.notNull(diversityArchive, "diversityArchive");
    Check.notNull(offspring, "offspring");

    int size = weights.length;
    List<S> candidates = new ArrayList<>(diversityArchive);
    candidates.addAll(offspring);
    List<Integer> candidateRegions = new ArrayList<>(candidates.size());
    for (S candidate : candidates) {
      candidateRegions.add(Subregions.associate(candidate.objectives(), weights));
    }

    int[] solutionsOfTheConvergenceArchive = new int[size];
    for (S solution : convergenceArchive) {
      solutionsOfTheConvergenceArchive[Subregions.associate(solution.objectives(), weights)]++;
    }

    List<S> selected = new ArrayList<>(size);
    int iteration = 1;
    while (selected.size() < size && !candidates.isEmpty()) {
      for (int region = 0; region < size && selected.size() < size; region++) {
        int missing = iteration - solutionsOfTheConvergenceArchive[region];
        for (int j = 0; j < missing; j++) {
          int best = bestCandidateOfTheSubregion(candidates, candidateRegions, region);
          if (best < 0) {
            break;
          }
          S solution = candidates.remove(best);
          candidateRegions.remove(best);
          if (selected.size() < size) {
            selected.add(solution);
          }
        }
      }
      iteration++;
    }
    return selected;
  }

  /**
   * Returns the index (in the candidates) of the non-dominated candidate of a subregion with the
   * smallest Tchebycheff value, or -1 if the subregion has no candidates.
   */
  private int bestCandidateOfTheSubregion(
      List<S> candidates, List<Integer> candidateRegions, int region) {
    List<Integer> inRegion = new ArrayList<>();
    for (int i = 0; i < candidates.size(); i++) {
      if (candidateRegions.get(i) == region) {
        inRegion.add(i);
      }
    }
    if (inRegion.isEmpty()) {
      return -1;
    }

    List<S> members = new ArrayList<>(inRegion.size());
    for (int index : inRegion) {
      members.add(candidates.get(index));
    }
    int[] fronts =
        NonDominatedSorting.frontNumbers(SolutionListUtils.getMatrixWithObjectiveValues(members));
    List<Integer> nonDominated = new ArrayList<>();
    for (int i = 0; i < members.size(); i++) {
      if (fronts[i] == 1) {
        nonDominated.add(inRegion.get(i));
      }
    }

    double[][] objectives = new double[nonDominated.size()][];
    for (int i = 0; i < nonDominated.size(); i++) {
      objectives[i] = candidates.get(nonDominated.get(i)).objectives();
    }
    double[] tchebycheff = Subregions.tchebycheffInOwnSubregion(objectives, weights);
    return nonDominated.get(Subregions.indexOfMinimum(tchebycheff));
  }
}
