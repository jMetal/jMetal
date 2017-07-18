package org.uma.jmetal.solution.util;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Random;

/**
 * @author Antonio J. Nebro
 * @version 1.0
 */
public class RepairDoubleSolutionAtRandomTest {
  private RepairDoubleSolution repair;

  @Before public void setup() {
    repair = new RepairDoubleSolutionAtRandom();
  }

  @Test(expected = JMetalException.class)
  public void shouldRRepairDoubleSolutionAtRandomRaiseAnExceptionIfTheBoundsAreIncorrect() {
    repair.repairSolutionVariableValue(0.0, 1.0, -1.0);
  }

  @Test
  public void shouldRRepairDoubleSolutionAtRandomAssignARandomValueIfValueIsLessThanTheLowerBound() {
    double lowerBound = -1.0;
    double upperBound = 1.0;
    assertThat(repair.repairSolutionVariableValue(-3, lowerBound, upperBound),
        Matchers.lessThanOrEqualTo(upperBound));

    assertThat(repair.repairSolutionVariableValue(-3, lowerBound, upperBound),
        Matchers.greaterThanOrEqualTo(lowerBound));
  }

  @Test
  public void shouldRRepairDoubleSolutionAtRandomAssignARandomValueIfValueIsGreaterThanTheUpperBound() {
    double lowerBound = -1.0;
    double upperBound = 1.0;
    assertThat(repair.repairSolutionVariableValue(4, lowerBound, upperBound),
        Matchers.lessThanOrEqualTo(upperBound));

    assertThat(repair.repairSolutionVariableValue(4, lowerBound, upperBound),
        Matchers.greaterThanOrEqualTo(lowerBound));
  }
  
	@Test
	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
		// Configuration
		double lowerBound = -1.0;
		double upperBound = 1.0;
		int value = 4;

		// Check configuration leads to use default generator by default
		final int[] defaultUses = { 0 };
		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
		defaultGenerator.setRandomGenerator(auditor);
		auditor.addListener((a) -> defaultUses[0]++);

		new RepairDoubleSolutionAtRandom().repairSolutionVariableValue(value, lowerBound, upperBound);
		assertTrue("No use of the default generator", defaultUses[0] > 0);

		// Test same configuration uses custom generator instead
		defaultUses[0] = 0;
		final int[] customUses = { 0 };
		new RepairDoubleSolutionAtRandom((a, b) -> {
			customUses[0]++;
			return new Random().nextDouble()*(b-a)+a;
		}).repairSolutionVariableValue(value, lowerBound, upperBound);
		assertTrue("Default random generator used", defaultUses[0] == 0);
		assertTrue("No use of the custom generator", customUses[0] > 0);
	}
}
