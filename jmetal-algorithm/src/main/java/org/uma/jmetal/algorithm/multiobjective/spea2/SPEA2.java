package org.uma.jmetal.algorithm.multiobjective.spea2;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.EnvironmentalSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.impl.StrengthRawFitness;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Juanjo
 **/
public class SPEA2 extends AbstractGeneticAlgorithm<Solution, List<Solution>> {

  protected final int maxIterations;
  protected final int populationSize;
  protected final Problem problem;
  protected final SolutionListEvaluator<Solution> evaluator;
  protected int iterations;
  protected List<Solution> archive;
  private final static StrengthRawFitness strenghtRawFitness = new StrengthRawFitness();
  private final EnvironmentalSelection environmentalSelection;

  public SPEA2(Problem problem, int maxIterations, int populationSize,
      CrossoverOperator crossoverOperator,
      MutationOperator mutationOperator,
      SelectionOperator selectionOperator, SolutionListEvaluator evaluator) {
    super();
    this.problem = problem;
    this.maxIterations = maxIterations;
    this.populationSize = populationSize;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;
    this.environmentalSelection = new EnvironmentalSelection(populationSize);

    this.archive = new ArrayList<>(populationSize);

    this.evaluator = evaluator;
  }

  @Override
  protected void initProgress() {
    iterations = 1;
  }

  @Override
  protected void updateProgress() {
    iterations++;
  }

  @Override
  protected boolean isStoppingConditionReached() {
    return iterations >= maxIterations;
  }

  @Override
  protected List<Solution> createInitialPopulation() {
    List<Solution> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      Solution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override
  protected List<Solution> evaluatePopulation(List<Solution> population) {
    population = evaluator.evaluate(population, problem);
    return population;
  }

  @Override
  protected List<Solution> selection(List<Solution> population) {
    List<Solution> union = new ArrayList<>(2*populationSize);
    union.addAll(archive);
    union.addAll(population);
    strenghtRawFitness.computeDensityEstimator(union);
    archive = environmentalSelection.execute(union);
    return archive;
  }

  @Override
  protected List<Solution> reproduction(List<Solution> population) {
    List<Solution> offSpringPopulation= new ArrayList<>(populationSize);

    while (offSpringPopulation.size() < populationSize){
      List<Solution> parents = new ArrayList<>(2);
      Solution candidateFirstParent = selectionOperator.execute(population);
      parents.add(candidateFirstParent);
      Solution candidateSecondParent;
      candidateSecondParent = selectionOperator.execute(population);
      parents.add(candidateSecondParent);

      //make the crossover
      List<Solution> offspring = crossoverOperator.execute(parents);
      mutationOperator.execute(offspring.get(0));
      offSpringPopulation.add(offspring.get(0));
    }
    return offSpringPopulation;
  }

  @Override
  protected List<Solution> replacement(List<Solution> population,
      List<Solution> offspringPopulation) {
    return offspringPopulation;
  }

  @Override
  public List<Solution> getResult() {
    return archive;
  }

}
