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

package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;

/**
 * Class implementing a (mu , lambda) Evolution Strategy (lambda must be divisible by mu)
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class EvolutionStrategyBuilder<S extends Solution<?>> implements AlgorithmBuilder<Algorithm<S>> {
  public enum EvolutionStrategyVariant {ELITIST, NON_ELITIST}

  private Problem<S> problem;
  private int mu;
  private int lambda;
  private int maxEvaluations;
  private MutationOperator<S> mutation;
  private EvolutionStrategyVariant variant ;

  public EvolutionStrategyBuilder(Problem<S> problem, MutationOperator<S> mutationOperator,
      EvolutionStrategyVariant variant) {
    this.problem = problem;
    this.mu = 1;
    this.lambda = 10;
    this.maxEvaluations = 250000;
    this.mutation = mutationOperator;
    this.variant = variant ;
  }

  public EvolutionStrategyBuilder<S> setMu(int mu) {
    this.mu = mu;

    return this;
  }

  public EvolutionStrategyBuilder<S> setLambda(int lambda) {
    this.lambda = lambda;

    return this;
  }

  public EvolutionStrategyBuilder<S> setMaxEvaluations(int maxEvaluations) {
    this.maxEvaluations = maxEvaluations;

    return this;
  }

  @Override public Algorithm<S> build() {
    if (variant == EvolutionStrategyVariant.ELITIST) {
      return new ElitistEvolutionStrategy<S>(problem, mu, lambda, maxEvaluations, mutation);
    } else if (variant == EvolutionStrategyVariant.NON_ELITIST) {
      return new NonElitistEvolutionStrategy<S>(problem, mu, lambda, maxEvaluations, mutation);
    } else {
      throw new JMetalException("Unknown variant: " + variant) ;
    }
  }

  /* Getters */
  public int getMu() {
    return mu;
  }

  public int getLambda() {
    return lambda;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public MutationOperator<S> getMutation() {
    return mutation;
  }
}
