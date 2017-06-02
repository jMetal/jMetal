package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;

import java.util.List;

/**
 * Abstract class representing a PSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractParticleSwarmOptimization<S, Result> implements Algorithm <Result> {
  private List<S> swarm;
  public List<S> getSwarm() {
    return swarm;
  }
  public void setSwarm(List<S> swarm) {
    this.swarm = swarm;
  }

  protected abstract void initProgress() ;
  protected abstract void updateProgress() ;

  protected abstract boolean isStoppingConditionReached() ;
  protected abstract List<S> createInitialSwarm() ;
  protected abstract List<S> evaluateSwarm(List<S> swarm) ;
  protected abstract void initializeLeader(List<S> swarm) ;
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
    swarm = createInitialSwarm() ;
    swarm = evaluateSwarm(swarm);
    initializeVelocity(swarm);
    initializeParticlesMemory(swarm) ;
    initializeLeader(swarm) ;
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
