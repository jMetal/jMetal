package org.uma.jmetal.component.algorithm.multiobjective;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.referencepoint.ReferencePointGenerator;

/**
 * Unit tests for the component-based NSGA-III implementation.
 */
class NSGAIIIBuilderTest {

  @Test
  void shouldConstructWithSingleLayerReferencePoints() {
    var problem = new DTLZ1();
    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(0.1, 20.0);

    NSGAIIIBuilder<DoubleSolution> builder = new NSGAIIIBuilder<>(
        problem, 12, crossover, mutation);

    // For 3 objectives and 12 divisions: C(14, 2) = 91 reference points
    // Population size should be 92 (rounded to multiple of 4)
    assertEquals(92, builder.getPopulationSize());
    assertEquals(91, builder.getReferencePoints().size());
  }

  @Test
  void shouldConstructWithTwoLayerReferencePoints() {
    var problem = new DTLZ2(14, 5);
    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(0.1, 20.0);

    NSGAIIIBuilder<DoubleSolution> builder = new NSGAIIIBuilder<>(
        problem, 6, 3, crossover, mutation);

    // Outer: C(10, 4) = 210, Inner: C(7, 4) = 35, Total: 245
    // Population size should be 248 (rounded to multiple of 4)
    assertEquals(248, builder.getPopulationSize());
    assertEquals(245, builder.getReferencePoints().size());
  }

  @Test
  void shouldRunOnDTLZ1WithThreeObjectives() {
    var problem = new DTLZ1();
    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    EvolutionaryAlgorithm<DoubleSolution> algorithm = new NSGAIIIBuilder<>(
        problem, 12, crossover, mutation)
        .setTermination(new TerminationByEvaluations(5000))
        .build();

    algorithm.run();

    List<DoubleSolution> result = algorithm.result();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(3, result.get(0).objectives().length);
  }

  @Test
  void shouldRunOnDTLZ2WithFiveObjectives() {
    var problem = new DTLZ2(14, 5);
    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    EvolutionaryAlgorithm<DoubleSolution> algorithm = new NSGAIIIBuilder<>(
        problem, 6, 3, crossover, mutation)
        .setTermination(new TerminationByEvaluations(5000))
        .build();

    algorithm.run();

    List<DoubleSolution> result = algorithm.result();

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals(5, result.get(0).objectives().length);
  }

  @Test
  void referencePointGeneratorShouldProduceCorrectCount() {
    // 3 objectives, 12 divisions: C(14, 2) = 91
    assertEquals(91,
        ReferencePointGenerator.calculateNumberOfReferencePoints(3, 12));

    // 5 objectives, 6 divisions: C(10, 4) = 210
    assertEquals(210,
        ReferencePointGenerator.calculateNumberOfReferencePoints(5, 6));

    // 5 objectives, 3 divisions: C(7, 4) = 35
    assertEquals(35,
        ReferencePointGenerator.calculateNumberOfReferencePoints(5, 3));
  }

  @Test
  void generatedReferencePointsShouldSumToOne() {
    List<double[]> refPoints = ReferencePointGenerator.generateSingleLayer(3, 4);

    for (double[] point : refPoints) {
      double sum = 0;
      for (double v : point) {
        sum += v;
      }
      assertEquals(1.0, sum, 1e-10, "Reference point should sum to 1.0");
    }
  }
}
