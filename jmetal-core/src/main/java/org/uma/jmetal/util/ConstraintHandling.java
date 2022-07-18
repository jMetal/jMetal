package org.uma.jmetal.util;

import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * This class offers a set of static methods for setting and getting information about constraints
 * in solutions.
 */
public class ConstraintHandling {

  /**
   * The computing of the constraints can be precomputed when evaluating a solution. In that case
   * the components of this enum type can be used as a key of a solution attribute indicating such
   * situation (see methods {@link #overallConstraintViolationDegree(Solution, double)} and
   * {@link #numberOfViolatedConstraints(Solution, int)}
   */
  public enum PRECOMPUTED {
    OVERALL_CONSTRAINT_VIOLATION,
    NUMBER_OF_VIOLATED_CONSTRAINTS
  }

  /**
   * Given a solution, it is feasible if the number of violated constraints is zero
   * @param solution
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> boolean isFeasible(S solution) {
    return numberOfViolatedConstraints(solution) == 0;
  }

  /**
   * Returns the number of constraints a solution violates. A check is made to determine whether
   * that number has been precomputed; if not, it is calculated as the sum of the constraints having
   * a negative value.
   *
   * @param solution
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> int numberOfViolatedConstraints(S solution) {
    long count = 0L;
    int bound = solution.constraints().length;
    for (int i = 0; i < bound; i++) {
      if (solution.constraints()[i] < 0) {
        count++;
      }
    }
    return (int) solution.attributes().getOrDefault(
        PRECOMPUTED.NUMBER_OF_VIOLATED_CONSTRAINTS,
        (int) count);
  }

  /**
   * Sets the overall constraint violation degree of a solution
   * @param solution
   * @param constraintViolationDegreeValue
   * @param <S>
   */
  public static <S extends Solution<?>> void overallConstraintViolationDegree(S solution, double constraintViolationDegreeValue) {
    solution.attributes().put(PRECOMPUTED.OVERALL_CONSTRAINT_VIOLATION, constraintViolationDegreeValue) ;
  }

  /**
   * Sets the number of violated constraints in a solution
   * @param solution
   * @param numberOfViolatedConstraints
   * @param <S>
   */
  public static <S extends Solution<?>> void numberOfViolatedConstraints(S solution, int numberOfViolatedConstraints) {
    solution.attributes().put(PRECOMPUTED.NUMBER_OF_VIOLATED_CONSTRAINTS, numberOfViolatedConstraints) ;
  }

  /**
   * Returns the overall constraint violation degree of a solution. A check is made to determine
   * whether that number has been precomputed; if not, it is calculated as the sum of the
   * values of the constraints having a negative value.
   *
   * @param solution
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> double overallConstraintViolationDegree(S solution) {
    double overallConstraintViolation =
        (double) solution.attributes().getOrDefault(
            PRECOMPUTED.OVERALL_CONSTRAINT_VIOLATION,
            0.0);
    if (overallConstraintViolation == 0.0) {
      double sum = 0.0;
      int bound = solution.constraints().length;
      for (int i = 0; i < bound; i++) {
        if (solution.constraints()[i] < 0.0) {
          double constraint = solution.constraints()[i];
          sum += constraint;
        }
      }
      overallConstraintViolation = sum;
    }
    return overallConstraintViolation;
  }

  /**
   * Returns the ratio of feasible solutions in a solution list
   *
   * @param solutions
   * @return
   */
  public static <S extends Solution<?>> double feasibilityRatio(List<S> solutions) {
    Check.collectionIsNotEmpty(solutions);
    long count = 0L;
    for (S solution : solutions) {
      if (isFeasible(solution)) {
        count++;
      }
    }
    double result = count;

    return result / solutions.size();
  }
}