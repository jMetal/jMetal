package org.uma.jmetal.util.ranking.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.Ranking;

/**
 * This class implements a solution list ranking based on the strength concept defined in SPEA2. The
 * strength of solution is computed by considering the number of solutions they dominates and the
 * strenght of the solutions dominating it. As an output, a set of subsets are obtained. The subsets
 * are numbered starting from 0; thus, subset 0 contains the non-dominated solutions, subset 1
 * contains the non-dominated population after removing those belonging to subset 0, and so on.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StrengthRanking<S extends Solution<?>> implements Ranking<S> {
  private final String attributeId = getClass().getName();
  private Comparator<S> dominanceComparator;

  private List<ArrayList<S>> rankedSubPopulations;

  /** Constructor */
  public StrengthRanking(Comparator<S> comparator) {
    this.dominanceComparator = comparator;
    rankedSubPopulations = new ArrayList<>();
  }

  /** Constructor */
  public StrengthRanking() {
    this(new DominanceWithConstraintsComparator<>());
  }

  @Override
  public Ranking<S> compute(@NotNull List<S> solutionList) {
    var strength = new int[solutionList.size()];
    var rawFitness = new int[solutionList.size()];

    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (var i = 0; i < solutionList.size(); i++) {
      for (var j = 0; j < solutionList.size(); j++) {
        if (dominanceComparator.compare(solutionList.get(i), solutionList.get(j)) < 0) {
          strength[i] += 1.0;
        }
      }
    }

    // Calculate the raw fitness:
    // rawFitness(i) = |{sum strength(j) | j <- SolutionSet and j dominate i}|
    for (var i = 0; i < solutionList.size(); i++) {
      for (var j = 0; j < solutionList.size(); j++) {
        if (dominanceComparator.compare(solutionList.get(i), solutionList.get(j)) == 1) {
          rawFitness[i] += strength[j];
        }
      }
    }

    var maxFitnessValue = 0;
    for (var i = 0; i < solutionList.size(); i++) {
      solutionList.get(i).attributes().put(attributeId, rawFitness[i]);
      if (rawFitness[i] > maxFitnessValue) {
        maxFitnessValue = rawFitness[i];
      }
    }

    // front[i] contains the list of individuals belonging to the front i
    rankedSubPopulations = new ArrayList<>(maxFitnessValue + 1);
    var bound = maxFitnessValue + 1;
      for (var index = 0; index < bound; index++) {
          rankedSubPopulations.add(new ArrayList<>());
      }

      // Assign each solution to its corresponding front
      for (var solution : solutionList) {
          rankedSubPopulations.get((int) solution.attributes().get(attributeId)).add(solution);
      }

      // Remove empty fronts
    // rankedSubPopulations.stream().filter(list -> (list.size() == 0));
    var counter = 0;
    while (counter < rankedSubPopulations.size()) {
      if (rankedSubPopulations.get(counter).size() == 0) {
        rankedSubPopulations.remove(counter);
      } else {
        counter++;
      }
    }

    return this;
  }

  @Override
  public List<S> getSubFront(int rank) {
    if (rank >= rankedSubPopulations.size()) {
      throw new JMetalException(
          "Invalid rank: " + rank + ". Max rank = " + (rankedSubPopulations.size() - 1));
    }
    return rankedSubPopulations.get(rank);
  }

  @Override
  public int getNumberOfSubFronts() {
    return rankedSubPopulations.size();
  }

  @Override
  public Integer getRank(@NotNull S solution) {
    Check.notNull(solution);

    Integer result = -1;
    if (solution.attributes().get(attributeId) != null) {
      result = (Integer) solution.attributes().get(attributeId);
    }
    return result;
  }

  @Override
  public @NotNull Object getAttributedId() {
    return attributeId ;
  }
}
