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

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a scatter search algorithm
 *
 * @param <S> Solution
 * @param <R> Result

 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractScatterSearch<S extends Solution<?>, R>  implements Algorithm<R>{
  private List<S> population;

  public List<S> getPopulation() {
    return population;
  }

  public void setPopulation(List<S> population) {
    this.population = population;
  }

  private int populationSize ;
  public int getPopulationSize () {
    return populationSize ;
  }

  public void setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;
  }

  public abstract boolean isStoppingConditionReached();

  public abstract boolean restartConditionIsFulfilled(List<S> solutionList);

  public abstract void restart();

  public abstract S diversificationGeneration();

  public abstract S improvement(S solution);

  public abstract void referenceSetUpdate();
  public abstract void referenceSetUpdate(S solution);

  public abstract List<List<S>> subsetGeneration();

  public abstract List<S> solutionCombination(List<List<S>> population);

  @Override public abstract R getResult();

  @Override public void run() {
    initializationPhase() ;
    referenceSetUpdate();
    while (!isStoppingConditionReached()) {
      List<List<S>> subset = subsetGeneration();
      List<S> combinedSolutions = solutionCombination(subset) ;
      if (restartConditionIsFulfilled(combinedSolutions)) {
        restart();
        referenceSetUpdate();
      } else {
        for (S solution : combinedSolutions) {
          S improvedSolution = improvement(solution);
          referenceSetUpdate(improvedSolution);
        }
      }
    }
  }

  /**
   * Initialization phase of the scatter search: the population is filled with diverse solutions that
   * have been improved.
   * @return The population
   */
  public void initializationPhase() {
    population = new ArrayList<>(populationSize) ;
    while (population.size() < populationSize) {
      S newSolution = diversificationGeneration() ;
      S improvedSolution = improvement(newSolution) ;
      population.add(improvedSolution) ;
    }
  }
}
