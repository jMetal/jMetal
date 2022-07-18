package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.experimental.componentbasedalgorithm.util.Preference;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.ranking.Ranking;

public class RankingAndDensityEstimatorReplacement<S extends Solution<?>>
    implements Replacement<S> {
  private Ranking<S> ranking;
  private DensityEstimator<S> densityEstimator;
  private RemovalPolicy removalPolicy;

  public RankingAndDensityEstimatorReplacement(
          Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this(ranking, densityEstimator, RemovalPolicy.sequential);
  }

  public RankingAndDensityEstimatorReplacement(
          Ranking<S> ranking, DensityEstimator<S> densityEstimator, RemovalPolicy removalPolicy) {
    this.ranking = ranking;
    this.densityEstimator = densityEstimator;
    this.removalPolicy = removalPolicy;
  }

  public RankingAndDensityEstimatorReplacement(
      Preference<S> preference, RemovalPolicy removalPolicy) {
    this.ranking = preference.getRanking();
    this.densityEstimator = preference.getDensityEstimator();
    this.removalPolicy = removalPolicy;
  }

  public List<S> replace(List<S> solutionList, @NotNull List<S> offspringList) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(solutionList);
    jointPopulation.addAll(offspringList);

    List<S> resultList;
    ranking.compute(jointPopulation);

    if (removalPolicy == RemovalPolicy.oneShot) {
      resultList = oneShotTruncation(0, solutionList.size());
    } else {
      resultList = sequentialTruncation(0, solutionList.size());
    }
    return resultList;
  }

  private @NotNull List<S> oneShotTruncation(int rankingId, int sizeOfTheResultingSolutionList) {
    var currentRankSolutions = ranking.getSubFront(rankingId);
    densityEstimator.compute(currentRankSolutions);

    @NotNull List<S> resultList = new ArrayList<>();

    if (currentRankSolutions.size() < sizeOfTheResultingSolutionList) {
      resultList.addAll(ranking.getSubFront(rankingId));
      resultList.addAll(
          oneShotTruncation(
              rankingId + 1, sizeOfTheResultingSolutionList - currentRankSolutions.size()));
    } else {
      currentRankSolutions.sort(Comparator.comparing(densityEstimator::getValue).reversed());
      var i = 0;
      while (resultList.size() < sizeOfTheResultingSolutionList) {
        resultList.add(currentRankSolutions.get(i));
        i++;
      }
    }

    return resultList;
  }

  private List<S> sequentialTruncation(int rankingId, int sizeOfTheResultingSolutionList) {
    var currentRankSolutions = ranking.getSubFront(rankingId);
    densityEstimator.compute(currentRankSolutions);

    @NotNull List<S> resultList = new ArrayList<>();

    if (currentRankSolutions.size() < sizeOfTheResultingSolutionList) {
      resultList.addAll(ranking.getSubFront(rankingId));
      resultList.addAll(
          sequentialTruncation(
              rankingId + 1, sizeOfTheResultingSolutionList - currentRankSolutions.size()));
    } else {
      for (var solution : currentRankSolutions) resultList.add(solution);
      while (resultList.size() > sizeOfTheResultingSolutionList) {
        resultList.sort(Comparator.comparing(densityEstimator::getValue).reversed()) ;

        resultList.remove(resultList.size() - 1);
        densityEstimator.compute(resultList);
      }
    }

    return resultList;
  }
}
