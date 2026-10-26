package org.uma.jmetal.algorithm.multiobjective.top;

import java.io.Serializable;
import java.util.function.IntToDoubleFunction;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * The differential evolution operators of ToP, as in PlatEMO: DE/current-to-rand/1 ({@code
 * DE_cr.m}) and DE/rand-to-best/1/bin ({@code DE_rb.m}) for phase 1, and DE/rand/1/bin ({@code
 * OperatorDE.m}) for phase 2.
 *
 * <p>Each operator copies a base vector and replaces each of its variables, with probability CR
 * (a uniform random number lower than CR, without a guaranteed j<sub>rand</sub>), by the value of
 * the mutant vector; then it clamps the variables to their bounds and applies the mutation
 * operator to the result. The parents are not modified, and the returned solution has to be
 * evaluated.
 */
@SuppressWarnings("serial")
final class ToPDifferentialEvolution implements Serializable {
  private final double cr;
  private final double f;
  private final MutationOperator<DoubleSolution> mutation;
  private final RandomGenerator<Double> randomGenerator;

  /**
   * Creates the operators.
   *
   * @param cr the crossover rate CR, in [0, 1]
   * @param f the scale factor F
   * @param mutation the mutation applied to each trial vector after clamping it
   * @param randomGenerator a generator of uniform random numbers in [0, 1)
   */
  ToPDifferentialEvolution(
      double cr,
      double f,
      MutationOperator<DoubleSolution> mutation,
      RandomGenerator<Double> randomGenerator) {
    this.cr = cr;
    this.f = f;
    this.mutation = mutation;
    this.randomGenerator = randomGenerator;
  }

  /**
   * DE/current-to-rand/1: x<sub>i</sub> + F(x<sub>r1</sub> - x<sub>i</sub>) + F(x<sub>r2</sub> -
   * x<sub>r3</sub>), with x<sub>i</sub> as base vector.
   *
   * @param current the solution x<sub>i</sub>
   * @param r1 the solution x<sub>r1</sub>
   * @param r2 the solution x<sub>r2</sub>
   * @param r3 the solution x<sub>r3</sub>
   * @return the trial vector, not evaluated
   */
  DoubleSolution currentToRand1(
      DoubleSolution current, DoubleSolution r1, DoubleSolution r2, DoubleSolution r3) {
    return trialVector(
        current,
        j ->
            value(current, j)
                + f * (value(r1, j) - value(current, j))
                + f * (value(r2, j) - value(r3, j)));
  }

  /**
   * DE/rand-to-best/1/bin: x<sub>r1</sub> + F(x<sub>best</sub> - x<sub>r1</sub>) +
   * F(x<sub>r2</sub> - x<sub>r3</sub>), with x<sub>r1</sub> as base vector.
   *
   * @param best the solution x<sub>best</sub>
   * @param r1 the solution x<sub>r1</sub>
   * @param r2 the solution x<sub>r2</sub>
   * @param r3 the solution x<sub>r3</sub>
   * @return the trial vector, not evaluated
   */
  DoubleSolution randToBest1(
      DoubleSolution best, DoubleSolution r1, DoubleSolution r2, DoubleSolution r3) {
    return trialVector(
        r1,
        j ->
            value(r1, j)
                + f * (value(best, j) - value(r1, j))
                + f * (value(r2, j) - value(r3, j)));
  }

  /**
   * DE/rand/1/bin: x<sub>1</sub> + F(x<sub>2</sub> - x<sub>3</sub>), with x<sub>1</sub> as base
   * vector.
   *
   * @param base the solution x<sub>1</sub>
   * @param second the solution x<sub>2</sub>
   * @param third the solution x<sub>3</sub>
   * @return the trial vector, not evaluated
   */
  DoubleSolution rand1(DoubleSolution base, DoubleSolution second, DoubleSolution third) {
    return trialVector(base, j -> value(base, j) + f * (value(second, j) - value(third, j)));
  }

  private DoubleSolution trialVector(DoubleSolution base, IntToDoubleFunction mutant) {
    DoubleSolution trial = (DoubleSolution) base.copy();
    int numberOfVariables = trial.variables().size();
    for (int j = 0; j < numberOfVariables; j++) {
      if (randomGenerator.getRandomValue() < cr) {
        trial.variables().set(j, mutant.applyAsDouble(j));
      }
    }
    for (int j = 0; j < numberOfVariables; j++) {
      Bounds<Double> bounds = trial.getBounds(j);
      double clamped =
          Math.min(
              Math.max(trial.variables().get(j), bounds.getLowerBound()),
              bounds.getUpperBound());
      trial.variables().set(j, clamped);
    }
    return mutation.execute(trial);
  }

  private static double value(DoubleSolution solution, int index) {
    return solution.variables().get(index);
  }
}
