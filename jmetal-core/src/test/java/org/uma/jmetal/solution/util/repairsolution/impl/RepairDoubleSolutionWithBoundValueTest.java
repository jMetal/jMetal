package org.uma.jmetal.solution.util.repairsolution.impl;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionWithBoundValueTest {
  private static final double EPSILON = 0.0000000000001 ;
  private RepairDoubleSolution repair ;

  @Before
  public void setup() {
    repair = new RepairDoubleSolutionWithBoundValue() ;
  }

  @Test (expected = RuntimeException.class)
  public void shouldRepairDoubleSolutionAtBoundsRaiseAnExceptionIfTheBoundsAreIncorrect() {
    repair.repairSolutionVariableValue(0.0, 1.0, -1.0) ;
  }

  @Test
  public void shouldRepairDoubleSolutionAtBoundsAssignTheLowerBoundIfValueIsLessThanIt() {
    assertEquals(-1.0, repair.repairSolutionVariableValue(-3, -1.0, 1.0), EPSILON) ;
  }

  @Test
  public void shouldRepairDoubleSolutionAtBoundsAssignTheUpperBoundIfValueIsGreaterThanIt() {
    assertEquals(1.0, repair.repairSolutionVariableValue(4, -1.0, 1.0), EPSILON) ;
  }
}
