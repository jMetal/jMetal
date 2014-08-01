package org.uma.jmetal.metaheuristic.multiobjective.smsemoa;

import org.uma.jmetal.core.*;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.Ranking;
import org.uma.jmetal.util.evaluator.SequentialSolutionSetEvaluator;
import org.uma.jmetal.util.evaluator.SolutionSetEvaluator;

/**
 * Created by Antonio J. Nebro on 31/07/14.
 */
public abstract class SMSEMOATemplate extends Algorithm {
  private static final double DEFAULT_OFFSET = 100.0 ;

  private SolutionSetEvaluator evaluator ;

  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;

  protected SolutionSet population;
  protected SolutionSet offspringPopulation;

  protected Operator mutationOperator;
  protected Operator crossoverOperator;
  protected Operator selectionOperator;

  protected double offset ;

  protected SMSEMOATemplate(Builder builder) {
    super() ;

    problem_ = builder.problem;
    populationSize = builder.populationSize;
    maxEvaluations = builder.maxEvaluations;
    mutationOperator = builder.mutationOperator;
    crossoverOperator = builder.crossoverOperator;
    selectionOperator = builder.selectionOperator;
    offset = builder.offset;

    evaluations = 0 ;
    evaluator = new SequentialSolutionSetEvaluator() ;
  }

  protected void createInitialPopulation() throws ClassNotFoundException, JMetalException {
    population = new SolutionSet(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = new Solution(problem_);
      population.add(newSolution);
    }
  }

  protected SolutionSet evaluatePopulation(SolutionSet population) throws JMetalException {
    evaluations += population.size() ;

    return evaluator.evaluate(population, problem_) ;
  }

  protected boolean stoppingCondition() {
    return evaluations >= maxEvaluations;
  }

  protected SolutionSet getNonDominatedSolutions(SolutionSet solutionSet) throws JMetalException {
    return new Ranking(solutionSet).getSubfront(0);
  }

  /* Getters */
  public Operator getCrossoverOperator() {
    return crossoverOperator;
  }

  public Operator getMutationOperator() {
    return mutationOperator;
  }

  public Operator getSelectionOperator() {
    return selectionOperator;
  }

  public int getPopulationSize() {
    return populationSize;
  }

  public int getMaxEvaluations() {
    return maxEvaluations;
  }

  public double getOffset() {
    return offset ;
  }

  public int getEvaluations () {
    return evaluations;
  }

  /** Builder class */
  public static class Builder {
    protected Problem problem;

    protected int populationSize;
    protected int maxEvaluations;

    protected Operator mutationOperator;
    protected Operator crossoverOperator;
    protected Operator selectionOperator;

    protected double offset ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.offset = DEFAULT_OFFSET ;
    }

    public Builder populationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder maxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder crossover(Operator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder mutation(Operator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder selection(Operator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public Builder offset(double offset) {
      this.offset = offset ;

      return this ;
    }

    public SMSEMOATemplate build(String smsemoaVariant) {
      SMSEMOATemplate algorithm  ;
      if ("SMSEMOA".equals(smsemoaVariant)) {
        algorithm = new SMSEMOA(this);
      } else if ("FastSMSEMOA".equals(smsemoaVariant)) {
        algorithm =  new FastSMSEMOA(this) ;
      } else {
        throw new JMetalException(smsemoaVariant + " variant unknown") ;
      }

      return algorithm ;
    }
  }
}
