package org.uma.jmetal.operator.selection.impl;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.ranking.Ranking;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.solutionattribute.impl.PreferenceDistance;

public class RankingAndPreferenceSelection<S extends Solution<?>>
        implements SelectionOperator<List<S>, List<S>> {
  private final int solutionsToSelect;
  private List<Double> interestPoint;
  private double epsilon;

  /**
   * Constructor
   */
  public RankingAndPreferenceSelection(int solutionsToSelect, List<Double> interestPoint, double epsilon) {
    this.solutionsToSelect = solutionsToSelect;
    this.interestPoint = interestPoint;
    this.epsilon = epsilon;
  }

  /* Getter */
  public int getNumberOfSolutionsToSelect() {
    return solutionsToSelect;
  }

  @Override
  public List<S> execute(@NotNull List<S> solutionList) {
    Check.notNull(solutionList);
    Check.collectionIsNotEmpty(solutionList);
    Check.that(solutionList.size() >= solutionsToSelect, "The population size (" + solutionList.size() + ") is smaller than" +
            "the solutions to selected (" + solutionsToSelect + ")");

    @NotNull Ranking<S> ranking = new FastNonDominatedSortRanking<S>();
    ranking.compute(solutionList);

    return preferenceDistanceSelection(ranking, solutionList.get(0).objectives().length);
  }

  protected List<S> preferenceDistanceSelection(Ranking<S> ranking, int numberOfObjectives) {
    var nInteresPoint = this.interestPoint.size() / numberOfObjectives;

    List<S> population = new ArrayList<>(solutionsToSelect);

    while (population.size() < solutionsToSelect) {
      var indexPoint = 0;
      for (var n = 0; (n < nInteresPoint) && (population.size() < solutionsToSelect); n++) {
        List<S> auxPopulation = new ArrayList<>(solutionsToSelect / nInteresPoint);
        var auxInterestPoint = nextInterestPoint(indexPoint, numberOfObjectives);
        indexPoint += numberOfObjectives;
        var preferenceDistance = new PreferenceDistance<S>(auxInterestPoint, epsilon);
        var rankingIndex = 0;
        while ((auxPopulation.size() < (solutionsToSelect / nInteresPoint)) && (population.size() < solutionsToSelect)) {
          if (subfrontFillsIntoThePopulation(ranking, rankingIndex, auxPopulation)) {
            addRankedSolutionsToPopulation(ranking, rankingIndex, auxPopulation);
            rankingIndex++;
          } else {
            preferenceDistance.computeDensityEstimator(ranking.getSubFront(rankingIndex));
            addLastRankedSolutionsToPopulation(ranking, rankingIndex, auxPopulation);
          }
        }
        population.addAll(auxPopulation);
      }
    }
    var preferenceDistance = new PreferenceDistance<S>(interestPoint, epsilon);
    population = preferenceDistance.epsilonClean(population);
    return population;
  }

  protected boolean subfrontFillsIntoThePopulation(Ranking<S> ranking, int rank, @NotNull List<S> population) {
    return ranking.getSubFront(rank).size() < (solutionsToSelect - population.size());
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, @NotNull List<S> population) {

    var front = ranking.getSubFront(rank);

    for (var i = 0; i < front.size(); i++) {
      population.add(front.get(i));
    }
  }

  protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> population) {
    var currentRankedFront = ranking.getSubFront(rank);

    currentRankedFront.sort(new CrowdingDistanceDensityEstimator<S>().getComparator());

    var i = 0;
    while (population.size() < solutionsToSelect) {
      population.add(currentRankedFront.get(i));
      i++;
    }
  }

  private @Nullable List<Double> nextInterestPoint(int index, int size) {
    List<Double> result = null;
    if (index < this.interestPoint.size()) {
      result = new ArrayList<>(size);
      for (var i = 0; i < size; i++) {
        result.add(this.interestPoint.get(index));
        index++;
      }
    }
    return result;
  }
}
