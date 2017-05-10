//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Abstract class representing a PSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractParticleSwarmOptimization<S extends Solution<?>, Result> implements Algorithm <Result> {
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
