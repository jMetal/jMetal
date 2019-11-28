package org.uma.jmetal.auto.util.fitness.impl;

import org.uma.jmetal.auto.util.fitness.Fitness;
import org.uma.jmetal.densityestimator.DensityEstimator;
import org.uma.jmetal.densityestimator.impl.KnnDensityEstimator;
import org.uma.jmetal.ranking.Ranking;
import org.uma.jmetal.ranking.impl.StrengthRanking;
import org.uma.jmetal.solution.Solution;

import java.util.List;

public class SPEA2Fitness<S extends Solution<?>> implements Fitness<S> {
  private String attributeId = getClass().getName() ;
  Ranking<S> ranking = new StrengthRanking<S>() ;
  DensityEstimator<S> densityEstimator = new KnnDensityEstimator<>(1) ;

  @Override
  public double getFitness(S solution) {
    return (double) solution.getAttribute(attributeId);
  }

  @Override
  public void computeFitness(List<S> solutionList) {
    ranking.computeRanking(solutionList) ;
    densityEstimator.computeDensityEstimator(solutionList);

    for (S solution : solutionList) {
      double fitness = (int)solution.getAttribute(ranking.getAttributeId())
          + 1.0/ (2.0 + (double)solution.getAttribute(densityEstimator.getAttributeId())) ;
      solution.setAttribute(attributeId, fitness);
    }
  }
}
