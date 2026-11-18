package org.uma.jmetal.algorithm.multiobjective.cmoeams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover.DE_MUTATION_TYPE;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * CMOEA-MS, the constrained multi-objective evolutionary algorithm with multiple stages of Y.
 * Tian, Y. Zhang, Y. Su, X. Zhang, K. C. Tan and Y. Jin, "Balancing Objective Optimization and
 * Constraint Satisfaction in Constrained Evolutionary Multiobjective Optimization", IEEE
 * Transactions on Cybernetics 52(9):9559-9572, 2022 (https://doi.org/10.1109/TCYB.2020.3021138).
 *
 * <p>The algorithm evolves one population of size N. At each generation, N parents are chosen by
 * binary tournaments on the fitness, the offspring are joined to the population (the union Q) and
 * the fitness of Q is computed in one of two stages:
 *
 * <ul>
 *   <li>Stage A gives the objectives the same priority as the constraints: the fitness is that of
 *       the two objectives [SDE, CV], the shift-based density estimation and the constraint
 *       violation, compared by Pareto dominance.
 *   <li>Stage B gives the objectives a lower priority than the constraints: the fitness is that of
 *       the objectives compared by the constrained dominance (a lower constraint violation
 *       dominates; equal violations are compared by Pareto dominance).
 * </ul>
 *
 * <p>The fitness is the one of SPEA2: the sum of the strengths of the dominating solutions plus a
 * density term lower than 1, so the non-dominated solutions have a fitness lower than 1. The
 * environmental selection keeps the solutions with a fitness lower than 1, completing them with
 * those of lowest fitness or truncating them by distance. The fitness of the initial population is
 * that of stage A. The result is the final population, which may contain infeasible and dominated
 * solutions.
 *
 * <p>This class follows the authors' code in PlatEMO ({@code CMOEAMS.m}, {@code CalFitness.m},
 * {@code CalSDE.m} and {@code EnvironmentalSelection.m}), which differs from the paper in the
 * following details:
 *
 * <ul>
 *   <li>Stage B is used when the ratio of feasible solutions of Q is greater than {@code lambda}
 *       and at least 10% of the evaluations have been done; the paper (Algorithm 1) uses stage A
 *       when the ratio is smaller than {@code lambda} and has no condition on the evaluations.
 *   <li>The constraint violation (CV) of a solution is the mean, over the constraints, of its
 *       violation of each constraint divided by the maximum violation of that constraint in Q (a
 *       constraint that no solution violates contributes zero); the paper uses the sum of the
 *       violations (Eq. 5).
 *   <li>The SDE objective of stage A is {@code 1 / (Dk + 2)}, where {@code Dk} is the distance of
 *       the solution to the (floor(sqrt(|Q|)) + 1)-th nearest solution of Q shifted to it
 *       (counting itself), on objectives normalized to [0, 1] in Q; the paper (Eq. 4) uses the
 *       distance to the nearest shifted solution without normalization.
 *   <li>The density term of the fitness, {@code 1 / (d + 2)}, and the truncation use the cosine
 *       distance; the paper uses the Euclidean distance. The truncation uses the objectives also
 *       in stage A, where the fitness is computed on [SDE, CV].
 * </ul>
 *
 * <p>The tournaments use the fitness computed on Q in the previous generation (the fitness is not
 * recomputed on the selected population), and their ties are broken as in PlatEMO. The divisions
 * by zero of PlatEMO (an objective with the same value in all of Q, a zero objective vector in the
 * cosine distance) give NaN values that are propagated and sorted as in MATLAB.
 *
 * <p>The variation follows PlatEMO's parameter {@code type}:
 *
 * <ul>
 *   <li>Type 1 (genetic operators, the default): the crossover must take two parents and return two
 *       children, as {@code OperatorGA} of PlatEMO. The N parents of the mating pool are paired as
 *       in PlatEMO (parent i with parent i + N/2), and give 2 floor(N/2) children, so an odd N
 *       gives N - 1 children. The paper and PlatEMO use simulated binary crossover (probability 1,
 *       distribution index 20) and polynomial mutation (probability 1/n, distribution index 20);
 *       jMetal's operators are not the same as PlatEMO's (e.g., jMetal's SBX is Deb's bounded
 *       version, PlatEMO's is the unbounded one followed by clamping to the bounds).
 *   <li>Type 2 (differential evolution, only for {@link DoubleSolution}): it is used when the
 *       crossover is a {@link DifferentialEvolutionCrossover} of variant {@code RAND_1_BIN} or
 *       {@code RAND_1_EXP}. As {@code OperatorDE} in PlatEMO, the child of the i-th solution of the
 *       population is {@code x_i + F (x_r1 - x_r2)}, where {@code r1} and {@code r2} are chosen by
 *       two independent binary tournaments, followed by the mutation. PlatEMO uses CR = 1 and F =
 *       0.5, with polynomial mutation.
 * </ul>
 *
 * <p>The algorithm stops when the number of evaluations reaches the maximum, which is checked
 * before each generation as in PlatEMO, so the last generation can exceed the maximum. A problem
 * without constraints has a zero constraint violation in all the solutions.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class CMOEAMS<S extends Solution<?>> implements Algorithm<List<S>> {
  private final Problem<S> problem;
  private final int populationSize;
  private final int maxEvaluations;
  private final double lambda;
  private final CrossoverOperator<S> crossover;
  private final MutationOperator<S> mutation;
  private final SolutionListEvaluator<S> evaluator;
  private final BoundedRandomGenerator<Integer> randomGenerator =
      (lowerBound, upperBound) -> JMetalRandom.getInstance().nextInt(lowerBound, upperBound);

  private List<S> population;
  private double[] fitness;
  private int evaluations;

  /**
   * Creates the algorithm.
   *
   * @param problem the problem to solve
   * @param populationSize the size of the population, at least 2
   * @param maxEvaluations the maximum number of evaluations
   * @param lambda the ratio of feasible solutions of the union of the population and the offspring
   *     above which the algorithm is in stage B, in [0, 1]
   * @param crossover the crossover, with two parents and two children, or a {@link
   *     DifferentialEvolutionCrossover} of variant {@code RAND_1_BIN} or {@code RAND_1_EXP}
   * @param mutation the mutation
   * @param evaluator the evaluator of the solutions
   */
  public CMOEAMS(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      double lambda,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      SolutionListEvaluator<S> evaluator) {
    Check.notNull(problem, "problem");
    Check.notNull(crossover, "crossover");
    Check.notNull(mutation, "mutation");
    Check.notNull(evaluator, "evaluator");
    checkPopulationSize(populationSize);
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    checkLambda(lambda);
    checkCrossover(crossover);

    this.problem = problem;
    this.populationSize = populationSize;
    this.maxEvaluations = maxEvaluations;
    this.lambda = lambda;
    this.crossover = crossover;
    this.mutation = mutation;
    this.evaluator = evaluator;
  }

  @Override
  public void run() {
    population = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      population.add(problem.createSolution());
    }
    population = evaluator.evaluate(population, problem);
    evaluations = population.size();
    fitness =
        CMOEAMSFitness.stageAFitness(
            objectives(population), CMOEAMSFitness.constraintViolation(constraints(population)));

    while (evaluations < maxEvaluations) {
      List<S> offspring = reproduction();
      offspring = evaluator.evaluate(offspring, problem);
      evaluations += offspring.size();
      replacement(offspring);
    }
  }

  @Override
  public List<S> result() {
    return population;
  }

  @Override
  public String name() {
    return "CMOEA-MS";
  }

  @Override
  public String description() {
    return "Constrained multi-objective evolutionary algorithm with multiple stages";
  }

  /** Returns the number of evaluations done. */
  public int evaluations() {
    return evaluations;
  }

  /**
   * Returns whether the algorithm is in stage B, as {@code CMOEAMS.m}: the ratio of feasible
   * solutions (those with a zero constraint violation) must be greater than {@code lambda}, and the
   * evaluations done must be at least 10% of the maximum.
   *
   * @param constraintViolation the constraint violation of the union of the population and the
   *     offspring
   * @param lambda the threshold of the ratio of feasible solutions
   * @param evaluations the evaluations done, including those of the offspring
   * @param maxEvaluations the maximum number of evaluations
   * @return true in stage B, false in stage A
   */
  static boolean isStageB(
      double[] constraintViolation, double lambda, int evaluations, int maxEvaluations) {
    long feasible = Arrays.stream(constraintViolation).filter(value -> value <= 0.0).count();
    double ratio = (double) feasible / constraintViolation.length;
    return ratio > lambda && evaluations >= 0.1 * maxEvaluations;
  }

  /** Returns the fitness of the population, aligned with it. */
  double[] fitness() {
    return fitness;
  }

  static void checkPopulationSize(int populationSize) {
    Check.that(
        populationSize >= 2, () -> "The population size must be at least 2: " + populationSize);
  }

  static void checkLambda(double lambda) {
    Check.that(lambda >= 0.0 && lambda <= 1.0, () -> "lambda must be in [0, 1]: " + lambda);
  }

  static void checkCrossover(CrossoverOperator<?> crossover) {
    if (crossover instanceof DifferentialEvolutionCrossover differentialEvolution) {
      Check.that(
          differentialEvolution.getMutationType() == DE_MUTATION_TYPE.RAND
              && differentialEvolution.getNumberOfDifferenceVectors() == 1,
          () ->
              "The differential evolution must be RAND_1_BIN or RAND_1_EXP: "
                  + differentialEvolution.getVariant());
    } else {
      Check.that(
          crossover.numberOfRequiredParents() == 2 && crossover.numberOfGeneratedChildren() == 2,
          () ->
              "The crossover must take two parents and return two children: "
                  + crossover.numberOfRequiredParents()
                  + " parents, "
                  + crossover.numberOfGeneratedChildren()
                  + " children");
    }
  }

  private List<S> reproduction() {
    return crossover instanceof DifferentialEvolutionCrossover differentialEvolution
        ? differentialEvolutionReproduction(differentialEvolution)
        : geneticReproduction();
  }

  /** Creates the offspring as {@code OperatorGA} of PlatEMO on a mating pool of N parents. */
  private List<S> geneticReproduction() {
    int[] matingPool =
        CMOEAMSSelection.tournamentSelection(fitness, populationSize, randomGenerator);
    int half = populationSize / 2;
    List<S> offspring = new ArrayList<>(Collections.nCopies(2 * half, null));
    for (int i = 0; i < half; i++) {
      List<S> parents =
          List.of(population.get(matingPool[i]), population.get(matingPool[i + half]));
      List<S> children = crossover.execute(parents);
      mutation.execute(children.get(0));
      mutation.execute(children.get(1));
      offspring.set(i, children.get(0));
      offspring.set(i + half, children.get(1));
    }
    return offspring;
  }

  /**
   * Creates the offspring as {@code OperatorDE(Problem, Population, Population(Mat1),
   * Population(Mat2))} of PlatEMO: the child of solution i is {@code x_i + F (x_r1 - x_r2)}.
   */
  private List<S> differentialEvolutionReproduction(
      DifferentialEvolutionCrossover differentialEvolution) {
    int[] first = CMOEAMSSelection.tournamentSelection(fitness, populationSize, randomGenerator);
    int[] second = CMOEAMSSelection.tournamentSelection(fitness, populationSize, randomGenerator);
    List<S> offspring = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      S current = population.get(i);
      differentialEvolution.setCurrentSolution((DoubleSolution) current);
      // The variant RAND_1 computes parent 3 + F (parent 1 - parent 2)
      List<S> parents = List.of(population.get(first[i]), population.get(second[i]), current);
      S child = crossover.execute(parents).get(0);
      mutation.execute(child);
      offspring.add(child);
    }
    return offspring;
  }

  private void replacement(List<S> offspring) {
    List<S> union = new ArrayList<>(population.size() + offspring.size());
    union.addAll(population);
    union.addAll(offspring);

    double[][] objectives = objectives(union);
    double[] constraintViolation = CMOEAMSFitness.constraintViolation(constraints(union));
    double[] unionFitness =
        isStageB(constraintViolation, lambda, evaluations, maxEvaluations)
            ? CMOEAMSFitness.stageBFitness(objectives, constraintViolation)
            : CMOEAMSFitness.stageAFitness(objectives, constraintViolation);

    int[] selected =
        CMOEAMSSelection.environmentalSelection(unionFitness, objectives, populationSize);
    List<S> nextPopulation = new ArrayList<>(selected.length);
    double[] nextFitness = new double[selected.length];
    for (int k = 0; k < selected.length; k++) {
      nextPopulation.add(union.get(selected[k]));
      nextFitness[k] = unionFitness[selected[k]];
    }
    population = nextPopulation;
    fitness = nextFitness;
  }

  private static double[][] objectives(List<? extends Solution<?>> solutions) {
    return solutions.stream().map(Solution::objectives).toArray(double[][]::new);
  }

  private static double[][] constraints(List<? extends Solution<?>> solutions) {
    return solutions.stream().map(Solution::constraints).toArray(double[][]::new);
  }
}
