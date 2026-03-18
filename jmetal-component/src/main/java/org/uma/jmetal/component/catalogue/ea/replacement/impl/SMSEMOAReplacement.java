package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.ranking.impl.MergeNonDominatedSortRanking;

/**
 * Stateful SMS-EMOA replacement optimized for steady-state updates.
 *
 * <p>The replacement keeps the current front structure across generations when the configured
 * ranking supports standard non-dominated fronts, inserts a single offspring incrementally, and
 * computes hypervolume contributions only on the worst front.
 *
 * @param <S> Solution type
 */
public class SMSEMOAReplacement<S extends Solution<?>> implements Replacement<S> {
  private static final double REFERENCE_POINT_OFFSET_FACTOR = 1.1;

  private final Ranking<S> ranking;
  private final SMSEMOAHypervolumeContributionCalculator contributionCalculator;
  private final boolean incrementalRankingSupported;

  private List<S> cachedPopulation = List.of();
  private List<List<S>> cachedFronts = List.of();
  private double[] cachedMaxObjectives = new double[0];
  private double[] referencePointBuffer = new double[0];

  /**
   * Constructor using the default exact/approximate configuration.
   *
   * @param ranking ranking implementation
   */
  public SMSEMOAReplacement(Ranking<S> ranking) {
    this.ranking = Objects.requireNonNull(ranking);
    this.contributionCalculator = new SMSEMOAHypervolumeContributionCalculator();
    incrementalRankingSupported = supportsIncrementalRanking(ranking);
  }

  /**
   * Constructor using the provided hypervolume implementation.
   *
   * @param ranking ranking implementation
   * @param hypervolume hypervolume implementation
   */
  public SMSEMOAReplacement(Ranking<S> ranking, Hypervolume hypervolume) {
    this.ranking = Objects.requireNonNull(ranking);
    Check.notNull(hypervolume);
    contributionCalculator = new SMSEMOAHypervolumeContributionCalculator();
    incrementalRankingSupported = supportsIncrementalRanking(ranking);
  }

  @Override
  public List<S> replace(List<S> solutionList, List<S> offspringList) {
    Check.notNull(solutionList);
    Check.notNull(offspringList);

    if (offspringList.isEmpty()) {
      rebuildCache(solutionList);
      return new ArrayList<>(solutionList);
    }

    List<S> currentPopulation = new ArrayList<>(solutionList);
    for (S offspring : offspringList) {
      currentPopulation = replaceSingleOffspring(currentPopulation, offspring);
    }

    return currentPopulation;
  }

  private List<S> replaceSingleOffspring(List<S> currentPopulation, S offspring) {
    Check.notNull(offspring);
    Check.that(!currentPopulation.isEmpty(), "The current population cannot be empty");

    if (usesSpecializedExact3DPath(currentPopulation.get(0).objectives().length)) {
      return replaceWithFullRanking(currentPopulation, offspring);
    }

    if (!incrementalRankingSupported) {
      return replaceWithFullRanking(currentPopulation, offspring);
    }

    ensureCacheMatches(currentPopulation);
    return replaceIncrementally(offspring);
  }

  boolean usesSpecializedExact3DPath(int numberOfObjectives) {
    return numberOfObjectives == 3;
  }

  private List<S> replaceWithFullRanking(List<S> currentPopulation, S offspring) {
    List<S> jointPopulation = new ArrayList<>(currentPopulation.size() + 1);
    jointPopulation.addAll(currentPopulation);
    jointPopulation.add(offspring);

    ranking.compute(jointPopulation);

    int worstFrontIndex = ranking.getNumberOfSubFronts() - 1;
    List<S> worstFront = ranking.getSubFront(worstFrontIndex);
    S solutionToRemove = selectLeastContributor(worstFront, jointPopulation);

    List<S> resultPopulation = new ArrayList<>(currentPopulation.size());
    for (int frontIndex = 0; frontIndex < worstFrontIndex; frontIndex++) {
      resultPopulation.addAll(ranking.getSubFront(frontIndex));
    }

    appendFrontWithoutRemovedSolution(resultPopulation, worstFront, solutionToRemove);
    rebuildCache(resultPopulation);

    return resultPopulation;
  }

