package org.uma.jmetal.algorithm.multiobjective.top;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.ConstraintHandling;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * ToP, the two-phase framework for constrained multi-objective optimization, with NSGA-II in its
 * second phase. Instances are created with {@link ToPBuilder}.
 *
 * <p>Reference: Z.-Z. Liu and Y. Wang, "Handling Constrained Multiobjective Optimization Problems
 * With Constraints in Both the Decision and Objective Spaces", IEEE Transactions on Evolutionary
 * Computation, 23(5):870-884, 2019. DOI: 10.1109/TEVC.2019.2894743.
 *
 * <p>This class reproduces the ToP of PlatEMO (its files {@code ToP.m}, {@code DE_cr.m}, {@code
 * DE_rb.m} and {@code EnvironmentalSelection.m}, and the functions {@code OperatorDE}, {@code
 * TournamentSelection}, {@code NDSort} and {@code CrowdingDistance} that they call). The overall
 * constraint violation of a solution, CV, is the absolute value of {@link
 * ConstraintHandling#overallConstraintViolationDegree} (a constraint value lower than zero is a
 * violation, as everywhere in jMetal), and a solution is feasible when its CV is zero. The run
 * starts with a random population of N solutions, delta = 1 and Pf, the ratio of feasible
 * solutions of the population, and each generation runs one of two phases:
 *
 * <ul>
 *   <li><b>Phase 1</b>, while (delta &ge; 0.2 or Pf &le; 1/3) and the evaluations done are not
 *       more than 90% of the maximum: the problem is solved as a constrained single-objective one,
 *       whose objective is the mean of the objectives. For each solution x<sub>i</sub>, with
 *       probability 0.5 a trial vector is created with DE/current-to-rand/1, x<sub>i</sub> +
 *       F(x<sub>r1</sub> - x<sub>i</sub>) + F(x<sub>r2</sub> - x<sub>r3</sub>), and otherwise with
 *       DE/rand-to-best/1/bin, x<sub>r1</sub> + F(x<sub>best</sub> - x<sub>r1</sub>) +
 *       F(x<sub>r2</sub> - x<sub>r3</sub>), where x<sub>best</sub> is the first solution with the
 *       lowest mean objective, feasible or not, and r1, r2 and r3 are random indices; the trial
 *       vector is clamped to the bounds and then mutated. Each trial vector replaces x<sub>i</sub>
 *       if its CV is smaller, or if both CVs are equal and its mean objective is smaller. Then Pf
 *       is updated and, if at least three solutions are feasible, delta is updated: the
 *       objectives of the feasible solutions are normalized with their minimum and maximum, each
 *       solution adds up its normalized objectives, and delta is the floor(n/3)-th smallest sum
 *       minus the smallest one, where n is the number of feasible solutions. With fewer than
 *       three feasible solutions delta keeps its value.
 *   <li><b>Phase 2</b>: NSGA-II with the constrained-domination principle and differential
 *       evolution. The population is sorted into fronts and the crowding distances of each front
 *       are computed; each of the N offspring is x<sub>1</sub> + F(x<sub>2</sub> -
 *       x<sub>3</sub>), clamped to the bounds and mutated, where x<sub>1</sub> is chosen by a
 *       binary tournament on the front number and the crowding distance and x<sub>2</sub> and
 *       x<sub>3</sub> are random; the next population is the best N solutions of the union of the
 *       population and the offspring by front number and, in the last front, by crowding
 *       distance.
 * </ul>
 *
 * <p>The variation is DE with a binomial crossover of rate CR (1.0 by default) and scale factor F
 * (0.5 by default), followed by a mutation operator (by default, the polynomial mutation with
 * probability 1/n and distribution index 20), as in PlatEMO. The run ends when the number of
 * evaluations reaches the maximum; as every generation evaluates N solutions, it can exceed the
 * maximum when the maximum is not a multiple of N. The phase is decided at the beginning of each
 * generation, and as delta and Pf only change in phase 1 and the evaluations only grow, once
 * phase 2 starts it lasts until the end. The result is the final population, which can contain
 * infeasible solutions.
 *
 * <p>Where the paper and PlatEMO differ, this class follows PlatEMO:
 *
 * <ul>
 *   <li>The random indices r1, r2 and r3 of phase 1 are drawn with replacement: they can be equal
 *       to each other and to i. The paper requires them to be mutually different and different
 *       from i.
 *   <li>F and CR are fixed (0.5 and 1.0). The paper draws them, for each trial vector, from the
 *       pools {0.6, 0.8, 1.0} and {0.1, 0.2, 1.0}.
 *   <li>The binomial crossover has no j<sub>rand</sub> (with CR &lt; 1 a trial vector can be a copy
 *       of its base vector), and the variables of DE/rand-to-best/1/bin that are not crossed are
 *       those of x<sub>r1</sub>, not those of x<sub>i</sub> as in Eq. (16) of the paper. Neither
 *       difference has any effect with CR = 1.
 *   <li>The trial vectors of phase 1 are clamped to the bounds and mutated with the polynomial
 *       mutation; the paper does not mutate them.
 *   <li>The objectives in delta are normalized with the minimum and maximum of the feasible
 *       solutions of the current population, not of all the feasible solutions found during the
 *       run as in the paper; delta keeps its value when fewer than three solutions are feasible;
 *       and phase 1 also ends when the evaluations done are more than 90% of the maximum, which
 *       the paper does not mention.
 *   <li>The mean of the objectives replaces their sum (Eq. (13) of the paper), which gives the
 *       same comparisons. When the two CVs are equal, the replacement compares the mean
 *       objectives also if both solutions are infeasible, and the trial vector replaces the
 *       solution only if its mean objective is strictly smaller.
 *   <li>Phase 2 creates the offspring with DE/rand/1/bin and the polynomial mutation, the first
 *       parent chosen by a binary tournament and the other two at random. In the paper, the
 *       second phase of ToP-NSGA-II-CDP uses the SBX crossover and the polynomial mutation of
 *       NSGA-II-CDP.
 * </ul>
 *
 * <p>The details of PlatEMO are reproduced, except for the random numbers:
 *
 * <ul>
 *   <li>The non-dominated sorting, jMetal's {@code FastNonDominatedSortRanking} with the
 *       constrained dominance of PlatEMO, puts an infeasible solution behind every feasible one
 *       and behind every solution with a smaller CV, and in the same front as the solutions with
 *       the same CV. PlatEMO does that by shifting the objectives of the infeasible solutions,
 *       which can, in addition, merge fronts when a CV is too small to change the shifted value
 *       in floating point; that rounding is not reproduced. jMetal's {@code
 *       DominanceWithConstraintsComparator} is not used because it compares by Pareto dominance
 *       two infeasible solutions with the same CV.
 *   <li>The crowding distance sorts each front by each objective from the order of the
 *       population, keeping the order of ties, and an objective whose values are all equal in a
 *       front gives NaN distances to its inner solutions (zero divided by zero) and infinite ones
 *       to its extremes. The truncation of the last front takes the NaN distances first and the
 *       binary tournament takes them as the worst ones, as MATLAB sorts NaN values. jMetal's
 *       {@code CrowdingDistanceDensityEstimator} is not used because it sorts each objective from
 *       the order of the previous one and skips the objectives with a single value.
 *   <li>Ties are broken by the position in the population: the best solution of phase 1 is the
 *       first one with the lowest mean objective, the tournament and the truncation prefer the
 *       first solution, and the selected solutions keep the order of the union of the population
 *       and the offspring.
 *   <li>When the objectives of the feasible solutions have a zero range, their normalization
 *       divides zero by zero and delta is NaN, so that the test delta &ge; 0.2 fails.
 *   <li>jMetal's {@code DifferentialEvolutionCrossover} is not used: its {@code
 *       RAND_TO_BEST_1_BIN} variant is DE/current-to-best/1, whose base vector is the current
 *       solution, not x<sub>r1</sub>, and its binomial crossover always takes one variable from
 *       the mutant vector (j<sub>rand</sub>), which PlatEMO does not.
 *   <li>The default mutation, jMetal's {@code PolynomialMutation}, applies the formula of
 *       PlatEMO; it mutates a variable when a uniform draw is not greater than the probability,
 *       while PlatEMO requires it to be lower, which only differs for a draw equal to it.
 * </ul>
 */
