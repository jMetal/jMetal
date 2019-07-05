package org.uma.jmetal.auto.component.replacement.impl;

import org.uma.jmetal.auto.component.replacement.Replacement;
import org.uma.jmetal.auto.component.selection.impl.RankingAndDensityEstimatorMatingPoolSelection;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class RankingAndDensityEstimatorReplacement<S extends Solution<?>> implements Replacement<S> {
  public Ranking<S> ranking ;
  public DensityEstimator<S> densityEstimator ;

  public RankingAndDensityEstimatorReplacement(Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this.ranking = ranking ;
    this.densityEstimator = densityEstimator ;
  }

  public List<S> replace(List<S> solutionList, List<S> offspringList) {
    List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(solutionList);
    jointPopulation.addAll(offspringList);

    //RankingAndDensityEstimatorMatingPoolSelection<S> selection ;
    //selection = new RankingAndDensityEstimatorMatingPoolSelection<S>(solutionList.size(), ranking, densityEstimator);

    //return selection.select(jointPopulation) ;
    ranking.computeRanking(jointPopulation) ;
    //return truncate(jointPopulation, solutionList.size(),densityEstimator) ;
    return truncate2(ranking, 0, solutionList.size()) ;
  }

  private List<S> truncate(List<S> solutionList, int size, DensityEstimator<S> densityEstimator) {
    return new RankingAndDensityEstimatorMatingPoolSelection<S>(size, ranking, densityEstimator)
        .select((solutionList));
  }

  private List<S> truncate2(Ranking<S> ranking, int rankingId, int sizeOfTheResultingSolutionList) {
    List<S> currentRankSolutions = ranking.getSubFront(rankingId) ;
    densityEstimator.computeDensityEstimator(currentRankSolutions) ;

    List<S> resultList = new ArrayList<>() ;

    if (currentRankSolutions.size() < sizeOfTheResultingSolutionList) {
      resultList.addAll(ranking.getSubFront(rankingId)) ;
      resultList.addAll(truncate2(ranking, rankingId+1, sizeOfTheResultingSolutionList - currentRankSolutions.size())) ;
    } else {
      densityEstimator.sort(currentRankSolutions) ;
      int i = 0 ;
      while (resultList.size() < sizeOfTheResultingSolutionList) {
        resultList.add(currentRankSolutions.get(i)) ;
        i++ ;
      }
    }

    return resultList ;
  }

}
