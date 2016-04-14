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
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.List;

/**
 * Abstract class representing an evolutionary algorithm
 * @param <S> Solution
 * @param <R> Result
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class AbstractEvolutionaryAlgorithm<S extends Solution<?>, R>  implements Algorithm<R>{
  private List<S> population;
  private int maxPopulationSize ;
  private Problem<S> problem ;

  public List<S> getPopulation() {
    return population;
  }
  public void setPopulation(List<S> population) {
    this.population = population;
  }

  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize ;
  }
  public int getMaxPopulationSize() {
    return maxPopulationSize ;
  }

  public void setProblem(Problem<S> problem) {
    this.problem = problem ;
  }
  public Problem<S> getProblem() {
    return problem ;
  }

  protected abstract void initProgress();

  protected abstract void updateProgress();

  protected abstract boolean isStoppingConditionReached();

  protected abstract  List<S> createInitialPopulation() ;

  protected abstract List<S> evaluatePopulation(List<S> population);

  protected abstract List<S> selection(List<S> population);

  protected abstract List<S> reproduction(List<S> population);

  protected abstract List<S> replacement(List<S> population, List<S> offspringPopulation);

  @Override public abstract R getResult();

  @Override public void run() {
    List<S> offspringPopulation;
    List<S> matingPopulation;

    population = createInitialPopulation();
    population = evaluatePopulation(population);
    initProgress();
    while (!isStoppingConditionReached()) {
      matingPopulation = selection(population);
      offspringPopulation = reproduction(matingPopulation);
      offspringPopulation = evaluatePopulation(offspringPopulation);
      population = replacement(population, offspringPopulation);
      updateProgress();
    }
  }
}
