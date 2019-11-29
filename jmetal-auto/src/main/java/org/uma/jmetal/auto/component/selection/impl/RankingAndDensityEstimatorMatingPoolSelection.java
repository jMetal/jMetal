package org.uma.jmetal.auto.component.selection.impl;

import org.uma.jmetal.auto.component.selection.MatingPoolSelection;
import org.uma.jmetal.component.densityestimator.DensityEstimator;
import org.uma.jmetal.component.ranking.Ranking;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

public class RankingAndDensityEstimatorMatingPoolSelection<S extends Solution<?>> implements MatingPoolSelection<S> {
  private int matingPoolSize ;

  public Ranking<S> ranking ;
  public DensityEstimator<S> densityEstimator ;

  public RankingAndDensityEstimatorMatingPoolSelection(int numberOfSolutionsToSelect, Ranking<S> ranking, DensityEstimator<S> densityEstimator) {
    this.ranking = ranking ;
    this.densityEstimator = densityEstimator ;
    this.matingPoolSize = numberOfSolutionsToSelect ;
  }

  public List<S> select(List<S> solutionList) {
    ranking.computeRanking(solutionList) ;
    for (int i = 0 ; i < ranking.getNumberOfSubFronts(); i++) {
      densityEstimator.computeDensityEstimator(ranking.getSubFront(i));
    }

    return getMatingPool()  ;
  }

  private List<S> getMatingPool() {
    List<S> matingPool = new ArrayList<>() ;
    int rankingIndex = 0 ;
    while (matingPool.size() < matingPoolSize) {
      if (subFrontFillsIntoTheMatingPool(ranking, rankingIndex, matingPool))    {
        densityEstimator.computeDensityEstimator(ranking.getSubFront(rankingIndex));
        addRankedSolutionsToPopulation(ranking, rankingIndex, matingPool);
        rankingIndex++;
      } else {
        densityEstimator.computeDensityEstimator(ranking.getSubFront(rankingIndex));
        addLastRankedSolutionsToPopulation(ranking, rankingIndex, matingPool);
      }
    }

    return matingPool ;
  }

  protected boolean subFrontFillsIntoTheMatingPool(Ranking<S> ranking, int rank, List<S> solutionList) {
    return ranking.getSubFront(rank).size() < (matingPoolSize - solutionList.size()) ;
  }

  protected void addRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> solutionList) {
    List<S> front ;

    front = ranking.getSubFront(rank);

    for (int i = 0 ; i < front.size(); i++) {
      solutionList.add(front.get(i));
    }
  }

  protected void addLastRankedSolutionsToPopulation(Ranking<S> ranking, int rank, List<S> solutionList) {
    List<S> currentRankedFront = ranking.getSubFront(rank) ;

    densityEstimator.sort(currentRankedFront) ;
    //Collections.sort(currentRankedFront, densityEstimator.getSolutionComparator()) ;

    int i = 0 ;
    while ( solutionList.size() < matingPoolSize) {
       solutionList.add(currentRankedFront.get(i)) ;
      i++ ;
    }
  }
}
