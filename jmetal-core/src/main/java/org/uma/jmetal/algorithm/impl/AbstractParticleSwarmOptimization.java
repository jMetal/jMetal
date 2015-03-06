package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractParticleSwarmOptimization<S extends Solution, Result> implements Algorithm <Result> {
  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<S> createInitialSwarm() ;
  protected abstract List<S> evaluateSwarm(List<S> swarm) ;
  protected abstract void initializeLeaders(List<S> swarm) ;
  protected abstract void initializeParticlesMemory(List<S> swarm) ;
  protected abstract void initializeVelocity(List<S> swarm) ;
  protected abstract void updateVelocity(List<S> swarm) ;
  protected abstract void updatePosition(List<S> swarm) ;
  protected abstract void perturbation(List<S> swarm) ;
  protected abstract void updateLeaders(List<S> swarm) ;
  protected abstract void updateParticlesMemory(List<S> swarm) ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    List<S> swarm ;
    swarm = createInitialSwarm() ;
    swarm = evaluateSwarm(swarm);
    initializeLeaders(swarm) ;
    initializeParticlesMemory(swarm) ;
    initializeLeaders(swarm);
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity(swarm);
      updatePosition(swarm);
      perturbation(swarm);
      swarm = evaluateSwarm(swarm) ;
      updateLeaders(swarm) ;
      updateParticlesMemory(swarm) ;
      updateProgress();
    }
  }
}