@SuppressWarnings("serial")
public class ToP implements Algorithm<List<DoubleSolution>> {
  /** Phase 1 goes on while delta is at least this value (or Pf is not above 1/3). */
  static final double DELTA_THRESHOLD = 0.2;

  /** Phase 1 goes on while Pf is not above this value (or delta is at least 0.2). */
  static final double FEASIBLE_RATIO_THRESHOLD = 1.0 / 3.0;

  /** Phase 1 ends when the evaluations done exceed this fraction of the maximum. */
  static final double PHASE_ONE_EVALUATION_FRACTION = 0.9;

  /** Probability of DE/current-to-rand/1 (otherwise, DE/rand-to-best/1/bin) in phase 1. */
  static final double CURRENT_TO_RAND_PROBABILITY = 0.5;

  /** Value of delta at the beginning of the run. */
  static final double INITIAL_DELTA = 1.0;

  private final Problem<DoubleSolution> problem;
  private final int populationSize;
  private final int maxEvaluations;
  private final ToPDifferentialEvolution differentialEvolution;
  private final SolutionListEvaluator<DoubleSolution> evaluator;

  private List<DoubleSolution> population = new ArrayList<>();
  private int evaluations;
  private double delta = INITIAL_DELTA;
  private double feasibleRatio;
  private int phaseTwoStart = -1;

