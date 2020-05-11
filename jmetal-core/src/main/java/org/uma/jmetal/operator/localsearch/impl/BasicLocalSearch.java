package org.uma.jmetal.operator.localsearch.impl;

import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

import java.util.Comparator;

/**
 * This class implements a basic local search operator based in the use of a mutation operator.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class BasicLocalSearch<S extends Solution<?>> implements LocalSearchOperator<S> {

  private Problem<S> problem;
  private int improvementRounds;
  private Comparator<S> comparator;

  private MutationOperator<S> mutationOperator;
  private int evaluations;
  private int numberOfImprovements;

  private RandomGenerator<Double> randomGenerator;

  /**
   * Constructor. Creates a new local search object.
   *
   * @param improvementRounds number of iterations
   * @param mutationOperator mutation operator
   * @param comparator comparator to determine which solution is the best
   * @param problem problem to resolve
   */
  public BasicLocalSearch(int improvementRounds, MutationOperator<S> mutationOperator,
      Comparator<S> comparator, Problem<S> problem) {
    this(improvementRounds, mutationOperator, comparator, problem,
        () -> JMetalRandom.getInstance().nextDouble());
  }

  /**
   * Constructor. Creates a new local search object.
   *
   * @param improvementRounds number of iterations
   * @param mutationOperator mutation operator
   * @param comparator comparator to determine which solution is the best
   * @param problem problem to resolve
   * @param randomGenerator the {@link RandomGenerator} to use when we must choose between
   * equivalent solutions
   */
  public BasicLocalSearch(int improvementRounds, MutationOperator<S> mutationOperator,
      Comparator<S> comparator, Problem<S> problem, RandomGenerator<Double> randomGenerator) {
    this.problem = problem;
    this.mutationOperator = mutationOperator;
    this.improvementRounds = improvementRounds;
    this.comparator = comparator;

    this.randomGenerator = randomGenerator;
    numberOfImprovements = 0;
  }

  /**
   * Executes the local search.
   *
   * @param solution The solution to improve
   * @return An improved solution
   */
  @SuppressWarnings("unchecked")
  public S execute(S solution) {
    int best;
    evaluations = 0;

    int rounds = improvementRounds;

    int i = 0;
    while (i < rounds) {
      S mutatedSolution = mutationOperator.execute((S) solution.copy());

      problem.evaluate(mutatedSolution);
      evaluations++;

      best = comparator.compare(mutatedSolution, solution) ;
      if (best == -1) {
        solution = mutatedSolution;
        numberOfImprovements++;
      } else if (best == 0) {
        if (randomGenerator.getRandomValue() < 0.5) {
          solution = mutatedSolution;
        }
      }
      i++;
    }
    return (S) solution.copy();
  }

  /**
   * Returns the number of evaluations
   */
  @Override
  public int getNumberOfEvaluations() {
    return evaluations;
  }

  @Override
  public int getNumberOfImprovements() {
    return numberOfImprovements;
  }

}
