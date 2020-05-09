package org.uma.jmetal.component.ranking.impl;

import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.AttributeComparator;
import org.uma.jmetal.solution.util.attribute.util.attributecomparator.impl.IntegerValueAttributeComparator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

/**
 * This class implements a solution list ranking based on the strength concept defined in SPEA2.
 * The strength of solution is computed by considering the number of solutions they dominates and the strenght of the
 * solutions dominating it.
 * As an output, a set of subsets are obtained. The subsets are numbered starting from 0; thus, subset 0 contains the
 * non-dominated solutions, subset 1 contains the non-dominated population after removing those belonging to subset
 * 0, and so on.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class StrengthRanking<S extends Solution<?>> implements Ranking<S> {
  private String attributeId = getClass().getName();
  private Comparator<S> dominanceComparator;
  private Comparator<S> solutionComparator;

  private List<ArrayList<S>> rankedSubPopulations;

  /** Constructor */
  public StrengthRanking(Comparator<S> comparator) {
    this.dominanceComparator = comparator;
    rankedSubPopulations = new ArrayList<>();
    this.solutionComparator =
        new IntegerValueAttributeComparator<>(attributeId, AttributeComparator.Ordering.DESCENDING);
  }

  /** Constructor */
  public StrengthRanking() {
    this(new DominanceComparator<>());
  }

  @Override
  public Ranking<S> computeRanking(List<S> solutionList) {
    int[] strength = new int[solutionList.size()];
    int[] rawFitness = new int[solutionList.size()];

    // strength(i) = |{j | j <- SolutionSet and i dominate j}|
    for (int i = 0; i < solutionList.size(); i++) {
      for (int j = 0; j < solutionList.size(); j++) {
        if (dominanceComparator.compare(solutionList.get(i), solutionList.get(j)) < 0) {
          strength[i] += 1.0;
        }
      }
    }

    // Calculate the raw fitness:
    // rawFitness(i) = |{sum strength(j) | j <- SolutionSet and j dominate i}|
    for (int i = 0; i < solutionList.size(); i++) {
      for (int j = 0; j < solutionList.size(); j++) {
        if (dominanceComparator.compare(solutionList.get(i), solutionList.get(j)) == 1) {
          rawFitness[i] += strength[j];
        }
      }
    }

    int maxFitnessValue = 0;
    for (int i = 0; i < solutionList.size(); i++) {
      solutionList.get(i).setAttribute(attributeId, rawFitness[i]);
      if (rawFitness[i] > maxFitnessValue) {
        maxFitnessValue = rawFitness[i];
      }
    }

    // front[i] contains the list of individuals belonging to the front i
    rankedSubPopulations = new ArrayList<>(maxFitnessValue + 1);
    IntStream.range(0, maxFitnessValue + 1)
        .forEach(index -> rankedSubPopulations.add(new ArrayList<>()));

    // Assign each solution to its corresponding front
    solutionList.stream()
        .forEach(
            solution ->
                rankedSubPopulations.get((int) solution.getAttribute(attributeId)).add(solution));

    // Remove empty fronts
    // rankedSubPopulations.stream().filter(list -> (list.size() == 0));
    int counter = 0;
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
  public Comparator<S> getSolutionComparator() {
    return solutionComparator;
  }

  @Override
  public String getAttributeId() {
    return attributeId;
  }
}
