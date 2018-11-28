package org.uma.jmetal.algorithm.multiobjective.ibea;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.multiobjective.ibea.IBEA;
import org.uma.jmetal.util.SolutionListUtils;

/**
 * This class implements the IBEA algorithm
 */
@SuppressWarnings("serial")
public class mIBEA<S extends Solution<?>> extends IBEA<S> {

    public mIBEA(Problem problem, int populationSize, int archiveSize, int maxEvaluations, SelectionOperator selectionOperator, CrossoverOperator crossoverOperator, MutationOperator mutationOperator) {
        super(problem, populationSize, archiveSize, maxEvaluations, selectionOperator, crossoverOperator, mutationOperator);
    }
  

  /**
   * Execute() method
   */
  @Override public void run() {
    int evaluations;
    List<S> solutionSet, offSpringSolutionSet;

    //Initialize the variables
    solutionSet = new ArrayList<>(populationSize);
    archive = new ArrayList<>(archiveSize);
    evaluations = 0;

    //-> Create the initial solutionSet
    S newSolution;
    for (int i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution();
      problem.evaluate(newSolution);
      evaluations++;
      solutionSet.add(newSolution);
    }

    while (evaluations < maxEvaluations) {
      List<S> union = new ArrayList<>();
      union.addAll(solutionSet);
      union.addAll(archive);
      union=SolutionListUtils.getNondominatedSolutions(union);//modificated part
      calculateFitness(union);
      archive = union;

      while (archive.size() > populationSize) {
        removeWorst(archive);
      }
      // Create a new offspringPopulation
      offSpringSolutionSet = new ArrayList<>(populationSize);
      S parent1;
      S parent2;
      while (offSpringSolutionSet.size() < populationSize) {
        int j = 0;
        do {
          j++;
          parent1 = selectionOperator.execute(archive);
        } while (j < IBEA.TOURNAMENTS_ROUNDS);
        int k = 0;
        do {
          k++;
          parent2 = selectionOperator.execute(archive);
        } while (k < IBEA.TOURNAMENTS_ROUNDS);

        List<S> parents = new ArrayList<>(2);
        parents.add(parent1);
        parents.add(parent2);

        //make the crossover
        List<S> offspring = crossoverOperator.execute(parents);
        mutationOperator.execute(offspring.get(0));
        problem.evaluate(offspring.get(0));
        //problem.evaluateConstraints(offSpring[0]);
        offSpringSolutionSet.add(offspring.get(0));
        evaluations++;
      }
      solutionSet = offSpringSolutionSet;
    }
  }

  

  @Override public String getName() {
    return "mIBEA" ;
  }

  @Override public String getDescription() {
    return "Modificated Indicator based Evolutionary Algorithm" ;
  }
}
