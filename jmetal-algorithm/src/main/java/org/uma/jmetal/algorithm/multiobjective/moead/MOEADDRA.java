package org.uma.jmetal.algorithm.multiobjective.moead;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Class implementing the MOEA/D-DRA algorithm described in :
 * Q. Zhang,  W. Liu,  and H Li, The Performance of a New Version of
 * MOEA/D on CEC09 Unconstrained MOP Test Instances, Working Report CES-491,
 * School of CS & EE, University of Essex, 02/2009
 *
 * @author Juan J. Durillo
 * @author Antonio J. Nebro
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MOEADDRA extends AbstractMOEAD<DoubleSolution> {
  protected DifferentialEvolutionCrossover differentialEvolutionCrossover ;

  protected DoubleSolution[] savedValues;
  protected double[] utility;
  protected int[] frequency;

  JMetalRandom randomGenerator ;

  public MOEADDRA(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize, int maxEvaluations,
                  MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover, FunctionType functionType,
                  String dataDirectory, double neighborhoodSelectionProbability,
                  int maximumNumberOfReplacedSolutions, int neighborSize) {
    super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation, functionType,
        dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
        neighborSize);

    differentialEvolutionCrossover = (DifferentialEvolutionCrossover)crossoverOperator ;

    savedValues = new DoubleSolution[populationSize];
    utility = new double[populationSize];
    frequency = new int[populationSize];
    for (int i = 0; i < utility.length; i++) {
      utility[i] = 1.0;
      frequency[i] = 0;
    }

    randomGenerator = JMetalRandom.getInstance() ;
  }

  @Override public void run() {
    initializePopulation() ;
    initializeUniformWeight();
    initializeNeighborhood();
    idealPoint.update(population); ;

    int generation = 0 ;
    evaluations = populationSize ;
    do {
      List<Integer> order = tourSelection(10) ;
      for (int i = 0; i < order.size(); i++) {
        int subProblemId = order.get(i);
        frequency[subProblemId]++;

        NeighborType neighborType = chooseNeighborType() ;
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType) ;

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        DoubleSolution child = children.get(0) ;
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        idealPoint.update(child.objectives());
        updateNeighborhood(child, subProblemId, neighborType);
      }

      generation++;
      if (generation % 30 == 0) {
        utilityFunction();
      }

    } while (evaluations < maxEvaluations);

  }

  protected void initializePopulation() {
    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newSolution = (DoubleSolution)problem.createSolution();

      problem.evaluate(newSolution);
      population.add(newSolution);
      savedValues[i] = (DoubleSolution) newSolution.copy();
    }
  }

  public void utilityFunction() throws JMetalException {
    double f1, f2, uti, delta;
    for (int n = 0; n < populationSize; n++) {
      f1 = fitnessFunction(population.get(n), lambda[n]);
      f2 = fitnessFunction(savedValues[n], lambda[n]);
      delta = f2 - f1;
      if (delta > 0.001) {
        utility[n] = 1.0;
      } else {
        uti = (0.95 + (0.05 * delta / 0.001)) * utility[n];
        utility[n] = uti < 1.0 ? uti : 1.0;
      }
      savedValues[n] = (DoubleSolution) population.get(n).copy();
    }
  }

  public List<Integer> tourSelection(int depth) {
    List<Integer> selected;
    List<Integer> candidate;

      // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU Et AL (NOT SORTED!!!!)
      List<Integer> result = new ArrayList<>();
      int bound1 = problem.getNumberOfObjectives();
      for (int i3 = 0; i3 < bound1; i3++) {
          Integer integer1 = i3;
          result.add(integer1);
      }
      selected = result;

      // set of unselected weights
      List<Integer> list = new ArrayList<>();
      int bound = populationSize;
      for (int i1 = problem.getNumberOfObjectives(); i1 < bound; i1++) {
          Integer integer = i1;
          list.add(integer);
      }
      candidate = list;

    while (selected.size() < (int) (populationSize / 5.0)) {
      int best_idd = (int) (randomGenerator.nextDouble() * candidate.size());
      int i2;
      int best_sub = candidate.get(best_idd);
      int s2;
      for (int i = 1; i < depth; i++) {
        i2 = (int) (randomGenerator.nextDouble() * candidate.size());
        s2 = candidate.get(i2);
        if (utility[s2] > utility[best_sub]) {
          best_idd = i2;
          best_sub = s2;
        }
      }
      selected.add(best_sub);
      candidate.remove(best_idd);
    }
    return selected;
  }

  @Override public String getName() {
    return "MOEADDRA" ;
  }

  @Override public String getDescription() {
    return "Multi-Objective Evolutionary Algorithm based on Decomposition. Version with Dynamic Resource Allocation" ;
  }
}
