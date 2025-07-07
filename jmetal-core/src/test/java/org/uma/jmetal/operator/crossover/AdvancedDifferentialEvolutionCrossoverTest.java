package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.operator.crossover.impl.AdvancedDifferentialEvolutionCrossover;
import org.uma.jmetal.operator.crossover.impl.AdvancedDifferentialEvolutionCrossover.Variant;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NegativeValueException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.errorchecking.exception.ValueOutOfRangeException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/** Test cases for {@link AdvancedDifferentialEvolutionCrossover} */
class AdvancedDifferentialEvolutionCrossoverTest {
  private static final double EPSILON = 0.00000000000001;
  private static final double DEFAULT_CR = 0.5;
  private static final double DEFAULT_F = 0.5;
  private static final double DEFAULT_PBEST = 0.05;
  private static final int POPULATION_SIZE = 10;
  private static final int MEMORY_SIZE = 5;

  private RandomGenerator<Double> randomGenerator;
  private FakeDoubleProblem problem;

  @BeforeEach
  void setup() {
    randomGenerator = mock(RandomGenerator.class);
    problem = new FakeDoubleProblem(3, 2, 0);
    when(randomGenerator.getRandomValue())
        .thenReturn(0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9);
  }

  @Test
  void constructorWithBuilderSetsParametersCorrectly() {
    // This test verifies the builder creates the operator without exceptions
    // and that it can execute with valid parameters
    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withCr(DEFAULT_CR)
            .withF(DEFAULT_F)
            .withVariant(Variant.RAND_1_BIN)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .withPBest(DEFAULT_PBEST)
            .withMemorySize(MEMORY_SIZE)
            .build();
            
    // Verify we can execute with valid parameters
    List<DoubleSolution> parents = createParents(4);
    List<DoubleSolution> offspring = crossover.execute(parents);
    assertNotNull(offspring);
    assertEquals(1, offspring.size());
  }

  @Test
  void constructorWithBuilderRaisesExceptionIfRandomGeneratorIsNull() {
    assertThrows(
        NullParameterException.class,
        () -> new AdvancedDifferentialEvolutionCrossover.Builder()
            .withRandomGenerator(null));
  }

  @Test
  void constructorWithBuilderRaisesExceptionIfCrIsNegative() {
    assertThrows(
        NegativeValueException.class,
        () -> new AdvancedDifferentialEvolutionCrossover.Builder().withCr(-0.5));
  }

  @Test
  void constructorWithBuilderRaisesExceptionIfFIsNegative() {
    assertThrows(
        NegativeValueException.class,
        () -> new AdvancedDifferentialEvolutionCrossover.Builder().withF(-0.5));
  }

  @Test
  void constructorWithBuilderRaisesExceptionIfCrIsGreaterThanOne() {
    assertThrows(
        InvalidConditionException.class,
        () -> new AdvancedDifferentialEvolutionCrossover.Builder().withCr(1.5));
  }

  @Test
  void constructorWithBuilderRaisesExceptionIfPBestIsNotInValidRange() {
    assertThrows(
        ValueOutOfRangeException.class,
        () -> new AdvancedDifferentialEvolutionCrossover.Builder().withPBest(1.5));
  }

  @Test
  void executeWithRAND1BINVariantProducesValidOffspring() {
    // This is a basic test to verify the operator doesn't crash
    // More detailed tests would require mocking the random generator more precisely
    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withCr(0.9)
            .withF(0.5)
            .withVariant(Variant.RAND_1_BIN)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();

    List<DoubleSolution> parents = createParents(4);
    List<DoubleSolution> offspring = crossover.execute(parents);
    
    assertNotNull(offspring);
    assertEquals(1, offspring.size());
    assertNotSame(parents.get(0), offspring.get(0));
    
    // Verify the offspring has the same number of variables as parents
    assertEquals(parents.get(0).variables().size(), offspring.get(0).variables().size());
    
    // Verify the offspring values are within bounds
    for (int i = 0; i < offspring.get(0).variables().size(); i++) {
      double value = offspring.get(0).variables().get(i);
      assertTrue(value >= 0.0 && value <= 1.0, "Value out of bounds: " + value);
    }
  }

