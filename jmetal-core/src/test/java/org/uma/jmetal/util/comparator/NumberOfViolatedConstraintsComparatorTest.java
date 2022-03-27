package org.uma.jmetal.util.comparator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.Test;
import org.uma.jmetal.solution.Solution;

/**
  Test cases:
  - The constructor returns a non-null object
  - If the first solution in method {@link NumberOfViolatedConstraintsComparator#compare(Solution, Solution)}  is null throws and exception
  - If the second solution in method {@link NumberOfViolatedConstraintsComparator#compare(Solution, Solution)} is null throws and exception
  - The two solutions have no constraints
  - The two solutions are feasible
  - The first solution is feasible and the second one is not
  - The second solution is feasible and the first one is not
  - Both solutions have the same number of violated constraints
  - The first solutions violates more constraints than the second solution
  - The second solutions violates more constraints than the first solution
 */
class NumberOfViolatedConstraintsComparatorTest {
  @Test
  public void constructorReturnsANonNullObject() {

  }

}