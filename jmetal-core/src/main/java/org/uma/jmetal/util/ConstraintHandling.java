package org.uma.jmetal.util;

import java.util.stream.IntStream;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.List;

public class ConstraintHandling {

  public enum PRECOMPUTED {
    OVERALL_CONSTRAINT_VIOLATION,
    NUMBER_OF_VIOLATED_CONSTRAINTS
  }

  public static <S extends Solution<?>> boolean isFeasible(S solution) {
    return numberOfViolatedConstraints(solution) == 0;
  }

  /**
   * Returns the number of constraints a solution violates.
   *
   * @param solution
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> int numberOfViolatedConstraints(S solution) {
    int result =
        (int) solution.attributes().getOrDefault(PRECOMPUTED.NUMBER_OF_VIOLATED_CONSTRAINTS, 0);
    if (result == 0) {
      result = (int) IntStream.range(0, solution.constraints().length)
          .filter(i -> solution.constraints()[i] < 0)
          .count();
    }
    return result;
  }

  /**
   * Returns the overall constraint violation degree of a solution.
   *
   * @param solution
   * @param <S>
   * @return
   */
  public static <S extends Solution<?>> double overallConstraintViolationDegree(S solution) {
    double overallConstraintViolation =
        (double) solution.attributes().getOrDefault(PRECOMPUTED.OVERALL_CONSTRAINT_VIOLATION, IntStream.range(0, solution.constraints().length)
            .filter(i -> solution.constraints()[i] < 0.0).mapToDouble(i -> solution.constraints()[i])
            .sum());
    if (overallConstraintViolation == 0.0) {
      overallConstraintViolation = IntStream.range(0, solution.constraints().length)
          .filter(i -> solution.constraints()[i] < 0.0).mapToDouble(i -> solution.constraints()[i])
          .sum();
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
    double result = solutions.stream().filter(ConstraintHandling::isFeasible).count();

    return result / solutions.size();
  }
}