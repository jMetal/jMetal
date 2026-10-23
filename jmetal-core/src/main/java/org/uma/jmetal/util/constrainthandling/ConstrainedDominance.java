package org.uma.jmetal.util.constrainthandling;

import java.util.Comparator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.constraintcomparator.ConstraintComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.NumberOfViolatedConstraintsComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;
import org.uma.jmetal.util.errorchecking.Check;

/**
 * Deb's constrained-domination principle (CDP): a feasible solution is preferred to an infeasible
 * one, two infeasible ones are compared by their violation and two feasible ones by their
 * objectives (Pareto dominance, with {@link #comparator()}). The violation is the overall
 * constraint violation degree or, as a variant, the number of violated constraints. Without
 * constraints it is the comparison of the objectives.
 *
 * <p>K. Deb, A. Pratap, S. Agarwal, T. Meyarivan. A fast and elitist multiobjective genetic
 * algorithm: NSGA-II. IEEE TEVC 6(2):182-197, 2002.
 *
 * @author Nicolás Rodríguez Uribe (prototype in Evolver)
 * @param <S> the type of the solutions
 */
public class ConstrainedDominance<S extends Solution<?>>
    implements PairwiseConstraintHandlingTechnique<S> {

  /** How the violation of two infeasible solutions is compared. */
  public enum ViolationMeasure {
    /** The sum of the violations of the constraints. */
    OVERALL_CONSTRAINT_VIOLATION,
    /** The number of violated constraints. */
    NUMBER_OF_VIOLATED_CONSTRAINTS
  }

  private final ConstraintComparator<S> constraintComparator;

  /** Creates the technique with the overall constraint violation degree. */
  public ConstrainedDominance() {
    this(ViolationMeasure.OVERALL_CONSTRAINT_VIOLATION);
  }

  /**
   * Creates the technique with the given violation measure.
   *
   * @param violationMeasure how two infeasible solutions are compared
   */
  public ConstrainedDominance(ViolationMeasure violationMeasure) {
    Check.notNull(violationMeasure);
    constraintComparator =
        switch (violationMeasure) {
          case OVERALL_CONSTRAINT_VIOLATION -> new OverallConstraintViolationDegreeComparator<>();
          case NUMBER_OF_VIOLATED_CONSTRAINTS -> new NumberOfViolatedConstraintsComparator<>();
        };
  }

  @Override
  public int compare(S solution1, S solution2, Comparator<S> objectiveComparison) {
    int result = constraintComparator.compare(solution1, solution2);
    return result != 0 ? result : objectiveComparison.compare(solution1, solution2);
  }
}
