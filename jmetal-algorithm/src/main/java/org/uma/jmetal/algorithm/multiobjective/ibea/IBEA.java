package org.uma.jmetal.algorithm.multiobjective.ibea;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the IBEA algorithm
 */
@SuppressWarnings("serial")
public class IBEA<S extends Solution<?>> implements Algorithm<List<S>> {
  protected Problem<S> problem;

  public static final int TOURNAMENTS_ROUNDS = 1;

  protected List<List<Double>> indicatorValues;
  protected double maxIndicatorValue;

  protected int populationSize;
  protected int archiveSize;
  protected int maxEvaluations;

  protected List<S> archive;

  protected CrossoverOperator<S> crossoverOperator;
  protected MutationOperator<S> mutationOperator;
  protected SelectionOperator<List<S>, S> selectionOperator;

  protected Fitness<S> solutionFitness = new Fitness<S>();

  /**
   * Constructor
   */
  public IBEA(Problem<S> problem, int populationSize, int archiveSize, int maxEvaluations,
      SelectionOperator<List<S>, S> selectionOperator, CrossoverOperator<S> crossoverOperator,
      MutationOperator<S> mutationOperator) {
    this.problem = problem;
    this.populationSize = populationSize;
    this.archiveSize = archiveSize;
    this.maxEvaluations = maxEvaluations;
    this.crossoverOperator = crossoverOperator;
    this.mutationOperator = mutationOperator;
    this.selectionOperator = selectionOperator;
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

  @Override public List<S> getResult() {
    return SolutionListUtils.getNondominatedSolutions(archive);
  }

  /**
   * Calculates the hypervolume of that portion of the objective space that
   * is dominated by individual a but not by individual b
   */
  double calculateHypervolumeIndicator(Solution<?> solutionA, Solution<?> solutionB, int d,
      double maximumValues[], double minimumValues[]) {
    double a, b, r, max;
    double volume ;
    double rho = 2.0;

    r = rho * (maximumValues[d - 1] - minimumValues[d - 1]);
    max = minimumValues[d - 1] + r;

    a = solutionA.getObjective(d - 1);
    if (solutionB == null) {
      b = max;
    } else {
      b = solutionB.getObjective(d - 1);
    }

    if (d == 1) {
      if (a < b) {
        volume = (b - a) / r;
      } else {
        volume = 0;
      }
    } else {
      if (a < b) {
        volume =
            calculateHypervolumeIndicator(solutionA, null, d - 1, maximumValues, minimumValues) * (b
                - a) / r;
        volume +=
            calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues)
                * (max - b) / r;
      } else {
        volume =
            calculateHypervolumeIndicator(solutionA, solutionB, d - 1, maximumValues, minimumValues)
                * (max - a) / r;
      }
    }

    return (volume);
  }

  /**
   * This structure stores the indicator values of each pair of elements
   */
  public void computeIndicatorValuesHD(List<S> solutionSet, double[] maximumValues,
      double[] minimumValues) {
    List<S> A, B;
    // Initialize the structures
    indicatorValues = new ArrayList<List<Double>>();
    maxIndicatorValue = -Double.MAX_VALUE;

    for (int j = 0; j < solutionSet.size(); j++) {
      A = new ArrayList<>(1);
      A.add(solutionSet.get(j));

      List<Double> aux = new ArrayList<Double>();
      for (S solution : solutionSet) {
        B = new ArrayList<>(1);
        B.add(solution);

        int flag = (new DominanceComparator<S>()).compare(A.get(0), B.get(0));

        double value;
        if (flag == -1) {
          value =
              -calculateHypervolumeIndicator(A.get(0), B.get(0), problem.getNumberOfObjectives(),
                  maximumValues, minimumValues);
        } else {
          value = calculateHypervolumeIndicator(B.get(0), A.get(0), problem.getNumberOfObjectives(),
              maximumValues, minimumValues);
        }

        //Update the max value of the indicator
        if (Math.abs(value) > maxIndicatorValue) {
          maxIndicatorValue = Math.abs(value);
        }
        aux.add(value);
      }
      indicatorValues.add(aux);
    }
  }

  /**
   * Calculate the fitness for the individual at position pos
   */
  public void fitness(List<S> solutionSet, int pos) {
    double fitness = 0.0;
    double kappa = 0.05;

    for (int i = 0; i < solutionSet.size(); i++) {
      if (i != pos) {
        fitness += Math.exp((-1 * indicatorValues.get(i).get(pos) / maxIndicatorValue) / kappa);
      }
    }
    solutionFitness.setAttribute(solutionSet.get(pos), fitness);
  }

  /**
   * Calculate the fitness for the entire population.
   */
  public void calculateFitness(List<S> solutionSet) {
    // Obtains the lower and upper bounds of the population
    double[] maximumValues = new double[problem.getNumberOfObjectives()];
    double[] minimumValues = new double[problem.getNumberOfObjectives()];

    for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
      maximumValues[i] = -Double.MAX_VALUE;
      minimumValues[i] = Double.MAX_VALUE;
    }

    for (S solution : solutionSet) {
      for (int obj = 0; obj < problem.getNumberOfObjectives(); obj++) {
        double value = solution.getObjective(obj);
        if (value > maximumValues[obj]) {
          maximumValues[obj] = value;
        }
        if (value < minimumValues[obj]) {
          minimumValues[obj] = value;
        }
      }
    }

    computeIndicatorValuesHD(solutionSet, maximumValues, minimumValues);
    for (int pos = 0; pos < solutionSet.size(); pos++) {
      fitness(solutionSet, pos);
    }
  }

  /**
   * Update the fitness before removing an individual
   */
  public void removeWorst(List<S> solutionSet) {

    // Find the worst;
    double worst = (double) solutionFitness.getAttribute(solutionSet.get(0));
    int worstIndex = 0;
    double kappa = 0.05;

    for (int i = 1; i < solutionSet.size(); i++) {
      if ((double) solutionFitness.getAttribute(solutionSet.get(i)) > worst) {
        worst = (double) solutionFitness.getAttribute(solutionSet.get(i));
        worstIndex = i;
      }
    }

    // Update the population
    for (int i = 0; i < solutionSet.size(); i++) {
      if (i != worstIndex) {
        double fitness = (double) solutionFitness.getAttribute(solutionSet.get(i));
        fitness -= Math.exp((-indicatorValues.get(worstIndex).get(i) / maxIndicatorValue) / kappa);
        solutionFitness.setAttribute(solutionSet.get(i), fitness);
      }
    }

    // remove worst from the indicatorValues list
    indicatorValues.remove(worstIndex);
    for (List<Double> anIndicatorValues_ : indicatorValues) {
      anIndicatorValues_.remove(worstIndex);
    }

    solutionSet.remove(worstIndex);
  }

  @Override public String getName() {
    return "IBEA" ;
  }

  @Override public String getDescription() {
    return "Indicator based Evolutionary Algorithm" ;
  }
}
