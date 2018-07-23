package org.uma.jmetal.algorithm.multiobjective.moead;

import org.uma.jmetal.algorithm.multiobjective.moead.util.MOEADUtils;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.crossover.DifferentialEvolutionCrossover;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Class implementing the MOEA/D-STM algorithm described in : K. Li, Q. Zhang, S. Kwong, M. Li and
 * R. Wang, "Stable Matching-Based Selection in Evolutionary Multiobjective Optimization", IEEE
 * Transactions on Evolutionary Computation, 18(6): 909-923, 2014. DOI: 10.1109/TEVC.2013.2293776
 *
 * @author Ke Li
 * @version 1.0
 */
@SuppressWarnings("serial")
public class MOEADSTM extends AbstractMOEAD<DoubleSolution> {

  protected DifferentialEvolutionCrossover differentialEvolutionCrossover;

  protected DoubleSolution[] savedValues;
  protected double[] utility;
  protected int[] frequency;

  JMetalRandom randomGenerator;

  public MOEADSTM(Problem<DoubleSolution> problem, int populationSize, int resultPopulationSize,
      int maxEvaluations,
      MutationOperator<DoubleSolution> mutation, CrossoverOperator<DoubleSolution> crossover,
      FunctionType functionType, String dataDirectory, double neighborhoodSelectionProbability,
      int maximumNumberOfReplacedSolutions, int neighborSize) {
    super(problem, populationSize, resultPopulationSize, maxEvaluations, crossover, mutation,
        functionType,
        dataDirectory, neighborhoodSelectionProbability, maximumNumberOfReplacedSolutions,
        neighborSize);

    differentialEvolutionCrossover = (DifferentialEvolutionCrossover) crossoverOperator;

    savedValues = new DoubleSolution[populationSize];
    utility = new double[populationSize];
    frequency = new int[populationSize];
    for (int i = 0; i < utility.length; i++) {
      utility[i] = 1.0;
      frequency[i] = 0;
    }

    randomGenerator = JMetalRandom.getInstance();
  }

  @Override
  public void run() {
    initializePopulation();
    initializeUniformWeight();
    initializeNeighborhood();
    idealPoint.update(population);
    nadirPoint.update(population);

    int generation = 0;
    evaluations = populationSize;
    do {
      int[] permutation = new int[populationSize];
      MOEADUtils.randomPermutation(permutation, populationSize);
      offspringPopulation.clear();

      for (int i = 0; i < populationSize; i++) {
        int subProblemId = permutation[i];
        frequency[subProblemId]++;

        NeighborType neighborType = chooseNeighborType();
        List<DoubleSolution> parents = parentSelection(subProblemId, neighborType);

        differentialEvolutionCrossover.setCurrentSolution(population.get(subProblemId));
        List<DoubleSolution> children = differentialEvolutionCrossover.execute(parents);

        DoubleSolution child = children.get(0);
        mutationOperator.execute(child);
        problem.evaluate(child);

        evaluations++;

        idealPoint.update(population);
        nadirPoint.update(population);
        updateNeighborhood(child, subProblemId, neighborType);

        offspringPopulation.add(child);
      }

      // Combine the parent and the current offspring populations
      jointPopulation.clear();
      jointPopulation.addAll(population);
      jointPopulation.addAll(offspringPopulation);

      // selection process
      stmSelection();

      generation++;
      if (generation % 30 == 0) {
        utilityFunction();
      }

    } while (evaluations < maxEvaluations);

  }

  protected void initializePopulation() {
    population = new ArrayList<>(populationSize);
    offspringPopulation = new ArrayList<>(populationSize);
    jointPopulation = new ArrayList<>(populationSize);

    for (int i = 0; i < populationSize; i++) {
      DoubleSolution newSolution = (DoubleSolution) problem.createSolution();

      problem.evaluate(newSolution);
      population.add(newSolution);
      savedValues[i] = (DoubleSolution) newSolution.copy();
    }
  }

