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

package org.uma.jmetal.algorithm.multiobjective.smsemoa;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.qualityindicator.impl.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMSEMOABuilder<S extends Solution<?>> implements AlgorithmBuilder<SMSEMOA<S>> {
  private static final double DEFAULT_OFFSET = 100.0 ;

  protected Problem<S> problem;

  protected int populationSize;
  protected int maxEvaluations;

  protected CrossoverOperator<S> crossoverOperator;
  protected MutationOperator<S> mutationOperator;
  protected SelectionOperator<List<S>, S> selectionOperator;

  protected double offset ;

  protected Hypervolume<S> hypervolumeImplementation;

  public SMSEMOABuilder(Problem<S> problem, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem ;
    this.offset = DEFAULT_OFFSET ;
    populationSize = 100 ;
    maxEvaluations = 25000 ;
    this.hypervolumeImplementation = new PISAHypervolume<>() ;
    hypervolumeImplementation.setOffset(offset);

    this.crossoverOperator = crossoverOperator ;
    this.mutationOperator = mutationOperator ;
    this.selectionOperator = new RandomSelection<S>() ;
  }

  public SMSEMOABuilder<S> setPopulationSize(int populationSize) {
    this.populationSize = populationSize ;

    return this ;
  }

  public SMSEMOABuilder<S> setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations ;

    return this ;
  }

  public SMSEMOABuilder<S> setCrossoverOperator(CrossoverOperator<S> crossover) {
    crossoverOperator = crossover ;

    return this ;
  }

  public SMSEMOABuilder<S> setMutationOperator(MutationOperator<S> mutation) {
    mutationOperator = mutation ;

    return this ;
  }

  public SMSEMOABuilder<S> setSelectionOperator(SelectionOperator<List<S>, S> selection) {
    selectionOperator = selection ;

    return this ;
  }

  public SMSEMOABuilder<S> setHypervolumeImplementation(Hypervolume<S> hypervolumeImplementation) {
    this.hypervolumeImplementation = hypervolumeImplementation;

    return this ;
  }


  public SMSEMOABuilder<S> setOffset(double offset) {
    this.offset = offset ;

    return this ;
  }

  @Override public SMSEMOA<S> build() {
    return new SMSEMOA<S>(problem, maxEvaluations, populationSize, offset,
        crossoverOperator, mutationOperator, selectionOperator, hypervolumeImplementation);
  }

  /*
   * Getters
   */

  public Problem<S> getProblem() {
    return problem;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public CrossoverOperator<S> getCrossoverOperator() {
    return crossoverOperator;
  }

  public MutationOperator<S> getMutationOperator() {
    return mutationOperator;
  }

  public SelectionOperator<List<S>, S> getSelectionOperator() {
    return selectionOperator;
  }

  public double getOffset() {
    return offset;
  }
}
