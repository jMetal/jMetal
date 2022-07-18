package org.uma.jmetal.solution.util.repairsolution.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Random;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.solution.util.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionWithRandomValueTest {
  private RepairDoubleSolution repair;

  @Before public void setup() {
    repair = new RepairDoubleSolutionWithRandomValue();
  }

  @Test(expected = InvalidConditionException.class)
  public void shouldRRepairRaiseAnExceptionIfTheBoundsAreIncorrect() {
    repair.repairSolutionVariableValue(0.0, 1.0, -1.0);
  }

  @Test
  public void shouldRepairAssignARandomValueIfValueIsLessThanTheLowerBound() {
      var lowerBound = -1.0;
      var upperBound = 1.0;
    assertThat(repair.repairSolutionVariableValue(-3, lowerBound, upperBound),
        Matchers.lessThanOrEqualTo(upperBound));

    assertThat(repair.repairSolutionVariableValue(-3, lowerBound, upperBound),
        Matchers.greaterThanOrEqualTo(lowerBound));
  }

  @Test
  public void shouldRepairAssignARandomValueIfValueIsGreaterThanTheUpperBound() {
      var lowerBound = -1.0;
      var upperBound = 1.0;
    assertThat(repair.repairSolutionVariableValue(4, lowerBound, upperBound),
        Matchers.lessThanOrEqualTo(upperBound));

    assertThat(repair.repairSolutionVariableValue(4, lowerBound, upperBound),
        Matchers.greaterThanOrEqualTo(lowerBound));
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
        var lowerBound = -1.0;
        var upperBound = 1.0;
        var value = 4;

		// Check configuration leads to use default generator by default
		final var defaultUses = new int[]{0};
        var defaultGenerator = JMetalRandom.getInstance();
        var auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new RepairDoubleSolutionWithRandomValue().repairSolutionVariableValue(value, lowerBound, upperBound);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final var customUses = new int[]{0};
		new RepairDoubleSolutionWithRandomValue((a, b) -> {
			customUses[0]++;
			return new Random().nextDouble()*(b-a)+a;
		}).repairSolutionVariableValue(value, lowerBound, upperBound);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}
