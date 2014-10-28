package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * Created by ajnebro on 26/10/14.
 */
public abstract class AbstractGeneticAlgorithm<Result> implements Algorithm <Result> {
  protected int populationSize ;
  protected Problem problem ;
  protected int evaluations ;
  protected int maxEvaluations ;

  protected List<Solution<?>> population ;
  protected List<Solution<?>> offspringPopulation ;
  protected SelectionOperator<List<Solution<?>>,Solution<?>> selectionOperator ;
  protected CrossoverOperator<List<Solution<?>>,List<Solution<?>>> crossoverOperator ;
  protected MutationOperator mutationOperator ;

  protected abstract void initialization() ;
  protected abstract boolean stoppingCondition() ;
  protected abstract void createInitialPopulation() ;
  protected abstract List<Solution<?>> evaluatePopulation(List<Solution<?>> population) ;
  protected abstract void solutionSelection(List<Solution<?>> population) ;
  @Override
  public abstract Result getResult() ;

  protected void increaseEvaluations() {
    evaluations ++ ;
  }

  protected void increaseEvaluations(int amount) {
    evaluations += amount ;
  }

  @Override
  public void run() {
    initialization();
    createInitialPopulation();
    population = evaluatePopulation(population);
    while (!stoppingCondition()) {
      for (int i = 0; i < (populationSize / 2); i++) {
        List<Solution<?>> parents = new ArrayList<>(2);
        parents.add(selectionOperator.execute(population));
        parents.add(selectionOperator.execute(population));

        List<Solution<?>> offspring = crossoverOperator.execute(parents);

        mutationOperator.execute(offspring.get(0));
        mutationOperator.execute(offspring.get(1));

        offspringPopulation.add(offspring.get(0));
        offspringPopulation.add(offspring.get(1));
      }
      List<Solution<?>> jointPopulation = evaluatePopulation(offspringPopulation);
      increaseEvaluations(offspringPopulation.size());
      solutionSelection(jointPopulation);
    }
  }
}
