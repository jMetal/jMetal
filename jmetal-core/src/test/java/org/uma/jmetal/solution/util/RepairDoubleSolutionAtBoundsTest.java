package org.uma.jmetal.solution.util;

import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;

import static org.junit.Assert.assertEquals;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionAtBoundsTest {
  private static final double EPSILON = 0.0000000000001 ;
  private RepairDoubleSolution repair ;

  @Before
  public void setup() {
    repair = new RepairDoubleSolutionAtBounds() ;
  }

  @Test (expected = JMetalException.class)
  public void shouldRRepairDoubleSolutionAtBoundsRaiseAnExceptionIfTheBoundsAreIncorrect() {
    repair.repairSolutionVariableValue(0.0, 1.0, -1.0) ;
  }

  @Test
  public void shouldRRepairDoubleSolutionAtBoundsAssignTheLowerBoundIfValueIsLessThanIt() {
    assertEquals(-1.0, repair.repairSolutionVariableValue(-3, -1.0, 1.0), EPSILON) ;
  }

  @Test
  public void shouldRRepairDoubleSolutionAtBoundsAssignTheUpperBoundIfValueIsGreaterThanIt() {
    assertEquals(1.0, repair.repairSolutionVariableValue(4, -1.0, 1.0), EPSILON) ;
  }
}
