package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractParticleSwarmOptimizationAlgorithm<S extends Solution, Result> implements Algorithm <Result> {
  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  private List<S> swarm ;
  public List<S> getPopulation() {
    return swarm ;
  }

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<S> createInitialSwarm() ;
  protected abstract List<S> evaluateSwarm(List<S> swarm) ;
  protected abstract void initializeLeaders(List<S> swarm) ;
  protected abstract void initializeParticlesMemory(List<S> swarm) ;
  protected abstract void computeSpeed() ;
  protected abstract void computeNewPositions() ;
  protected abstract void perturbation() ;
  protected abstract void updateLeaders() ;
  protected abstract void updateParticleMemory() ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    swarm = createInitialSwarm() ;
    swarm = evaluateSwarm(swarm);
    initializeLeaders(swarm) ;
    initializeParticlesMemory(swarm) ;
    //updateLeadersDensityEstimator() ;
    initProgress();

    while (!isStoppingConditionReached()) {
      computeSpeed();
      computeNewPositions();
      perturbation();
      swarm = evaluateSwarm(swarm) ;
      updateLeaders() ;
      updateParticleMemory() ;
      //updateLeadersDensityEstimator() ;
      updateProgress();
    }
  }
}
