package org.uma.jmetal.operator.mutation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.AuditableRandomGenerator;

public class NonUniformMutationTest {

  @Test
  public void testJMetalRandomGeneratorNotUsedWhenCustomRandomGeneratorProvided() {
    // Configuration

    List<Bounds<Double>> bounds = Arrays.asList(Bounds.create(0.0, 1.0));

    DoubleSolution solution = new DefaultDoubleSolution(bounds, 2, 0);

    // Check configuration leads to use default generator by default
    final int[] defaultUses = {0};
    JMetalRandom defaultGenerator = JMetalRandom.getInstance();
    AuditableRandomGenerator auditor =
        new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
    defaultGenerator.setRandomGenerator(auditor);
    auditor.addListener((a) -> defaultUses[0]++);

    new NonUniformMutation(0.5, 0.5, 10).execute(solution);
    assertTrue(defaultUses[0] > 0, "No use of the default generator");

    // Test same configuration uses custom generator instead
    defaultUses[0] = 0;
    final int[] customUses = {0};
    new NonUniformMutation(
            0.5,
            0.5,
            10,
            new RepairDoubleSolutionWithBoundValue(),
            () -> {
              customUses[0]++;
              return new Random().nextDouble();
            })
        .execute(solution);
        assertTrue(defaultUses[0] == 0, "Default random generator used");
        assertTrue(customUses[0] > 0, "No use of the custom generator");
  }
}
