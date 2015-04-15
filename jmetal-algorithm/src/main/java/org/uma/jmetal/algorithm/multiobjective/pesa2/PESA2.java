package org.uma.jmetal.algorithm.multiobjective.pesa2;

import org.uma.jmetal.algorithm.impl.AbstractGeneticAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.pesa2.util.PESA2Selection;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ajnebro on 4/4/15.
 */
public class PESA2<S extends Solution> extends AbstractGeneticAlgorithm<S, List<S>> {
  private int maxEvaluations ;
  private int archiveSize ;
  private int populationSize ;
  private int biSections ;

  private int evaluations ;

  protected final Problem<S> problem;
  protected SelectionOperator<AdaptiveGridArchive, S> selectionOperator ;

  private AdaptiveGridArchive<S> archive;
  protected final SolutionListEvaluator<S> evaluator;

  public PESA2(Problem<S> problem, int maxEvaluations, int populationSize, int archiveSize,
      int biSections, CrossoverOperator<List<S>, List<S>> crossoverOperator,
      MutationOperator<S> mutationOperator, SolutionListEvaluator<S> evaluator) {
    this.problem = problem ;
    this.maxEvaluations = maxEvaluations ;
    this.populationSize = populationSize ;
    this.archiveSize = archiveSize ;
    this.biSections = biSections ;

    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = new PESA2Selection();

    this.evaluator = evaluator ;

    archive = new AdaptiveGridArchive<>(archiveSize,biSections,problem.getNumberOfObjectives()) ;
  }

  @Override protected void initProgress() {
    evaluations = populationSize ;
  }

  @Override protected void updateProgress() {
    evaluations += populationSize ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations ;
  }

  @Override protected List<S> createInitialPopulation() {
    List<S> population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      S newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<S> evaluatePopulation(List<S> population) {
    population = evaluator.evaluate(population, problem);

    return population;
  }

  @Override protected List<S> selection(List<S> population) {
    List<S> matingPopulation = new ArrayList<>(populationSize) ;

    for (S solution : population) {
      archive.add(solution) ;
    }

    while (matingPopulation.size() < populationSize) {
      S solution = selectionOperator.execute(archive) ;

      matingPopulation.add(solution);
    }

    return matingPopulation ;
  }

  @Override protected List<S> reproduction(List<S> population) {
    List<S> offspringPopulation = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i+=2) {
      List<S> parents = new ArrayList<>(2);
      parents.add(population.get(i));
      parents.add(population.get(i + 1));

      List<S> offspring = crossoverOperator.execute(parents);

      mutationOperator.execute(offspring.get(0));

      offspringPopulation.add(offspring.get(0));
    }
    return offspringPopulation;
  }

  @Override protected List<S> replacement(List<S> population, List<S> offspringPopulation) {
    for (S solution : offspringPopulation) {
      archive.add(solution) ;
    }

    return Collections.emptyList();
  }

  @Override public List<S> getResult() {
    return archive.getSolutionList();
  }
}
