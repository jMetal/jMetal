package org.uma.jmetal.solution.util.repairsolution.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithOppositeBoundValue;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;

public class RepairDoubleSolutionWithOppositeBoundValueTest {
  private static final double EPSILON = 0.0000000000001 ;
  private RepairDoubleSolution repair ;

  @BeforeEach
  public void setup() {
    repair = new RepairDoubleSolutionWithOppositeBoundValue() ;
  }

  @Test
  public void shouldRepairRaiseAnExceptionIfTheBoundsAreIncorrect() {
    assertThrows(InvalidConditionException.class,
        () -> repair.repairSolutionVariableValue(0.0, 1.0, -1.0));
  }

  @Test
  public void shouldRRepairAssignTheLowerBoundIfValueIsHigherThanTheUpperBound() {
    assertEquals(-1.0, repair.repairSolutionVariableValue(3.0, -1.0, 1.0), EPSILON) ;
  }

  @Test
  public void shouldRRepairAssignTheUpperBoundIfValueIsLowerThanTheLowerBound() {
    assertEquals(1.0, repair.repairSolutionVariableValue(-4, -1.0, 1.0), EPSILON) ;
  }
}