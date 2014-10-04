package org.uma.jmetal3.metaheuristic.multiobjective.smsemoa;

import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal3.core.Algorithm;
import org.uma.jmetal3.core.Problem;
import org.uma.jmetal3.core.Solution;
import org.uma.jmetal3.operator.crossover.CrossoverOperator;
import org.uma.jmetal3.operator.mutation.MutationOperator;
import org.uma.jmetal3.operator.selection.SelectionOperator;
import org.uma.jmetal3.util.solutionattribute.Ranking;
import org.uma.jmetal3.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Antonio J. Nebro on 31/07/14.
 */
public abstract class SMSEMOATemplate implements Algorithm<List<Solution>> {
  private static final double DEFAULT_OFFSET = 100.0 ;

  protected Problem problem ;

  protected int populationSize;
  protected int maxEvaluations;
  protected int evaluations;

  protected List<Solution<?>> population;
  protected List<Solution<?>> offspringPopulation;

  protected MutationOperator mutation;
  protected CrossoverOperator crossover;
  protected SelectionOperator selection;

  protected double offset ;

  protected Ranking ranking ;

  protected SMSEMOATemplate(Builder builder) {
    problem = builder.problem;
    populationSize = builder.populationSize;
    maxEvaluations = builder.maxEvaluations;
    mutation = builder.mutationOperator;
    crossover = builder.crossoverOperator;
    selection = builder.selectionOperator;
    offset = builder.offset;

    ranking = new DominanceRanking() ;

    evaluations = 0 ;
  }

  protected void createInitialPopulation()  {
    population = new ArrayList<>(populationSize);

    Solution newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution() ;
      population.add(newSolution);
    }
  }

  protected List<Solution<?>> evaluatePopulation(List<Solution<?>> population) throws JMetalException {
    evaluations += population.size() ;

    for (int i = 0 ; i < population.size(); i++) {
      problem.evaluate(population.get(i)) ;
    }

    return population ;
  }

  protected boolean stoppingCondition() {
    return evaluations >= maxEvaluations;
  }

  protected List<Solution<?>> getNonDominatedSolutions(List<Solution<?>> solutionSet) {
    return ranking.computeRanking(solutionSet).getSubfront(0);
  }

  protected void computeRanking(List<Solution<?>> solutionSet) {
    ranking.computeRanking(solutionSet) ;
  }

  /* Getters */
  public CrossoverOperator getCrossover() {
    return crossover;
  }

  public MutationOperator getMutation() {
    return mutation;
  }

  public SelectionOperator getSelection() {
    return selection;
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

    protected MutationOperator mutationOperator;
    protected CrossoverOperator crossoverOperator;
    protected SelectionOperator selectionOperator;

    protected double offset ;

    public Builder(Problem problem) {
      this.problem = problem ;
      this.offset = DEFAULT_OFFSET ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setMaxEvaluations(int maxEvaluations) {
      this.maxEvaluations = maxEvaluations ;

      return this ;
    }

    public Builder setCrossover(CrossoverOperator crossover) {
      crossoverOperator = crossover ;

      return this ;
    }

    public Builder setMutation(MutationOperator mutation) {
      mutationOperator = mutation ;

      return this ;
    }

    public Builder setSelection(SelectionOperator selection) {
      selectionOperator = selection ;

      return this ;
    }

    public Builder setOffset(double offset) {
      this.offset = offset ;

      return this ;
    }

    public SMSEMOATemplate build(String smsemoaVariant) {
      SMSEMOATemplate algorithm  ;
      if ("SMSEMOA".equals(smsemoaVariant)) {
        algorithm = new SMSEMOA(this);
      //} else if ("FastSMSEMOA".equals(smsemoaVariant)) {
      //  algorithm =  new FastSMSEMOA(this) ;
      } else {
        throw new JMetalException(smsemoaVariant + " variant unknown") ;
      }

      return algorithm ;
    }
  }
}