  /**
   * Creates the algorithm with the parameters of a builder, which has checked them.
   *
   * @param builder the builder
   */
  ToP(ToPBuilder builder) {
    this.problem = builder.getProblem();
    this.populationSize = builder.getPopulationSize();
    this.maxEvaluations = builder.getMaxEvaluations();
    this.evaluator = builder.getSolutionListEvaluator();
    this.differentialEvolution =
        new ToPDifferentialEvolution(
            builder.getCr(),
            builder.getF(),
            builder.getMutationOperator(),
            () -> JMetalRandom.getInstance().nextDouble());
  }

  @Override
  public void run() {
    population = createInitialPopulation();
    population = evaluator.evaluate(population, problem);
    evaluations = populationSize;
    feasibleRatio = feasibleRatio(population);
    delta = INITIAL_DELTA;
    phaseTwoStart = -1;

    while (evaluations < maxEvaluations) {
      if (phaseOneContinues(delta, feasibleRatio, evaluations, maxEvaluations)) {
        phaseOneGeneration();
      } else {
        if (phaseTwoStart < 0) {
          phaseTwoStart = evaluations;
        }
        phaseTwoGeneration();
      }
      evaluations += populationSize;
    }
  }

  /**
   * Returns the final population (an empty list before the run).
   *
   * @return a copy of the population
   */
  @Override
  public List<DoubleSolution> result() {
    return new ArrayList<>(population);
  }

  @Override
  public String name() {
    return "ToP";
  }

  @Override
  public String description() {
    return "Two-phase framework (ToP) with NSGA-II for constrained multi-objective optimization";
  }

  /**
   * Returns the number of evaluations done so far.
   *
   * @return the number of evaluations
   */
  public int evaluations() {
    return evaluations;
  }

  /**
   * Returns the number of evaluations done when phase 2 started, if it did.
   *
   * @return the evaluations at the start of phase 2, or an empty value if the run did not reach it
   */
  public OptionalInt phaseTwoStart() {
    return phaseTwoStart < 0 ? OptionalInt.empty() : OptionalInt.of(phaseTwoStart);
  }

