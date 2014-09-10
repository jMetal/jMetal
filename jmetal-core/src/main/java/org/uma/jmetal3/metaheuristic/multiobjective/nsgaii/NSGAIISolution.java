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

  @Override
  public double getCrowdingDistance() {
    return crowdingDistance ;
  }

  @Override
  public int getRanking() {
    return ranking;
  }

  @Override
  public Solution<?> copy() {
    
    return null;
  }
}


//public abstract class GenericSolutionImpl<T, P extends Problem> implements Solution<T>{
// public class BinarySolutionImpl extends GenericSolutionImpl<BitSet, BinaryProblem> implements BinarySolution {
