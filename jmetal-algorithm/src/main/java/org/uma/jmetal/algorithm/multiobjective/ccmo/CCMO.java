package org.uma.jmetal.algorithm.multiobjective.ccmo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * CCMO, the coevolutionary framework for constrained multi-objective optimization of Tian et al.
 * (Y. Tian, T. Zhang, J. Xiao, X. Zhang and Y. Jin, "A Coevolutionary Framework for Constrained
 * Multiobjective Optimization Problems", IEEE Transactions on Evolutionary Computation
 * 25(1):102-116, 2021, <a
 * href="https://doi.org/10.1109/TEVC.2020.3004012">doi:10.1109/TEVC.2020.3004012</a>).
 *
 * <p>CCMO evolves two populations of the same size N with the same optimizer: the first one solves
 * the original problem and the second one a helper problem, which is the original problem without
 * constraints. In each generation, each population creates its own offspring, and each population
 * selects its next generation from itself together with the offspring of both populations, so that
 * the two populations only share their offspring (a "weak cooperation"). The result is the first
 * population.
 *
 * <p>This class follows the implementation of CCMO in PlatEMO (files {@code CCMO.m}, {@code
 * CalFitness.m} and {@code EnvironmentalSelection.m}), in which the optimizer is SPEA2 with a
 * dominance relation in which the constraint violation comes first:
 *
 * <ul>
 *   <li><b>Fitness</b> (see {@code CCMOFitness}): a solution dominates another one if its
 *       constraint violation is smaller or, with the same violation, if it dominates it in the
 *       Pareto sense; the fitness is the raw fitness of SPEA2 (the sum of the strengths of the
 *       solutions that dominate a solution) plus the density {@code 1 / (d_k + 2)}, where {@code
 *       d_k} is the distance in the (not normalized) objective space to the k-th nearest neighbour,
 *       with k the floor of the square root of the number of solutions. The second population uses
 *       the same fitness with all the violations set to zero.
 *   <li><b>Mating selection</b> (see {@code CCMOTournamentSelection}): binary tournaments on the
 *       fitness, with ties won by the solution that comes first in the population.
 *   <li><b>Variation</b>: with {@link VariationType#GA} (the default, PlatEMO's {@code type = 1}),
 *       each population selects N parents, pairs the i-th of the first half with the i-th of the
 *       second half, and keeps the first child of the crossover of each pair after mutating it
 *       (PlatEMO's {@code OperatorGAhalf}), so each population creates N/2 offspring and a
 *       generation costs N evaluations. With {@link VariationType#DE} ({@code type = 2}, for
 *       {@link DoubleSolution} only), each population selects 2N parents and the i-th offspring is
 *       {@code x_i + F (r1_i - r2_i)}, where {@code x_i} is the i-th solution of the population and
 *       {@code r1_i}, {@code r2_i} are the i-th parents of each half, followed by the mutation
 *       (PlatEMO's {@code OperatorDE}); each population creates N offspring and a generation costs
 *       2N evaluations. The crossover must then be a {@link DifferentialEvolutionCrossover} with
 *       one difference vector and a random base vector ({@code RAND_1_BIN} or {@code RAND_1_EXP}),
 *       to which this class gives {@code (r1_i, r2_i, x_i)} as parents and {@code x_i} as the
 *       current solution; PlatEMO uses CR = 1 and F = 0.5.
 *   <li><b>Environmental selection</b> (see {@code CCMOEnvironmentalSelection}): the environmental
 *       selection of SPEA2 on the union of the population and both sets of offspring (in this
 *       order): the solutions with a fitness lower than 1, filled with the next best ones by
 *       fitness or reduced with the truncation of SPEA2, which removes one at a time the solution
 *       whose sorted distances to the remaining ones are the lexicographically smallest. The
 *       population is kept sorted by fitness, and its fitness (computed on the union) is the one
 *       of the next mating selection.
 * </ul>
 *
 * <p>Differences with the paper, where this class follows PlatEMO:
 *
 * <ul>
 *   <li>The paper embeds in CCMO the constrained NSGA-II, with the truncation of SPEA2 instead of
 *       the crowding distance in the environmental selection (Section IV-A); PlatEMO, and this
 *       class, use the fitness and the environmental selection of SPEA2, so the population is not
 *       filled front by front and the mating selection compares SPEA2 fitness values instead of
 *       ranks and crowding distances.
 *   <li>In the constrained dominance of the paper, two infeasible solutions with the same
 *       violation do not dominate each other; in PlatEMO they are compared by Pareto dominance.
 *   <li>Algorithm 1 of the paper selects N/2 parents to create N/2 offspring; PlatEMO selects N
 *       parents and keeps one child per pair. The DE variation is not in the paper.
 * </ul>
 *
 * <p>Differences with PlatEMO that come from jMetal:
 *
 * <ul>
 *   <li>The variation operators are the ones given to the constructor (for real variables, the
 *       runner uses SBX with probability 1.0 and distribution index 20, and polynomial mutation
 *       with probability 1/n and distribution index 20, as PlatEMO). The SBX of jMetal is the
 *       bounded version of Deb's code, while PlatEMO uses the unbounded formula and then repairs
 *       the values to the bounds. {@link DifferentialEvolutionCrossover} always takes the variable
 *       of a random position from the mutant vector, which only matters when CR is lower than 1.
 *   <li>The constraint violation follows the jMetal convention (a constraint value lower than zero
 *       is violated): it is the absolute value of the overall constraint violation degree of {@link
 *       org.uma.jmetal.util.ConstraintHandling}, which is the sum of the violations that PlatEMO
 *       computes, or the value precomputed by the problem.
 *   <li>The offspring are evaluated by a {@link SolutionListEvaluator}. As in PlatEMO, the
 *       algorithm stops when the number of evaluations reaches the maximum, checked before each
 *       generation, so the last generation can exceed it.
 * </ul>
 *
 * <p>The result is the first population: it can contain dominated solutions and, if the problem
 * is hard, infeasible ones; filter it with {@link
 * org.uma.jmetal.util.ConstraintHandling#isFeasible} or {@link
 * org.uma.jmetal.util.SolutionListUtils#getNonDominatedSolutions} when needed. Use {@link
 * CCMOBuilder} to create instances.
 *
 * @param <S> the type of the solutions
 */
@SuppressWarnings("serial")
public class CCMO<S extends Solution<?>> implements Algorithm<List<S>> {

  /** The way the populations create their offspring. */
  public enum VariationType {
    /** Crossover and mutation, one child per pair of parents (N/2 offspring per population). */
    GA,
    /** Differential evolution and mutation (N offspring per population); real encoding only. */
    DE
  }

  private final Problem<S> problem;
  private final int populationSize;
  private final int maxEvaluations;
  private final CrossoverOperator<S> crossover;
  private final MutationOperator<S> mutation;
  private final VariationType variationType;
  private final SolutionListEvaluator<S> evaluator;

  private List<S> population;
  private double[] fitness;
  private List<S> helperPopulation;
  private double[] helperFitness;
  private int evaluations;

  /**
   * Creates an instance of CCMO.
   *
   * @param problem the problem to solve
   * @param populationSize the size N of each of the two populations (at least 2)
   * @param maxEvaluations the maximum number of evaluations (positive)
   * @param crossover the crossover operator: one that needs two parents with {@link
   *     VariationType#GA}, or a {@link DifferentialEvolutionCrossover} with one difference vector
   *     and a random base vector with {@link VariationType#DE}
   * @param mutation the mutation operator, applied to every offspring
   * @param variationType the variation of the populations
   * @param evaluator the evaluator of the solutions
   */
  public CCMO(
      Problem<S> problem,
      int populationSize,
      int maxEvaluations,
      CrossoverOperator<S> crossover,
      MutationOperator<S> mutation,
      VariationType variationType,
      SolutionListEvaluator<S> evaluator) {
    Check.notNull(problem, "problem");
    Check.notNull(crossover, "crossover");
    Check.notNull(mutation, "mutation");
    Check.notNull(variationType, "variationType");
    Check.notNull(evaluator, "evaluator");
    Check.that(populationSize >= 2, "The population size must be at least 2: " + populationSize);
    Check.valueIsPositive(maxEvaluations, "maxEvaluations");
    checkCrossover(crossover, variationType);

    this.problem = problem;
    this.populationSize = populationSize;
    this.maxEvaluations = maxEvaluations;
    this.crossover = crossover;
    this.mutation = mutation;
    this.variationType = variationType;
    this.evaluator = evaluator;
    this.population = new ArrayList<>();
    this.helperPopulation = new ArrayList<>();
  }

  @Override
  public void run() {
    population = evaluator.evaluate(createInitialPopulation(), problem);
    helperPopulation = evaluator.evaluate(createInitialPopulation(), problem);
    evaluations = 2 * populationSize;
    fitness = CCMOFitness.compute(population, true);
    helperFitness = CCMOFitness.compute(helperPopulation, false);

    while (evaluations < maxEvaluations) {
      List<S> offspring = evaluate(createOffspring(population, fitness));
      List<S> helperOffspring = evaluate(createOffspring(helperPopulation, helperFitness));

      var next =
          CCMOEnvironmentalSelection.execute(
              union(population, offspring, helperOffspring), populationSize, true);
      var nextHelper =
          CCMOEnvironmentalSelection.execute(
              union(helperPopulation, offspring, helperOffspring), populationSize, false);

      population = next.population();
      fitness = next.fitness();
      helperPopulation = nextHelper.population();
      helperFitness = nextHelper.fitness();
    }
  }

  /** Returns the population that solves the original problem, sorted by fitness. */
  @Override
  public List<S> result() {
    return new ArrayList<>(population);
  }

  @Override
  public String name() {
    return "CCMO";
  }

  @Override
  public String description() {
    return "Coevolutionary constrained multi-objective optimization framework";
  }

  /** Returns the number of evaluations done by the last run. */
  public int numberOfEvaluations() {
    return evaluations;
  }

  /** Returns the population that solves the helper problem (the problem without constraints). */
  public List<S> helperPopulation() {
    return new ArrayList<>(helperPopulation);
  }

  public VariationType variationType() {
    return variationType;
  }

  private static void checkCrossover(
      CrossoverOperator<?> crossover, VariationType variationType) {
    switch (variationType) {
      case GA ->
          Check.that(
              crossover.numberOfRequiredParents() == 2,
              "The GA variation needs a crossover with two parents, and "
                  + crossover.getClass().getSimpleName()
                  + " needs "
                  + crossover.numberOfRequiredParents());
      case DE ->
          Check.that(
              crossover instanceof DifferentialEvolutionCrossover de
                  && de.getMutationType() == DifferentialEvolutionCrossover.DE_MUTATION_TYPE.RAND
                  && de.getNumberOfDifferenceVectors() == 1,
              "The DE variation needs a DifferentialEvolutionCrossover with variant RAND_1_BIN"
                  + " or RAND_1_EXP");
    }
  }

  private List<S> createInitialPopulation() {
    return IntStream.range(0, populationSize)
        .mapToObj(i -> problem.createSolution())
        .collect(Collectors.toCollection(ArrayList::new));
  }

  private List<S> evaluate(List<S> solutions) {
    List<S> evaluated = evaluator.evaluate(solutions, problem);
    evaluations += evaluated.size();
    return evaluated;
  }

  private List<S> createOffspring(List<S> parents, double[] parentFitness) {
    return switch (variationType) {
      case GA -> geneticOffspring(parents, parentFitness);
      case DE -> differentialEvolutionOffspring(parents, parentFitness);
    };
  }

  /** PlatEMO's {@code OperatorGAhalf} on N parents selected by tournament: N/2 offspring. */
  private List<S> geneticOffspring(List<S> parents, double[] parentFitness) {
    int[] matingPool = CCMOTournamentSelection.execute(parentFitness, parents.size());
    int numberOfOffspring = parents.size() / 2;
    List<S> offspring = new ArrayList<>(numberOfOffspring);
    for (int i = 0; i < numberOfOffspring; i++) {
      List<S> pair =
          List.of(
              parents.get(matingPool[i]), parents.get(matingPool[numberOfOffspring + i]));
      offspring.add(mutation.execute(crossover.execute(pair).get(0)));
    }
    return offspring;
  }

  /**
   * PlatEMO's {@code OperatorDE(Problem, P, P(pool(1:N)), P(pool(N+1:2N)))} with 2N parents
   * selected by tournament: N offspring.
   */
  @SuppressWarnings("unchecked")
  private List<S> differentialEvolutionOffspring(List<S> parents, double[] parentFitness) {
    var differentialEvolution = (DifferentialEvolutionCrossover) crossover;
    int size = parents.size();
    int[] matingPool = CCMOTournamentSelection.execute(parentFitness, 2 * size);
    List<S> offspring = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      var current = (DoubleSolution) parents.get(i);
      differentialEvolution.setCurrentSolution(current);
      List<DoubleSolution> deParents =
          List.of(
              (DoubleSolution) parents.get(matingPool[i]),
              (DoubleSolution) parents.get(matingPool[size + i]),
              current);
      S child = (S) differentialEvolution.execute(deParents).get(0);
      offspring.add(mutation.execute(child));
    }
    return offspring;
  }

  private List<S> union(List<S> current, List<S> offspring, List<S> helperOffspring) {
    List<S> union = new ArrayList<>(current.size() + offspring.size() + helperOffspring.size());
    union.addAll(current);
    union.addAll(offspring);
    union.addAll(helperOffspring);
    return union;
  }
}
