package org.uma.jmetal.algorithm.multiobjective.mgpso.resources;

import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

/**
 * Created by ajnebro on 26/10/14.
 */
@SuppressWarnings("serial")
public abstract class AbstractParticleSwarmOptimization<S extends Solution<?>, Result> implements Algorithm<Result> {
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
  protected abstract void initializeGlobalBest(List<S> swarm) ;
  protected abstract void initializeParticleBest(List<S> swarm) ;
  protected abstract void initializeVelocity(List<S> swarm) ;
  protected abstract void updateVelocity(List<S> swarm) ;
  protected abstract void updatePosition(List<S> swarm) ;
  protected abstract void perturbation(List<S> swarm) ;
  protected abstract void updateGlobalBest(List<S> swarm) ;
  protected abstract void updateParticleBest(List<S> swarm) ;

  @Override
  public abstract Result getResult() ;

  @Override
  public void run() {
    swarm = createInitialSwarm() ;
    swarm = evaluateSwarm(swarm);
    initializeVelocity(swarm);
    initializeParticleBest(swarm) ;
    initializeGlobalBest(swarm) ;
    initProgress();

    while (!isStoppingConditionReached()) {
      updateVelocity(swarm);
      updatePosition(swarm);
      perturbation(swarm);
      swarm = evaluateSwarm(swarm) ;
      updateGlobalBest(swarm) ;
      updateParticleBest(swarm) ;
      updateProgress();
    }
  }
}