  private List<S> replaceIncrementally(S offspring) {
    insertOffspringIntoFronts(offspring);

    List<S> worstFront = cachedFronts.get(cachedFronts.size() - 1);
    S solutionToRemove = selectLeastContributor(worstFront, createJointReferencePoint(offspring));
    worstFront.remove(solutionToRemove);
    trimEmptyTrailingFronts();

    List<S> resultPopulation = flattenFronts();
    cachedPopulation = new ArrayList<>(resultPopulation);
    recomputeCachedMaxObjectives(resultPopulation);

    return resultPopulation;
  }

  private void ensureCacheMatches(List<S> currentPopulation) {
    if (cachedPopulation.size() != currentPopulation.size()) {
      rebuildCache(currentPopulation);
      return;
    }

    for (int index = 0; index < currentPopulation.size(); index++) {
      if (cachedPopulation.get(index) != currentPopulation.get(index)) {
        rebuildCache(currentPopulation);
        return;
      }
    }
  }

  private void rebuildCache(List<S> population) {
    if (population.isEmpty()) {
      cachedPopulation = List.of();
      cachedFronts = List.of();
      cachedMaxObjectives = new double[0];
      return;
    }

    ranking.compute(population);

    List<List<S>> fronts = new ArrayList<>(ranking.getNumberOfSubFronts());
    for (int frontIndex = 0; frontIndex < ranking.getNumberOfSubFronts(); frontIndex++) {
      fronts.add(new ArrayList<>(ranking.getSubFront(frontIndex)));
    }

    cachedFronts = fronts;
    cachedPopulation = new ArrayList<>(population);
    recomputeCachedMaxObjectives(population);
  }

  private void recomputeCachedMaxObjectives(List<S> population) {
    int numberOfObjectives = population.get(0).objectives().length;
    if (cachedMaxObjectives.length != numberOfObjectives) {
      cachedMaxObjectives = new double[numberOfObjectives];
    }

    for (int objective = 0; objective < numberOfObjectives; objective++) {
      cachedMaxObjectives[objective] = Double.NEGATIVE_INFINITY;
    }

    for (S solution : population) {
      double[] objectives = solution.objectives();
      for (int objective = 0; objective < numberOfObjectives; objective++) {
        cachedMaxObjectives[objective] =
            Math.max(cachedMaxObjectives[objective], objectives[objective]);
      }
    }
  }

  private void insertOffspringIntoFronts(S offspring) {
    for (int frontIndex = 0; frontIndex < cachedFronts.size(); frontIndex++) {
      List<S> front = cachedFronts.get(frontIndex);
      if (isDominatedByAny(offspring, front)) {
        continue;
      }

      List<S> displacedSolutions = removeSolutionsDominatedBy(front, List.of(offspring));
      front.add(offspring);
      propagateDisplacedSolutions(frontIndex + 1, displacedSolutions);
      return;
    }

    cachedFronts = appendNewFront(cachedFronts, List.of(offspring));
  }

  private void propagateDisplacedSolutions(int startFrontIndex, List<S> displacedSolutions) {
    List<S> pendingSolutions = displacedSolutions;
    int frontIndex = startFrontIndex;

    while (!pendingSolutions.isEmpty()) {
      if (frontIndex >= cachedFronts.size()) {
        cachedFronts = appendNewFront(cachedFronts, pendingSolutions);
        return;
      }

      List<S> currentFront = cachedFronts.get(frontIndex);
      List<S> nextPendingSolutions = removeSolutionsDominatedBy(currentFront, pendingSolutions);
      cachedFronts.set(frontIndex, prependSolutions(pendingSolutions, currentFront));
      pendingSolutions = nextPendingSolutions;
      frontIndex++;
    }
  }

