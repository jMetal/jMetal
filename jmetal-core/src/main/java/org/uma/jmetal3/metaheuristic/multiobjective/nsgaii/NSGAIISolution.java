package org.uma.jmetal3.metaheuristic.multiobjective.nsgaii;

import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.encoding.attributes.CrowdingDistanceSolution;
import org.uma.jmetal3.encoding.attributes.RankingSolution;
import org.uma.jmetal3.encoding.impl.GenericSolutionImpl;

/**
 * Created by antonio on 10/09/14.
 */
public class NSGAIISolution extends GenericSolutionImpl implements RankingSolution, CrowdingDistanceSolution {
  private double crowdingDistance ;
  private int ranking ;
  private Solution solution ;

  public NSGAIISolution(Solution solution) {
    this.solution = solution ;
  }

  @Override
  public double getCrowdingDistance() {
    return crowdingDistance ;
  }

  @Override
  public int getRank() {
    return ranking;
  }

  @Override
  public void setRank(int ranking) {
    this.ranking = ranking ;
  }

  @Override
  public void setCrowdingDistance(double crowdingDistance) {
    this.crowdingDistance = crowdingDistance ;
  }

  @Override
  public Solution<?> copy() {
    NSGAIISolution newSolution = new NSGAIISolution(this) ;
    newSolution.solution = this.solution.copy();

    return newSolution;
  }

  public Solution getProblemSolution() {
     return this.solution ;
  }
}