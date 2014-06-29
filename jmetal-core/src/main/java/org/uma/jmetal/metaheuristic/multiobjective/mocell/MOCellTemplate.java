package org.uma.jmetal.metaheuristic.multiobjective.mocell;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

/**
 * Created by antelverde on 29/06/14.
 */
public abstract class MOCellTemplate extends Algorithm {
  protected SolutionSetEvaluator evaluator ;

  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;

  protected SolutionSet population;
  protected SolutionSet offspringPopulation;

  protected Operator mutationOperator;
  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  protected MOCellTemplate(Builder builder) {
    super() ;

    evaluator = builder.evaluator_ ;
    populationSize = builder.populationSize_ ;
    maxEvaluations = builder.maxEvaluations_ ;
    mutationOperator = builder.mutationOperator_ ;
    crossoverOperator = builder.crossoverOperator_ ;
    selectionOperator = builder.selectionOperator_ ;
    evaluations = 0 ;
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      newSolution.setLocation(i);
      population.add(newSolution);
    }
  }

  protected SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    for (int i = 0; i < populationSize; i++) {
      problem_.evaluate(population.get(i));
      problem_.evaluateConstraints(population.get(i));
    }
    return population ;
  }

  public static class Builder {
    protected SolutionSetEvaluator evaluator_ ;
    protected Problem problem_ ;

    protected int populationSize_;
    protected int maxEvaluations_;

    protected Operator mutationOperator_;
    protected Operator crossoverOperator_;
    protected Operator selectionOperator_;

    public Builder(Problem problem, SolutionSetEvaluator evaluator) {
      evaluator_ = evaluator ;
      problem_ = problem ;
    }

    public Builder populationSize(int populationSize) {
      populationSize_ = populationSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      maxEvaluations_ = maxEvaluations ;

      return this ;
    }

    public Builder crossover(Operator crossover) {
      crossoverOperator_ = crossover ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator_ = mutation ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator_ = selection ;

      return this ;
    }

    public MOCellTemplate build(String mocellVariant) {
      MOCellTemplate algorithm = null ;
      /*
      if ("NSGAII".equals(NSGAIIVariant)) {
        algorithm = new NSGAII(this);
      } else if ("SteadyStateNSGAII".equals(NSGAIIVariant)) {
        algorithm =  new SteadyStateNSGAII(this) ;
      } else {
        throw new JMetalException(NSGAIIVariant + " variant unknown") ;
      }

*/

      return algorithm ;
    }
  }
}
