package org.uma.jmetal.operator.mutation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uma.jmetal.operator.mutation.impl.LevyFlightMutation;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.RepairDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.pseudorandom.RandomGenerator;

/**
 * Test class for the {@link LevyFlightMutation} operator.
 *
 * @author Antonio J. Nebro
 */
@DisplayName("LevyFlightMutation Tests")
class LevyFlightMutationTest {

  @Mock private DoubleSolution mockSolution;

  @Mock private Bounds<Double> mockBounds;

  @Mock private RepairDoubleSolution mockRepair;

  @Mock private RandomGenerator<Double> mockRandom;

  private LevyFlightMutation mutation;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    when(mockBounds.getLowerBound()).thenReturn(0.0);
    when(mockBounds.getUpperBound()).thenReturn(1.0);
  }

  private void setupSolutionVariables(List<Double> values) {
    List<Double> variables = new ArrayList<>(values);
    when(mockSolution.variables()).thenReturn(variables);
    for (int i = 0; i < values.size(); i++) {
      when(mockSolution.getBounds(i)).thenReturn(mockBounds);
    }
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Default constructor sets default values")
    void defaultConstructorSetsDefaultValues() {
      mutation = new LevyFlightMutation();

      assertEquals(0.01, mutation.mutationProbability(), 1e-10);
      assertEquals(1.5, mutation.beta(), 1e-10);
      assertEquals(0.01, mutation.stepSize(), 1e-10);
      assertNotNull(mutation.solutionRepair());
    }

    @Test
    @DisplayName("Constructor with parameters sets values correctly")
    void constructorWithParametersSetsValues() {
      mutation = new LevyFlightMutation(0.5, 1.8, 0.05);

      assertEquals(0.5, mutation.mutationProbability(), 1e-10);
      assertEquals(1.8, mutation.beta(), 1e-10);
      assertEquals(0.05, mutation.stepSize(), 1e-10);
    }

    @ParameterizedTest
    @ValueSource(doubles = {-0.1, 1.1})
    @DisplayName("Constructor throws exception for invalid mutation probability")
    void constructorThrowsForInvalidProbability(double invalidProbability) {
      assertThrows(
          JMetalException.class, () -> new LevyFlightMutation(invalidProbability, 1.5, 0.01));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 1.0, 2.1})
    @DisplayName("Constructor throws exception for invalid beta")
    void constructorThrowsForInvalidBeta(double invalidBeta) {
      assertThrows(JMetalException.class, () -> new LevyFlightMutation(0.1, invalidBeta, 0.01));
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -0.1, -1.0})
    @DisplayName("Constructor throws exception for invalid step size")
    void constructorThrowsForInvalidStepSize(double invalidStepSize) {
      assertThrows(JMetalException.class, () -> new LevyFlightMutation(0.1, 1.5, invalidStepSize));
    }
  }

  @Nested
  @DisplayName("Property Tests")
  class PropertyTests {

    @BeforeEach
    void setUp() {
      mutation = new LevyFlightMutation(0.1, 1.5, 0.01);
    }

    @Test
    @DisplayName("Mutation probability getter and setter work correctly")
    void mutationProbabilityGetterAndSetter() {
      assertEquals(0.1, mutation.mutationProbability(), 1e-10);

      mutation.mutationProbability(0.5);
      assertEquals(0.5, mutation.mutationProbability(), 1e-10);

      assertThrows(JMetalException.class, () -> mutation.mutationProbability(-0.1));
      assertThrows(JMetalException.class, () -> mutation.mutationProbability(1.1));
    }

    @Test
    @DisplayName("Beta getter and setter work correctly")
    void betaGetterAndSetter() {
      assertEquals(1.5, mutation.beta(), 1e-10);

      mutation.beta(1.8);
      assertEquals(1.8, mutation.beta(), 1e-10);

      assertThrows(JMetalException.class, () -> mutation.beta(0.5));
      assertThrows(JMetalException.class, () -> mutation.beta(2.1));
    }

    @Test
    @DisplayName("Step size getter and setter work correctly")
    void stepSizeGetterAndSetter() {
      assertEquals(0.01, mutation.stepSize(), 1e-10);

      mutation.stepSize(0.05);
      assertEquals(0.05, mutation.stepSize(), 1e-10);

      assertThrows(JMetalException.class, () -> mutation.stepSize(0.0));
      assertThrows(JMetalException.class, () -> mutation.stepSize(-0.1));
    }

    @Test
    @DisplayName("Solution repair getter and setter work correctly")
    void solutionRepairGetterAndSetter() {
      assertNotNull(mutation.solutionRepair());

      mutation.solutionRepair(mockRepair);
      assertSame(mockRepair, mutation.solutionRepair());
    }
  }

  @Nested
  @DisplayName("Execution Tests")
  class ExecutionTests {

    @BeforeEach
    void setUp() {
      mutation = new LevyFlightMutation(0.5, 1.5, 0.1, mockRepair, mockRandom);
      setupSolutionVariables(List.of(0.5, 0.5, 0.5));
    }

    @Test
    @DisplayName("Execute throws exception for null solution")
    void executeThrowsForNullSolution() {
      assertThrows(JMetalException.class, () -> mutation.execute(null));
    }

    @Test
    @DisplayName("Execute returns the same solution instance")
    void executeReturnsSameInstance() {
      // Set up mock to return a value < 0.5 to trigger mutation
      when(mockRandom.getRandomValue()).thenReturn(0.1);

      // Mock the Gaussian values for the mutation
      when(mockRandom.getRandomValue())
          .thenReturn(0.5) // u1
          .thenReturn(0.5); // v1

      // Mock repair to return the same value
      when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
          .thenAnswer(inv -> inv.getArgument(0));

      DoubleSolution result = mutation.execute(mockSolution);
      assertSame(mockSolution, result);
    }

    @Test
    @DisplayName("Execute handles zero mutation probability correctly")
    void executeWithZeroProbability() {
      // Set mutation probability to 0.0
      mutation.mutationProbability(0.0);
      
      // The random generator will be called once per variable (3 times) to check mutation probability
      // We use values > 0.0 to ensure no mutations occur
      when(mockRandom.getRandomValue())
          .thenReturn(0.1)   // First variable - check mutation (0.1 > 0.0, no mutation)
          .thenReturn(0.2)   // Second variable - check mutation (0.2 > 0.0, no mutation)
          .thenReturn(0.3);  // Third variable - check mutation (0.3 > 0.0, no mutation)

      // Mock repair to verify it's never called
      when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
          .thenAnswer(inv -> {
            fail("Repair should not be called when mutation probability is 0.0");
            return 0.0; // This line is unreachable but needed for compilation
          });

      DoubleSolution result = mutation.execute(mockSolution);

      // Verify repair was never called
      verify(mockRepair, never()).repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble());
      
      // Verify all values remain unchanged
      for (int i = 0; i < 3; i++) {
        assertEquals(0.5, result.variables().get(i), 1e-10);
      }
    }

    @Test
    @DisplayName("Execute applies mutation with correct probability")
    void executeAppliesMutationWithProbability() {
        // First variable mutates (0.1 < 0.5), second doesn't (0.6 > 0.5), third does (0.4 < 0.5)
        // Each mutation requires 6 calls to getRandomValue():
        // 1. Check if variable should mutate (0.1, 0.6, 0.4)
        // 2. For each mutation, generateLevyStep() calls:
        //    - generateGaussian() calls getRandomValue() twice (u1, v1)
        //    - generateGaussian() calls getRandomValue() twice again (u2, v2)
        when(mockRandom.getRandomValue())
            .thenReturn(0.1)  // First variable - check mutation (mutate)
            .thenReturn(0.5)  // First Gaussian u1 (for first mutation)
            .thenReturn(0.5)  // First Gaussian v1
            .thenReturn(0.5)  // Second Gaussian u2
            .thenReturn(0.5)  // Second Gaussian v2
            .thenReturn(0.6)  // Second variable - check mutation (don't mutate)
            .thenReturn(0.4)  // Third variable - check mutation (mutate)
            .thenReturn(0.5)  // First Gaussian u1 (for third mutation)
            .thenReturn(0.5)  // First Gaussian v1
            .thenReturn(0.5)  // Second Gaussian u2
            .thenReturn(0.5); // Second Gaussian v2

        // Mock the repair to just return the value it receives (don't modify it)
        when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
            .thenAnswer(inv -> inv.getArgument(0));

        // Store original values for comparison
        double originalValue = 0.5;
        
        DoubleSolution result = mutation.execute(mockSolution);
        
        // Verify repair was called for first and third variables
        verify(mockRepair, times(2)).repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble());
        
        // Verify the second variable was not changed (should still be original value)
        assertEquals(originalValue, result.variables().get(1), 1e-10);
        
        // First and third variables should be different from original (but we don't know exact value due to Lévy step)
        assertNotEquals(originalValue, result.variables().get(0), 1e-10);
        assertNotEquals(originalValue, result.variables().get(2), 1e-10);
    }

    @Test
    @DisplayName("Execute handles empty solution correctly")
    void executeWithEmptySolution() {
      setupSolutionVariables(List.of());

      DoubleSolution result = mutation.execute(mockSolution);

      assertTrue(result.variables().isEmpty());
      verify(mockRandom, never()).getRandomValue();
    }
  }

  @Nested
  @DisplayName("Edge Case Tests")
  class EdgeCaseTests {

    @Test
    @DisplayName("Beta near 1.0 produces heavy-tailed distribution")
    void betaNearOneProducesHeavyTails() {
      mutation = new LevyFlightMutation(1.0, 1.01, 0.1, mockRepair, mockRandom);
      setupSolutionVariables(List.of(0.5));

      // Mock the random values for the Lévy step calculation
      when(mockRandom.getRandomValue())
          .thenReturn(0.1) // Mutation probability check
          .thenReturn(0.5) // u1
          .thenReturn(0.5); // v1

      when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
          .thenAnswer(inv -> inv.getArgument(0));

      mutation.execute(mockSolution);

      verify(mockRepair).repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Beta near 2.0 produces more Gaussian-like distribution")
    void betaNearTwoProducesGaussianLikeDistribution() {
      mutation = new LevyFlightMutation(1.0, 1.99, 0.1, mockRepair, mockRandom);
      setupSolutionVariables(List.of(0.5));

      // Mock the random values for the Lévy step calculation
      when(mockRandom.getRandomValue())
          .thenReturn(0.1) // Mutation probability check
          .thenReturn(0.5) // u1
          .thenReturn(0.5); // v1

      when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
          .thenAnswer(inv -> inv.getArgument(0));

      mutation.execute(mockSolution);

      verify(mockRepair).repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble());
    }

    @Test
    @DisplayName("Very small step size produces minimal changes")
    void verySmallStepSizeProducesMinimalChanges() {
      mutation = new LevyFlightMutation(1.0, 1.5, 1e-10, mockRepair, mockRandom);
      setupSolutionVariables(List.of(0.5));

      // Mock the random values for the Lévy step calculation
      when(mockRandom.getRandomValue())
          .thenReturn(0.1) // Mutation probability check
          .thenReturn(0.5) // u1
          .thenReturn(0.5); // v1

      when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
          .thenAnswer(inv -> inv.getArgument(0));

      DoubleSolution result = mutation.execute(mockSolution);

      // With very small step size, the change should be minimal
      assertEquals(0.5, result.variables().get(0), 1e-5);
    }

    @Test
    @DisplayName("Very large step size can produce large changes")
    void veryLargeStepSizeCanProduceLargeChanges() {
      mutation = new LevyFlightMutation(1.0, 1.5, 100.0, mockRepair, mockRandom);
      setupSolutionVariables(List.of(0.5));

      // Mock the random values for the Lévy step calculation
      when(mockRandom.getRandomValue())
          .thenReturn(0.1) // Mutation probability check
          .thenReturn(0.5) // u1
          .thenReturn(0.5); // v1

      // Mock repair to just return the value (don't clip)
      when(mockRepair.repairSolutionVariableValue(anyDouble(), anyDouble(), anyDouble()))
          .thenAnswer(inv -> inv.getArgument(0));

      DoubleSolution result = mutation.execute(mockSolution);

      // With very large step size, the change should be significant
      assertNotEquals(0.5, result.variables().get(0), 1.0);
    }
  }

  @Test
  @DisplayName("toString returns expected format")
  void toStringReturnsExpectedFormat() {
    mutation = new LevyFlightMutation(0.1, 1.5, 0.05);

    String result = mutation.toString();

    assertTrue(result.contains("LevyFlightMutation"));
    assertTrue(result.contains("mutationProbability=0.1"));
    assertTrue(result.contains("beta=1.5"));
    assertTrue(result.contains("stepSize=0.05"));
  }
}
