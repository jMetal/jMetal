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

package org.uma.jmetal.algorithm.multiobjective.cdg;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.uma.jmetal.algorithm.AlgorithmBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Builder class for algorithm CDG
 *
 * @author Feng Zhang
 * @version 1.0
 */
public class CDGBuilder implements AlgorithmBuilder<AbstractCDG<DoubleSolution>> {

  protected Problem<DoubleSolution> problem;

  /** Delta in Zhang & Li paper */
  protected double neighborhoodSelectionProbability;

  protected CrossoverOperator<DoubleSolution> crossover;

  protected int populationSize;

  protected int resultPopulationSize;

  protected int numberOfThreads;

  protected double sigma_;

  protected int maxEvaluations;

  protected int k_;

  protected int t_;

  protected int subproblemNum_;

  protected int childGrid_;

  protected int childGridNum_;

  /** Constructor */
  public CDGBuilder(Problem<DoubleSolution> problem) {

    this.problem = problem;
    populationSize = 300;
    resultPopulationSize = 300;
    maxEvaluations = 300000;
    crossover = new DifferentialEvolutionCrossover();
    neighborhoodSelectionProbability = 0.9;
    numberOfThreads = 1;
    sigma_ = 10e-6;

    if (problem.getNumberOfObjectives() == 2) {
      k_ = 180;
      t_ = 1;
      childGrid_ = 60;
    } else if (problem.getNumberOfObjectives() == 3) {
      k_ = 25;
      t_ = 1;
      k_++;
      childGrid_ = 20;
    } else {
      k_ = 180;
      t_ = 5;
    }
    childGridNum_ = (int) Math.pow(childGrid_, problem.getNumberOfObjectives());
    childGridNum_++;
    subproblemNum_ = (int) Math.pow(k_, problem.getNumberOfObjectives() - 1);
    subproblemNum_ = subproblemNum_ * problem.getNumberOfObjectives();
  }

  /* Getters/Setters */
  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public int getResultPopulationSize() {
    return resultPopulationSize;
  }

  public CrossoverOperator<DoubleSolution> getCrossover() {
    return crossover;
  }

  public double getNeighborhoodSelectionProbability() {
    return neighborhoodSelectionProbability;
  }

  public int getNumberOfThreads() {
    return numberOfThreads;
  }

  public int getK() {
    return k_;
  }

  public double getT() {
    return t_;
  }

  public int getChildGrid() {
    return childGrid_;
  }

  public int getChildGridNum() {
    return childGridNum_;
  }

  public CDGBuilder setPopulationSize(int populationSize) {
    this.populationSize = populationSize;

    return this;
  }

  public @NotNull CDGBuilder setResultPopulationSize(int resultPopulationSize) {
    this.resultPopulationSize = resultPopulationSize;

    return this;
  }

  public @NotNull CDGBuilder setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  public @NotNull CDGBuilder setNeighborhoodSelectionProbability(double neighborhoodSelectionProbability) {
    this.neighborhoodSelectionProbability = neighborhoodSelectionProbability;

    return this;
  }

  public @NotNull CDGBuilder setCrossover(CrossoverOperator<DoubleSolution> crossover) {
    this.crossover = crossover;

    return this;
  }

  public @NotNull CDGBuilder setNumberOfThreads(int numberOfThreads) {
    this.numberOfThreads = numberOfThreads;

    return this;
  }

  public @NotNull CDGBuilder setK(int k) {
    this.k_ = k;

    return this;
  }

  public CDGBuilder setT(int t) {
    this.t_ = t;

    return this;
  }

  public @NotNull CDGBuilder setChildGrid(int childGrid) {
    this.childGrid_ = childGrid;

    return this;
  }

  public CDGBuilder setChildGridNum(int childGridNum) {
    this.childGridNum_ = childGridNum;

    return this;
  }

  public AbstractCDG<DoubleSolution> build() {
    @Nullable AbstractCDG<DoubleSolution> algorithm = null;
    algorithm =
        new CDG(
            problem,
            populationSize,
            resultPopulationSize,
            maxEvaluations,
            crossover,
            neighborhoodSelectionProbability,
            sigma_,
            k_,
            t_,
            subproblemNum_,
            childGrid_,
            childGridNum_);
    return algorithm;
  }
}