  @Override
  public List<DoubleSolution> getResult() {
    return population;
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
    List<Integer> selected = new ArrayList<Integer>();
    List<Integer> candidate = new ArrayList<Integer>();

    for (int k = 0; k < problem.getNumberOfObjectives(); k++) {
      // WARNING! HERE YOU HAVE TO USE THE WEIGHT PROVIDED BY QINGFU Et AL
      // (NOT SORTED!!!!)
      selected.add(k);
    }

    for (int n = problem.getNumberOfObjectives(); n < populationSize; n++) {
      // set of unselected weights
      candidate.add(n);
    }

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

  /**
   * Select the next parent population, based on the stable matching criteria
   */
  public void stmSelection() {

    int[] idx = new int[populationSize];
    double[] nicheCount = new double[populationSize];

    int[][] solPref = new int[jointPopulation.size()][];
    double[][] solMatrix = new double[jointPopulation.size()][];
    double[][] distMatrix = new double[jointPopulation.size()][];
    double[][] fitnessMatrix = new double[jointPopulation.size()][];

    for (int i = 0; i < jointPopulation.size(); i++) {
      solPref[i] = new int[populationSize];
      solMatrix[i] = new double[populationSize];
      distMatrix[i] = new double[populationSize];
      fitnessMatrix[i] = new double[populationSize];
    }
    int[][] subpPref = new int[populationSize][];
    double[][] subpMatrix = new double[populationSize][];
    for (int i = 0; i < populationSize; i++) {
      subpPref[i] = new int[jointPopulation.size()];
      subpMatrix[i] = new double[jointPopulation.size()];
    }

    // Calculate the preference values of solution matrix
    for (int i = 0; i < jointPopulation.size(); i++) {
      int minIndex = 0;
      for (int j = 0; j < populationSize; j++) {
        fitnessMatrix[i][j] = fitnessFunction(jointPopulation.get(i), lambda[j]);
        distMatrix[i][j] = calculateDistance2(jointPopulation.get(i), lambda[j]);
        if (distMatrix[i][j] < distMatrix[i][minIndex]) {
          minIndex = j;
        }
      }
      nicheCount[minIndex] = nicheCount[minIndex] + 1;
    }

    // calculate the preference values of subproblem matrix and solution matrix
    for (int i = 0; i < jointPopulation.size(); i++) {
      for (int j = 0; j < populationSize; j++) {
        subpMatrix[j][i] = fitnessFunction(jointPopulation.get(i), lambda[j]);
        solMatrix[i][j] = distMatrix[i][j] + nicheCount[j];
      }
    }

    // sort the preference value matrix to get the preference rank matrix
    for (int i = 0; i < populationSize; i++) {
      for (int j = 0; j < jointPopulation.size(); j++) {
        subpPref[i][j] = j;
      }
      MOEADUtils.quickSort(subpMatrix[i], subpPref[i], 0, jointPopulation.size() - 1);
    }
    for (int i = 0; i < jointPopulation.size(); i++) {
      for (int j = 0; j < populationSize; j++) {
        solPref[i][j] = j;
      }
      MOEADUtils.quickSort(solMatrix[i], solPref[i], 0, populationSize - 1);
    }

    idx = stableMatching(subpPref, solPref, populationSize, jointPopulation.size());

    population.clear();
    for (int i = 0; i < populationSize; i++) {
      population.add(i, jointPopulation.get(idx[i]));
    }
  }

  /**
   * Return the stable matching between 'subproblems' and 'solutions' ('subproblems' propose first).
   * It is worth noting that the number of solutions is larger than that of the subproblems.
   */
  public int[] stableMatching(int[][] manPref, int[][] womanPref, int menSize, int womenSize) {

    // Indicates the mating status
    int[] statusMan = new int[menSize];
    int[] statusWoman = new int[womenSize];

    final int NOT_ENGAGED = -1;
    for (int i = 0; i < womenSize; i++) {
      statusWoman[i] = NOT_ENGAGED;
    }

    // List of men that are not currently engaged.
    LinkedList<Integer> freeMen = new LinkedList<Integer>();
    for (int i = 0; i < menSize; i++) {
      freeMen.add(i);
    }

    // next[i] is the next woman to whom i has not yet proposed.
    int[] next = new int[womenSize];

    while (!freeMen.isEmpty()) {
      int m = freeMen.remove();
      int w = manPref[m][next[m]];
      next[m]++;
      if (statusWoman[w] == NOT_ENGAGED) {
        statusMan[m] = w;
        statusWoman[w] = m;
      } else {
        int m1 = statusWoman[w];
        if (prefers(m, m1, womanPref[w], menSize)) {
          statusMan[m] = w;
          statusWoman[w] = m;
          freeMen.add(m1);
        } else {
          freeMen.add(m);
        }
      }
    }

    return statusMan;
  }

  /**
   * Returns true in case that a given woman prefers x to y.
   */
  public boolean prefers(int x, int y, int[] womanPref, int size) {

    for (int i = 0; i < size; i++) {
      int pref = womanPref[i];
      if (pref == x) {
        return true;
      }
      if (pref == y) {
        return false;
      }
    }
    // this should never happen.
    System.out.println("Error in womanPref list!");
    return false;
  }

  /**
   * Calculate the perpendicular distance between the solution and reference line
   */
  public double calculateDistance(DoubleSolution individual, double[] lambda) {
    double scale;
    double distance;

    double[] vecInd = new double[problem.getNumberOfObjectives()];
    double[] vecProj = new double[problem.getNumberOfObjectives()];

    // vecInd has been normalized to the range [0,1]
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      vecInd[i] = (individual.getObjective(i) - idealPoint.getValue(i)) /
          (nadirPoint.getValue(i) - idealPoint.getValue(i));
    }

    scale = innerproduct(vecInd, lambda) / innerproduct(lambda, lambda);
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      vecProj[i] = vecInd[i] - scale * lambda[i];
    }

    distance = norm_vector(vecProj);

    return distance;
  }

  /**
   * Calculate the perpendicular distance between the solution and reference line
   */
  public double calculateDistance2(DoubleSolution individual, double[] lambda) {

    double distance;
    double distanceSum = 0.0;

    double[] vecInd = new double[problem.getNumberOfObjectives()];
    double[] normalizedObj = new double[problem.getNumberOfObjectives()];

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      distanceSum += individual.getObjective(i);
    }
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      normalizedObj[i] = individual.getObjective(i) / distanceSum;
    }
    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      vecInd[i] = normalizedObj[i] - lambda[i];
    }

    distance = norm_vector(vecInd);

    return distance;
  }

  /**
   * Calculate the norm of the vector
   */
  public double norm_vector(double[] z) {
    double sum = 0;

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      sum += z[i] * z[i];
    }

    return Math.sqrt(sum);
  }

  /**
   * Calculate the dot product of two vectors
   */
  public double innerproduct(double[] vec1, double[] vec2) {
    double sum = 0;

    for (int i = 0; i < vec1.length; i++) {
      sum += vec1[i] * vec2[i];
    }

    return sum;
  }

  @Override
  public String getName() {
    return "MOEADSTM";
  }

  @Override
  public String getDescription() {
    return "Multi-Objective Evolutionary Algorithm based on Decomposition. Version with Stable Matching Model";
  }
}
