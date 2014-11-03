package org.uma.jmetal.algorithm.singleobjective.geneticalgorithm;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.ObjectiveComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Created by ajnebro on 26/10/14.
 */
public class GenerationalGeneticAlgorithm extends AbstractGeneticAlgorithm<Solution, Solution> {
  private Comparator<Solution> comparator ;
  private int maxIterations ;
  private int populationSize ;
  private int iterations ;

  private Problem problem ;

  private SolutionListEvaluator evaluator ;

  /** Constructor */
  private GenerationalGeneticAlgorithm(Builder builder) {
    problem = builder.problem ;
    maxIterations = builder.maxIterations ;
    populationSize = builder.populationSize ;

    crossoverOperator = builder.crossoverOperator ;
    mutationOperator = builder.mutationOperator ;
    selectionOperator = builder.selectionOperator ;

    evaluator = builder.evaluator ;

    comparator = new ObjectiveComparator(0) ;
  }

  /** Builder class */
  public static class Builder {
    private Problem problem ;
    private int maxIterations ;
    private int populationSize ;
    private CrossoverOperator crossoverOperator ;
    private MutationOperator mutationOperator ;
    private SelectionOperator selectionOperator ;
    private SolutionListEvaluator evaluator ;

    /** Builder constructor */
    public Builder(Problem problem) {
      this.problem = problem ;
      maxIterations = 250 ;
      populationSize = 100 ;
      evaluator = new SequentialSolutionListEvaluator() ;
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

    public Builder setSolutionListEvaluator(SolutionListEvaluator evaluator) {
      this.evaluator = evaluator ;

       return this ;
    }

    public GenerationalGeneticAlgorithm build() {
      return new GenerationalGeneticAlgorithm(this) ;
    }
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return (iterations >= maxIterations) ;
  }

  @Override
  protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(populationSize) ;
    for (int i = 0; i < populationSize; i++) {
      Solution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<Solution> replacement(List population, List offspringPopulation) {
    population.sort(comparator);
    offspringPopulation.add(population.get(0)) ;
    offspringPopulation.add(population.get(1)) ;
    offspringPopulation.sort(comparator);
    offspringPopulation.remove(offspringPopulation.size() - 1) ;
    offspringPopulation.remove(offspringPopulation.size() - 1) ;

    return offspringPopulation;
  }

  @Override
  protected List<Solution> reproduction(List<Solution> matingPopulation) {
    List<Solution> offspringPopulation = new ArrayList<>(matingPopulation.size()+2) ;
    for (int i = 0; i < populationSize; i+=2) {
      List<Solution> parents = new ArrayList<>(2);
      parents.add(matingPopulation.get(i)) ;
      parents.add(matingPopulation.get(i + 1));

      List<Solution> offspring = crossoverOperator.execute(parents);
      mutationOperator.execute(offspring.get(0)) ;
      mutationOperator.execute(offspring.get(1)) ;

      offspringPopulation.add(offspring.get(0)) ;
      offspringPopulation.add(offspring.get(1)) ;
    }
    return offspringPopulation ;
  }

  @Override
  protected List<Solution> selection(List<Solution> population) {
    List<Solution> matingPopulation = new ArrayList<>(population.size()) ;
    for (int i = 0; i < populationSize; i++) {
      Solution solution = selectionOperator.execute(population);
      matingPopulation.add(solution) ;
    }

    return matingPopulation;
  }

  @Override
  protected List<Solution> evaluatePopulation(List<Solution> population) {
    for (Solution solution : population) {
      problem.evaluate(solution);
    }

    return population ;
  }

  @Override
  public Solution getResult() {
    getPopulation().sort(comparator);
    return getPopulation().get(0);
  }

  @Override
  public void initProgress() {
    iterations = 1 ;
  }

  @Override
  public void updateProgress() {
    iterations++ ;
  }
}