  private List<DoubleSolution> createInitialPopulation() {
    List<DoubleSolution> initialPopulation = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      initialPopulation.add(problem.createSolution());
    }
    return initialPopulation;
  }

  /** Phase 1: constrained single-objective DE on the mean of the objectives. */
  private void phaseOneGeneration() {
    int best = indexOfLowestMeanObjective(population);

    List<DoubleSolution> offspring = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      if (JMetalRandom.getInstance().nextDouble() < CURRENT_TO_RAND_PROBABILITY) {
        offspring.add(
            differentialEvolution.currentToRand1(
                population.get(i), randomMember(), randomMember(), randomMember()));
      } else {
        offspring.add(
            differentialEvolution.randToBest1(
                population.get(best), randomMember(), randomMember(), randomMember()));
      }
    }
    offspring = evaluator.evaluate(offspring, problem);

    population = oneToOneReplacement(population, offspring);
    feasibleRatio = feasibleRatio(population);
    delta = updatedDelta(population, delta);
  }

  /** Phase 2: NSGA-II with the constrained-domination principle and DE. */
  private void phaseTwoGeneration() {
    int[] frontNumbers = ToPSelection.frontNumbers(population);
    double[] crowdingDistances = ToPSelection.crowdingDistances(population, frontNumbers);

    List<DoubleSolution> offspring = new ArrayList<>(populationSize);
    for (int i = 0; i < populationSize; i++) {
      int parent =
          ToPSelection.binaryTournament(
              randomIndex(), randomIndex(), frontNumbers, crowdingDistances);
      offspring.add(
          differentialEvolution.rand1(population.get(parent), randomMember(), randomMember()));
    }
    offspring = evaluator.evaluate(offspring, problem);

    List<DoubleSolution> union = new ArrayList<>(population);
    union.addAll(offspring);
    population = ToPSelection.select(union, populationSize);
  }

  private int randomIndex() {
    return JMetalRandom.getInstance().nextInt(0, populationSize - 1);
  }

  private DoubleSolution randomMember() {
    return population.get(randomIndex());
  }

  /**
   * Returns the overall constraint violation of a solution, a non-negative value that is zero if
   * and only if the solution is feasible.
   *
   * @param solution the solution
   * @return the absolute value of its overall constraint violation degree
   */
  static double constraintViolation(DoubleSolution solution) {
    return Math.abs(ConstraintHandling.overallConstraintViolationDegree(solution));
  }

  /**
   * Returns the mean of the objectives of a solution, the objective of phase 1.
   *
   * @param solution the solution
   * @return the sum of its objectives divided by their number
   */
  static double meanObjective(DoubleSolution solution) {
    double[] objectives = solution.objectives();
    double sum = 0.0;
    for (double objective : objectives) {
      sum += objective;
    }
    return sum / objectives.length;
  }

  /**
   * Returns the index of the first solution with the lowest mean objective, feasible or not.
   *
   * @param solutions a non-empty list of solutions
   * @return the index of the best solution of phase 1
   */
  static int indexOfLowestMeanObjective(List<DoubleSolution> solutions) {
    int best = 0;
    double lowest = meanObjective(solutions.get(0));
    for (int i = 1; i < solutions.size(); i++) {
      double value = meanObjective(solutions.get(i));
      if (value < lowest) {
        lowest = value;
        best = i;
      }
    }
    return best;
  }

  /**
   * Tells whether a trial vector replaces its parent in phase 1: if its overall constraint
   * violation is smaller, or if both are equal and its mean objective is smaller.
   *
   * @param offspring the trial vector, evaluated
   * @param parent the solution of the population at the same position
   * @return true if the offspring replaces the parent
   */
  static boolean offspringReplacesParent(DoubleSolution offspring, DoubleSolution parent) {
    double offspringViolation = constraintViolation(offspring);
    double parentViolation = constraintViolation(parent);
    return offspringViolation < parentViolation
        || (offspringViolation == parentViolation
            && meanObjective(offspring) < meanObjective(parent));
  }

  /**
   * The one-to-one replacement of phase 1: each offspring is compared with the solution at its
   * position.
   *
   * @param population the population
   * @param offspring the evaluated offspring, one per solution of the population
   * @return a new list with the next population
   */
  static List<DoubleSolution> oneToOneReplacement(
      List<DoubleSolution> population, List<DoubleSolution> offspring) {
    List<DoubleSolution> nextPopulation = new ArrayList<>(population.size());
    for (int i = 0; i < population.size(); i++) {
      nextPopulation.add(
          offspringReplacesParent(offspring.get(i), population.get(i))
              ? offspring.get(i)
              : population.get(i));
    }
    return nextPopulation;
  }

  /**
   * Returns Pf, the ratio of feasible solutions (those with a zero overall constraint violation).
   *
   * @param solutions a non-empty list of solutions
   * @return the number of feasible solutions divided by the number of solutions
   */
  static double feasibleRatio(List<DoubleSolution> solutions) {
    long feasible = solutions.stream().filter(s -> constraintViolation(s) == 0.0).count();
    return (double) feasible / solutions.size();
  }

  /**
   * Returns the value of delta after a generation of phase 1. The objectives of the n feasible
   * solutions are normalized with their minimum and maximum, the normalized objectives of each
   * solution are added up, and delta is the floor(n/3)-th smallest sum minus the smallest one. If
   * fewer than three solutions are feasible, delta keeps its value; if an objective has the same
   * value in all of them, the normalization gives NaN, and so does delta.
   *
   * @param population the population
   * @param delta the current value of delta
   * @return the new value of delta
   */
  static double updatedDelta(List<DoubleSolution> population, double delta) {
    List<double[]> feasible =
        population.stream()
            .filter(s -> constraintViolation(s) == 0.0)
            .map(DoubleSolution::objectives)
            .toList();
    if (feasible.size() < 3) {
      return delta;
    }

    int numberOfObjectives = feasible.get(0).length;
    double[] minimum = new double[numberOfObjectives];
    double[] maximum = new double[numberOfObjectives];
    Arrays.fill(minimum, Double.POSITIVE_INFINITY);
    Arrays.fill(maximum, Double.NEGATIVE_INFINITY);
    for (double[] objectives : feasible) {
      for (int j = 0; j < numberOfObjectives; j++) {
        minimum[j] = Math.min(minimum[j], objectives[j]);
        maximum[j] = Math.max(maximum[j], objectives[j]);
      }
    }

    double[] sums = new double[feasible.size()];
    for (int k = 0; k < feasible.size(); k++) {
      double[] objectives = feasible.get(k);
      for (int j = 0; j < numberOfObjectives; j++) {
        sums[k] += (objectives[j] - minimum[j]) / (maximum[j] - minimum[j]);
      }
    }
    Arrays.sort(sums);

    return sums[sums.length / 3 - 1] - sums[0];
  }

  /**
   * Tells whether a generation belongs to phase 1: (delta &ge; 0.2 or Pf &le; 1/3) and the
   * evaluations done are not more than 90% of the maximum.
   *
   * @param delta the current value of delta
   * @param feasibleRatio the current ratio of feasible solutions
   * @param evaluations the evaluations done
   * @param maxEvaluations the maximum number of evaluations
   * @return true for phase 1, false for phase 2
   */
  static boolean phaseOneContinues(
      double delta, double feasibleRatio, int evaluations, int maxEvaluations) {
    return (delta >= DELTA_THRESHOLD || feasibleRatio <= FEASIBLE_RATIO_THRESHOLD)
        && evaluations <= PHASE_ONE_EVALUATION_FRACTION * maxEvaluations;
  }
}
