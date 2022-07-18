package org.uma.jmetal.algorithm.multiobjective.ibea;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;

/** This class implements the IBEA algorithm */
@SuppressWarnings("serial")
public class mIBEA<S extends Solution<?>> extends IBEA<S> {

  public mIBEA(
      Problem<S> problem,
      int populationSize,
      int archiveSize,
      int maxEvaluations,
      SelectionOperator<List<S>, S> selectionOperator,
      CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    super(
        problem,
        populationSize,
        archiveSize,
        maxEvaluations,
        selectionOperator,
        crossoverOperator,
        mutationOperator);
  }

  /** Execute() method */
  @Override
  public void run() {
    List<S> offSpringSolutionSet;

    // Initialize the variables
    List<S> solutionSet = new ArrayList<>(populationSize);
    archive = new ArrayList<>(archiveSize);
    var evaluations = 0;

    // -> Create the initial solutionSet
    S newSolution;
    for (var i = 0; i < populationSize; i++) {
      newSolution = problem.createSolution();
      problem.evaluate(newSolution);
      evaluations++;
      solutionSet.add(newSolution);
    }

    while (evaluations < maxEvaluations) {
      List<S> union = new ArrayList<>();
      union.addAll(solutionSet);
      union.addAll(archive);
      union = SolutionListUtils.getNonDominatedSolutions(union); // modificated part
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
        var j = 0;
        do {
          j++;
          parent1 = selectionOperator.execute(archive);
        } while (j < IBEA.TOURNAMENTS_ROUNDS);
        var k = 0;
        do {
          k++;
          parent2 = selectionOperator.execute(archive);
        } while (k < IBEA.TOURNAMENTS_ROUNDS);

        @NotNull List<S> parents = new ArrayList<>(2);
        parents.add(parent1);
        parents.add(parent2);

        // make the crossover
        var offspring = crossoverOperator.execute(parents);
        mutationOperator.execute(offspring.get(0));
        problem.evaluate(offspring.get(0));
        // problem.evaluateConstraints(offSpring[0]);
        offSpringSolutionSet.add(offspring.get(0));
        evaluations++;
      }
      solutionSet = offSpringSolutionSet;
    }
  }

  @Override
  public String getName() {
    return "mIBEA";
  }

  @Override
  public @NotNull String getDescription() {
    return "Modificated Indicator based Evolutionary Algorithm";
  }
}
