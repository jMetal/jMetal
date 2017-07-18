package org.uma.jmetal.algorithm.multiobjective.mochc;

import org.uma.jmetal.algorithm.impl.AbstractEvolutionaryAlgorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.binarySet.BinarySet;
import org.uma.jmetal.util.comparator.CrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This class executes the MOCHC algorithm described in:
 * A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo
 * "Optimal antenna placement using a new multi-objective chc algorithm".
 * GECCO '07: Proceedings of the 9th annual conference on Genetic and
 * evolutionary computation. London, England. July 2007.
 */
@SuppressWarnings("serial")
public class MOCHC extends AbstractEvolutionaryAlgorithm<BinarySolution, List<BinarySolution>> {
  private BinaryProblem problem;

  private int maxEvaluations;
  private int maxPopulationSize ;
  private int convergenceValue;
  private double preservedPopulation;
  private double initialConvergenceCount;
  private CrossoverOperator<BinarySolution> crossover;
  private MutationOperator<BinarySolution> cataclysmicMutation;
  private SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection;
  private SelectionOperator<List<BinarySolution>, BinarySolution> parentSelection;
  private int evaluations;
  private int minimumDistance;
  private int size;
  private Comparator<BinarySolution> comparator;

  private SolutionListEvaluator<BinarySolution> evaluator;
  private int lastOffspringPopulationSize ;

  /**
   * Constructor
   */
  public MOCHC(BinaryProblem problem, int populationSize, int maxEvaluations, int convergenceValue,
      double preservedPopulation, double initialConvergenceCount,
      CrossoverOperator<BinarySolution> crossoverOperator,
      MutationOperator<BinarySolution> cataclysmicMutation,
      SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection, SelectionOperator<List<BinarySolution>, BinarySolution> parentSelection,
      SolutionListEvaluator<BinarySolution> evaluator) {
    super();
    this.problem = problem;
    setMaxPopulationSize(populationSize);
    this.maxEvaluations = maxEvaluations;
    this.convergenceValue = convergenceValue;
    this.preservedPopulation = preservedPopulation;
    this.initialConvergenceCount = initialConvergenceCount;
    this.crossover = crossoverOperator;
    this.cataclysmicMutation = cataclysmicMutation;
    this.newGenerationSelection = newGenerationSelection;
    this.parentSelection = parentSelection;
    this.evaluator = evaluator;

    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      size += problem.getNumberOfBits(i);
    }
    minimumDistance = (int) Math.floor(this.initialConvergenceCount * size);

    comparator = new CrowdingDistanceComparator<BinarySolution>();
  }
  
  public void setMaxPopulationSize(int maxPopulationSize) {
    this.maxPopulationSize = maxPopulationSize ;
  }
  public int getMaxPopulationSize() {
    return maxPopulationSize ;
  }

  @Override protected void initProgress() {
    evaluations = getMaxPopulationSize() ;
  }

  @Override protected void updateProgress() {
    evaluations += lastOffspringPopulationSize ;
  }

  @Override protected boolean isStoppingConditionReached() {
    return evaluations >= maxEvaluations;
  }

  @Override protected List<BinarySolution> createInitialPopulation() {
    List<BinarySolution> population = new ArrayList<>(getMaxPopulationSize());
    for (int i = 0; i < getMaxPopulationSize(); i++) {
      BinarySolution newIndividual = problem.createSolution();
      population.add(newIndividual);
    }
    return population;
  }

  @Override protected List<BinarySolution> evaluatePopulation(List<BinarySolution> population) {
    population = evaluator.evaluate(population, problem);

    return population;
  }

  @Override protected List<BinarySolution> selection(List<BinarySolution> population) {
    List<BinarySolution> matingPopulation = new ArrayList<>(population.size());
    for (int i = 0; i < population.size(); i ++) {
      BinarySolution solution = parentSelection.execute(population);
      matingPopulation.add(solution);
    }

    return matingPopulation;
  }

  @Override protected List<BinarySolution> reproduction(List<BinarySolution> matingPopulation) {
    List<BinarySolution> offspringPopulation = new ArrayList<>();

    for (int i = 0; i < matingPopulation.size(); i += 2) {
      List<BinarySolution> parents = new ArrayList<>(2);
      parents.add(matingPopulation.get(i));
      parents.add(matingPopulation.get(i + 1));

      if (hammingDistance(parents.get(0), parents.get(1)) >= minimumDistance) {
        List<BinarySolution> offspring = crossover.execute(parents);
        offspringPopulation.add(offspring.get(0));
        offspringPopulation.add(offspring.get(1));
      }
    }

    lastOffspringPopulationSize = offspringPopulation.size() ;
    return offspringPopulation;
  }

  @Override protected List<BinarySolution> replacement(List<BinarySolution> population,
      List<BinarySolution> offspringPopulation) {
    List<BinarySolution> union = new ArrayList<>();
    union.addAll(population);
    union.addAll(offspringPopulation);

    List<BinarySolution> newPopulation = newGenerationSelection.execute(union);

    if (SolutionListUtils.solutionListsAreEquals(population, newPopulation)) {
      minimumDistance--;
    }

    if (minimumDistance <= -convergenceValue) {
     // minimumDistance = (int) (1.0 / size * (1 - 1.0 / size) * size);
      minimumDistance = (int) (0.35 * (1 - 0.35) * size);

      int preserve = (int) Math.floor(preservedPopulation * population.size());
      newPopulation = new ArrayList<>(getMaxPopulationSize());
      Collections.sort(population, comparator);
      for (int i = 0; i < preserve; i++) {
        newPopulation.add((BinarySolution) population.get(i).copy());
      }
      for (int i = preserve; i < getMaxPopulationSize(); i++) {
        BinarySolution solution = (BinarySolution) population.get(i).copy();
        cataclysmicMutation.execute(solution);

        newPopulation.add(solution);
      }
    }

    return newPopulation;
  }

  @Override public List<BinarySolution> getResult() {
    NonDominatedSolutionListArchive<BinarySolution> archive = new NonDominatedSolutionListArchive<>() ;
    for (BinarySolution solution : getPopulation()) {
      archive.add(solution) ;
    }

    return archive.getSolutionList();
  }

  /**
   * Calculate the hamming distance between two solutions
   *
   * @param solutionOne A <code>Solution</code>
   * @param solutionTwo A <code>Solution</code>
   * @return the hamming distance between solutions
   */

  private int hammingDistance(BinarySolution solutionOne, BinarySolution solutionTwo) {
    int distance = 0;
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      distance += hammingDistance(solutionOne.getVariableValue(i), solutionTwo.getVariableValue(i));
    }

    return distance;
  }

  private int hammingDistance(BinarySet bitSet1, BinarySet bitSet2) {
    if (bitSet1.getBinarySetLength() != bitSet2.getBinarySetLength()) {
      throw new JMetalException("The bitsets have different length: "
          + bitSet1.getBinarySetLength() +", " + bitSet2.getBinarySetLength()) ;
    }
    int distance = 0;
    int i = 0;
    while (i < bitSet1.getBinarySetLength()) {
      if (bitSet1.get(i) != bitSet2.get(i)) {
        distance++;
      }
      i++;
    }

    return distance;
  }

  @Override
  public String getName() {
    return "MOCHC";
  }

  @Override
  public String getDescription() {
    return "Multiobjective CHC algorithm";
  }
}