  private List<S> removeSolutionsDominatedBy(List<S> front, List<S> dominators) {
    List<S> dominatedSolutions = new ArrayList<>();
    List<S> survivors = new ArrayList<>(front.size());

    for (S solution : front) {
      if (isDominatedByAny(solution, dominators)) {
        dominatedSolutions.add(solution);
      } else {
        survivors.add(solution);
      }
    }

    front.clear();
    front.addAll(survivors);

    return dominatedSolutions;
  }

  private boolean isDominatedByAny(S candidate, List<S> dominators) {
    for (S dominator : dominators) {
      if (VectorUtils.dominanceTest(dominator.objectives(), candidate.objectives()) == -1) {
        return true;
      }
    }

    return false;
  }

  private List<S> prependSolutions(List<S> prefix, List<S> suffix) {
    List<S> combinedSolutions = new ArrayList<>(prefix.size() + suffix.size());
    combinedSolutions.addAll(prefix);
    combinedSolutions.addAll(suffix);
    return combinedSolutions;
  }

  private List<List<S>> appendNewFront(List<List<S>> fronts, List<S> newFront) {
    List<List<S>> updatedFronts = new ArrayList<>(fronts);
    updatedFronts.add(new ArrayList<>(newFront));
    return updatedFronts;
  }

  private void trimEmptyTrailingFronts() {
    while (!cachedFronts.isEmpty() && cachedFronts.get(cachedFronts.size() - 1).isEmpty()) {
      cachedFronts = new ArrayList<>(cachedFronts.subList(0, cachedFronts.size() - 1));
    }
  }

  private List<S> flattenFronts() {
    List<S> flattenedFronts = new ArrayList<>();
    for (List<S> front : cachedFronts) {
      flattenedFronts.addAll(front);
    }
    return flattenedFronts;
  }

  private S selectLeastContributor(List<S> front, List<S> jointPopulation) {
    return selectLeastContributor(front, calculateReferencePoint(jointPopulation));
  }

  private S selectLeastContributor(List<S> front, double[] referencePoint) {
    if (front.size() == 1) {
      return front.get(0);
    }

    double[] contributions = contributionCalculator.compute(front, referencePoint);
    int leastContributorIndex = 0;
    double leastContribution = contributions[0];

    for (int index = 1; index < contributions.length; index++) {
      if (contributions[index] <= leastContribution) {
        leastContribution = contributions[index];
        leastContributorIndex = index;
      }
    }

    return front.get(leastContributorIndex);
  }

  private double[] calculateReferencePoint(List<S> solutionList) {
    int numberOfObjectives = solutionList.get(0).objectives().length;
    if (referencePointBuffer.length != numberOfObjectives) {
      referencePointBuffer = new double[numberOfObjectives];
    }

    for (int objective = 0; objective < numberOfObjectives; objective++) {
      double maxValue = Double.NEGATIVE_INFINITY;
      for (S solution : solutionList) {
        maxValue = Math.max(maxValue, solution.objectives()[objective]);
      }
      referencePointBuffer[objective] = maxValue * REFERENCE_POINT_OFFSET_FACTOR;
    }

    return referencePointBuffer;
  }
  private double[] createJointReferencePoint(S offspring) {
    int numberOfObjectives = offspring.objectives().length;
    if (referencePointBuffer.length != numberOfObjectives) {
      referencePointBuffer = new double[numberOfObjectives];
    }

    for (int objective = 0; objective < numberOfObjectives; objective++) {
      double maxValue = Math.max(cachedMaxObjectives[objective], offspring.objectives()[objective]);
      referencePointBuffer[objective] = maxValue * REFERENCE_POINT_OFFSET_FACTOR;
    }

    return referencePointBuffer;
  }

  private void appendFrontWithoutRemovedSolution(
      List<S> resultPopulation, List<S> front, S solutionToRemove) {
    boolean removed = false;
    for (S solution : front) {
      if (!removed && solution == solutionToRemove) {
        removed = true;
        continue;
      }
      resultPopulation.add(solution);
    }
  }

  private boolean supportsIncrementalRanking(Ranking<S> ranking) {
    return ranking instanceof FastNonDominatedSortRanking<?>
        || ranking instanceof MergeNonDominatedSortRanking<?>;
  }
}
