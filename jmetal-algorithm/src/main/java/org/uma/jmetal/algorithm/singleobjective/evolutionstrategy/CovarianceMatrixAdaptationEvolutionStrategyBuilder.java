package org.uma.jmetal.algorithm.singleobjective.evolutionstrategy;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;

/**
 * 
 * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
 *
 */
public class CovarianceMatrixAdaptationEvolutionStrategyBuilder<S extends Solution<?>> implements
		AlgorithmBuilder<CovarianceMatrixAdaptationEvolutionStrategy> {
    private DoubleProblem problem ;
    private int lambda ;
    private int maxEvaluations ;
    private double [] typicalX;
    private double sigma;

    public CovarianceMatrixAdaptationEvolutionStrategyBuilder(DoubleProblem problem) {
      this.problem = problem;
      lambda = CovarianceMatrixAdaptationEvolutionStrategy.DEFAULT_LAMBDA;
      maxEvaluations = CovarianceMatrixAdaptationEvolutionStrategy.DEFAULT_MAX_EVALUATIONS;
      sigma = CovarianceMatrixAdaptationEvolutionStrategy.DEFAULT_SIGMA;
    }

    public CovarianceMatrixAdaptationEvolutionStrategyBuilder(DoubleProblem problem, int lambda, int maxEvaluations, double[] typicalX, double sigma) {
      this.problem = problem;
      this.lambda = lambda;
      this.maxEvaluations = maxEvaluations;
      this.sigma = sigma;
    }

    public CovarianceMatrixAdaptationEvolutionStrategyBuilder<S> setLambda(int lambda) {
      this.lambda = lambda;
      return this;
    }

    public CovarianceMatrixAdaptationEvolutionStrategyBuilder<S> setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations;
      return this;
    }

    public CovarianceMatrixAdaptationEvolutionStrategyBuilder<S> setTypicalX (double [] typicalX) {
      this.typicalX = typicalX;
      return this;
    }

    public CovarianceMatrixAdaptationEvolutionStrategyBuilder<S> setSigma (double sigma) {
      this.sigma = sigma;
      return this;
    }

    public CovarianceMatrixAdaptationEvolutionStrategy build() {
      return new CovarianceMatrixAdaptationEvolutionStrategy(problem, lambda, maxEvaluations, typicalX, sigma);
    }
}
