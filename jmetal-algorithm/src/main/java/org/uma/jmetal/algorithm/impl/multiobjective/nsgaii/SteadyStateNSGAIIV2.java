package org.uma.jmetal.algorithm.impl.multiobjective.nsgaii;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.solutionattribute.Ranking;
import org.uma.jmetal.util.solutionattribute.impl.CrowdingDistance;
import org.uma.jmetal.util.solutionattribute.impl.DominanceRanking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 30/10/14.
 */
public class SteadyStateNSGAIIV2 extends NSGAIIV2 {

  /** Constructor */
  private SteadyStateNSGAIIV2(Builder builder) {
    problem = builder.problem ;
    maxIterations = builder.maxIterations ;
    populationSize = builder.populationSize ;

    crossoverOperator = builder.crossoverOperator ;
    mutationOperator = builder.mutationOperator ;
    selectionOperator = builder.selectionOperator ;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int maxIterations ;
    private int populationSize ;
    private CrossoverOperator crossoverOperator ;
    private MutationOperator mutationOperator ;
    private SelectionOperator selectionOperator ;

    /** Builder constructor */
    public Builder(Problem problem) {
      this.problem = problem ;
      maxIterations = 250 ;
      populationSize = 100 ;
    }

    public Builder setMaxIterations(int maxIterations) {
      this.maxIterations = maxIterations ;

      return this ;
    }

    public Builder setPopulationSize(int populationSize) {
      this.populationSize = populationSize ;

      return this ;
    }

    public Builder setCrossoverOperator(CrossoverOperator crossoverOperator) {
      this.crossoverOperator = crossoverOperator ;

      return this ;
    }

    public Builder setMutationOperator(MutationOperator mutationOperator) {
      this.mutationOperator = mutationOperator ;

      return this ;
    }

    public Builder setSelectionOperator(SelectionOperator selectionOperator) {
      this.selectionOperator = selectionOperator ;

      return this ;
    }

    public SteadyStateNSGAIIV2 build() {
      return new SteadyStateNSGAIIV2(this) ;
    }
  }

  @Override
  protected List<Solution> selection(List<Solution> population) {
    List<Solution> matingPopulation = new ArrayList<>(2) ;

    matingPopulation.add(selectionOperator.execute(population)) ;
    matingPopulation.add(selectionOperator.execute(population)) ;

    return matingPopulation;
  }

  @Override
  protected List<Solution> reproduction(List<Solution> population) {
    List<Solution> offspringPopulation = new ArrayList<>(1);

    List<Solution> parents = new ArrayList<>(2);
    parents.add(population.get(0));
    parents.add(population.get(1));

    List<Solution> offspring = crossoverOperator.execute(parents);

    mutationOperator.execute(offspring.get(0));

    offspringPopulation.add(offspring.get(0));
    return offspringPopulation ;
  }
}
