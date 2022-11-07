package org.uma.jmetal.algorithm.multiobjective.mgpso;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 26/10/14.
 */
@SuppressWarnings("serial")
public abstract class AbstractSubSwarmParticleSwarmOptimization<S extends Solution<?>, Result> implements Algorithm<Result> {
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
  protected abstract void createInitialSwarm() ;
  protected abstract void evaluateSwarm() ;
  protected abstract void initializeGlobalBest() ;
  protected abstract void initializeParticleBest() ;
  protected abstract void initializeVelocity() ;
  protected abstract void updateVelocity() ;
  protected abstract void updatePosition() ;
  protected abstract void perturbation() ;
  protected abstract void updateGlobalBest() ;
  protected abstract void updateParticleBest() ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    createInitialSwarm() ;
    evaluateSwarm();
    initializeVelocity();
    initializeParticleBest() ;
    initializeGlobalBest() ;
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity();
      updatePosition();
      perturbation();
      evaluateSwarm() ;
      updateGlobalBest() ;
      updateParticleBest() ;
      updateProgress();
    }
  }
}