  @Test
  void executeWithSHADEVariantUpdatesMemory() {
    // This test verifies that the SHADE variant updates its memory
    // We'll use a mock to control the random values precisely
    when(randomGenerator.getRandomValue())
        .thenReturn(0.1, 0.2, 0.3, 0.4, 0.5)  // For parent selection
        .thenReturn(0.6, 0.7, 0.8, 0.9, 0.1)   // For CR and F generation
        .thenReturn(0.2, 0.3, 0.4, 0.5, 0.6);  // For crossover

    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withCr(0.9)
            .withF(0.5)
            .withVariant(Variant.SHADE)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .withMemorySize(2)
            .build();

    List<DoubleSolution> parents = createParents(5);
    List<DoubleSolution> offspring = crossover.execute(parents);
    
    assertNotNull(offspring);
    assertEquals(1, offspring.size());
    
    // The memory should be updated after execution
    // Since we can't directly access the private memory, we'll check if the operator
    // behaves differently on subsequent calls
    List<DoubleSolution> secondOffspring = crossover.execute(parents);
    assertNotEquals(offspring.get(0).variables(), secondOffspring.get(0).variables());
  }

  @Test
  void crossoverProbabilityReturnsOne() {
    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.RAND_1_BIN)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    
    assertEquals(1.0, crossover.crossoverProbability(), EPSILON);
  }

  @Test
  void numberOfRequiredParentsReturnsCorrectValueForEachVariant() {
    // RAND_1_BIN needs 4 parents (target + 3 difference vectors)
    AdvancedDifferentialEvolutionCrossover rand1Bin =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.RAND_1_BIN)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    assertEquals(4, rand1Bin.numberOfRequiredParents());

    // RAND_2_BIN needs 5 parents (target + 4 difference vectors)
    AdvancedDifferentialEvolutionCrossover rand2Bin =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.RAND_2_BIN)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    assertEquals(5, rand2Bin.numberOfRequiredParents());

    // CURRENT_TO_RAND_1 needs 4 parents (target + 3 difference vectors)
    AdvancedDifferentialEvolutionCrossover currentToRand1 =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.CURRENT_TO_RAND_1)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    assertEquals(4, currentToRand1.numberOfRequiredParents());
    
    // CURRENT_TO_PBEST_1 needs 4 parents (target + 3 difference vectors)
    AdvancedDifferentialEvolutionCrossover currentToPBest1 =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.CURRENT_TO_PBEST_1)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    assertEquals(4, currentToPBest1.numberOfRequiredParents());
    
    // SHADE needs 5 parents (target + 4 difference vectors + archive)
    AdvancedDifferentialEvolutionCrossover shade =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.SHADE)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    assertEquals(5, shade.numberOfRequiredParents());
  }

  @Test
  void numberOfGeneratedChildrenReturnsOne() {
    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.RAND_1_BIN)
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    
    assertEquals(1, crossover.numberOfGeneratedChildren());
  }

  @Test
  void executeRaisesExceptionIfNumberOfParentsIsIncorrect() {
    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.RAND_1_BIN)  // Needs 4 parents
            .withRandomGenerator(randomGenerator)
            .withPopulationSize(POPULATION_SIZE)
            .build();
    
    // Only 3 parents provided, but 4 are needed
    List<DoubleSolution> parents = createParents(3);
    assertThrows(InvalidConditionException.class, () -> crossover.execute(parents));
  }

  @Test
  void customRandomGeneratorIsUsedInsteadOfDefault() {
    // This test verifies that the custom random generator is used
    final boolean[] customGeneratorUsed = {false};
    RandomGenerator<Double> customRandom = new RandomGenerator<>() {
      @Override
      public Double getRandomValue() {
        customGeneratorUsed[0] = true;
        return 0.5; // Always return 0.5 for predictable testing
      }
    };

    AdvancedDifferentialEvolutionCrossover crossover =
        new AdvancedDifferentialEvolutionCrossover.Builder()
            .withVariant(Variant.RAND_1_BIN)
            .withRandomGenerator(customRandom)
            .withPopulationSize(POPULATION_SIZE)
            .build();

    List<DoubleSolution> parents = createParents(4);
    crossover.execute(parents);
    
    // The custom random generator should have been called
    assertTrue(customGeneratorUsed[0]);
  }

  // Helper method to create parent solutions
  private List<DoubleSolution> createParents(int numberOfParents) {
    List<DoubleSolution> parents = new ArrayList<>();
    for (int i = 0; i < numberOfParents; i++) {
      DoubleSolution parent = problem.createSolution();
      // Set some distinct values for testing
      for (int j = 0; j < parent.variables().size(); j++) {
        parent.variables().set(j, 0.1 * (i + 1) * (j + 1));
      }
      parents.add(parent);
    }
    return parents;
  }
}
