package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;
import org.uma.jmetal.util.pseudorandom.BoundedRandomGenerator;

public class DifferentialEvolutionCrossoverTest {
  private double EPSILON = 0.0000000001;

  @Test
  public void shouldDefaultConstructorCreateADefaultOperator() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();

    assertNotNull(crossover);
    assertEquals(0.5, crossover.getCr(), EPSILON);
    assertEquals(0.5, crossover.getF(), EPSILON);
    assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN, crossover.getVariant());
  }

  @Test
  public void shouldSetCrChangeTheCrParameter() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();
    crossover.setCr(0.9);

    assertEquals(0.9, crossover.getCr(), EPSILON);
  }

  @Test
  public void shouldSetCrChangeTheFParameter() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();
    crossover.setF(0.9);

    assertEquals(0.9, crossover.getF(), EPSILON);
  }

  @Test
  public void shouldGetCrossoverProbabilityReturnOne() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();
    assertEquals(1.0, crossover.crossoverProbability(), EPSILON);
  }

  @Test
  public void shouldGetNumberOfGeneratedChildrenReturnOne() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();
    assertEquals(1.0, crossover.numberOfGeneratedChildren(), EPSILON);
  }

  @Test
  public void shouldGetVariantFromStringParseTheNameOfEveryVariant() {
    for (DifferentialEvolutionCrossover.DE_VARIANT variant :
        DifferentialEvolutionCrossover.DE_VARIANT.values()) {
      assertEquals(variant, DifferentialEvolutionCrossover.getVariantFromString(variant.name()));
    }
  }

  @Test
  public void shouldRAND_1_BINVariantBeCorrectlyParsed() {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN);

    assertEquals(1, crossover.getNumberOfDifferenceVectors());
    assertEquals(
        DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.BIN, crossover.getCrossoverType());
    assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.RAND, crossover.getMutationType());
    assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN, crossover.getVariant());
  }

  @Test
  public void shouldRAND_1_EXPVariantBeCorrectlyParsed() {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_EXP);

    assertEquals(1, crossover.getNumberOfDifferenceVectors());
    assertEquals(
        DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.EXP, crossover.getCrossoverType());
    assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.RAND, crossover.getMutationType());
    assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_EXP, crossover.getVariant());
  }

    @Test
    public void shouldRAND_2_BINVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.RAND_2_BIN);

        assertEquals(2, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.BIN, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.RAND, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.RAND_2_BIN, crossover.getVariant());
    }

    @Test
    public void shouldRAND_2_EXPVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.RAND_2_EXP);

        assertEquals(2, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.EXP, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.RAND, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.RAND_2_EXP, crossover.getVariant());
    }

    @Test
    public void shouldBEST_1_BINVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.BEST_1_BIN);

        assertEquals(1, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.BIN, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.BEST, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.BEST_1_BIN, crossover.getVariant());
    }

    @Test
    public void shouldBEST_1_EXPVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.BEST_1_EXP);

        assertEquals(1, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.EXP, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.BEST, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.BEST_1_EXP, crossover.getVariant());
    }


    @Test
    public void shouldBEST_2_BINVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.BEST_2_BIN);

        assertEquals(2, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.BIN, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.BEST, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.BEST_2_BIN, crossover.getVariant());
    }

    @Test
    public void shouldBEST_2_EXPVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.BEST_2_EXP);

        assertEquals(2, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.EXP, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.BEST, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.BEST_2_EXP, crossover.getVariant());
    }

    @Test
    public void shouldRAND_TO_BEST_1_BINVariantBeCorrectlyParsed() {
        DifferentialEvolutionCrossover crossover =
                new DifferentialEvolutionCrossover(
                        0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.RAND_TO_BEST_1_BIN);

        assertEquals(1, crossover.getNumberOfDifferenceVectors());
        assertEquals(
                DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.BIN, crossover.getCrossoverType());
        assertEquals(DifferentialEvolutionCrossover.DE_MUTATION_TYPE.RAND_TO_BEST, crossover.getMutationType());
        assertEquals(DifferentialEvolutionCrossover.DE_VARIANT.RAND_TO_BEST_1_BIN, crossover.getVariant());
    }

  @Test
  public void shouldCURRENT_TO_RAND_1_BINVariantBeCorrectlyParsed() {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.CURRENT_TO_RAND_1_BIN);

    assertEquals(1, crossover.getNumberOfDifferenceVectors());
    assertEquals(
        DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.BIN, crossover.getCrossoverType());
    assertEquals(
        DifferentialEvolutionCrossover.DE_MUTATION_TYPE.CURRENT_TO_RAND,
        crossover.getMutationType());
  }

  @Test
  public void shouldCURRENT_TO_RAND_1_EXPVariantBeCorrectlyParsed() {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(
            0.1, 0.1, DifferentialEvolutionCrossover.DE_VARIANT.CURRENT_TO_RAND_1_EXP);

    assertEquals(1, crossover.getNumberOfDifferenceVectors());
    assertEquals(
        DifferentialEvolutionCrossover.DE_CROSSOVER_TYPE.EXP, crossover.getCrossoverType());
    assertEquals(
        DifferentialEvolutionCrossover.DE_MUTATION_TYPE.CURRENT_TO_RAND,
        crossover.getMutationType());
  }

  private static DoubleSolution solution(double... variables) {
    DoubleSolution solution =
        new DefaultDoubleSolution(
            List.of(Bounds.create(-10.0, 10.0), Bounds.create(-10.0, 10.0)), 1, 0);
    for (int i = 0; i < variables.length; i++) {
      solution.variables().set(i, variables[i]);
    }
    return solution;
  }

  /**
   * With CR = 1 every variable comes from the mutant x + F (r3 - x) + F (r1 - r2): with x = (1, 2),
   * r1 = (3, 5), r2 = (1, 1), r3 = (2, 4) and F = 0.5, the child is (2.5, 5).
   */
  private static void assertCurrentToRandChild(DifferentialEvolutionCrossover.DE_VARIANT variant) {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(1.0, 0.5, variant, (a, b) -> a, (a, b) -> a);
    crossover.setCurrentSolution(solution(1.0, 2.0));

    DoubleSolution child =
        crossover
            .execute(List.of(solution(3.0, 5.0), solution(1.0, 1.0), solution(2.0, 4.0)))
            .get(0);

    assertEquals(2.5, child.variables().get(0), 1e-12);
    assertEquals(5.0, child.variables().get(1), 1e-12);
  }

  @Test
  public void shouldCURRENT_TO_RAND_1_BINVariantApplyTheCurrentToRandMutation() {
    assertCurrentToRandChild(DifferentialEvolutionCrossover.DE_VARIANT.CURRENT_TO_RAND_1_BIN);
  }

  @Test
  public void shouldCURRENT_TO_RAND_1_EXPVariantApplyTheCurrentToRandMutation() {
    assertCurrentToRandChild(DifferentialEvolutionCrossover.DE_VARIANT.CURRENT_TO_RAND_1_EXP);
  }

  /**
   * Executes the crossover with F = 0.5, the current solution x = (1, 2), the best one (4, 6) and
   * the parents r1 = (3, 5), r2 = (1, 1), r3 = (2, 4), r4 = (0, 1) and r5 = (5, 3), of which the
   * variant takes the first ones that it needs.
   */
  private static DoubleSolution execute(
      DifferentialEvolutionCrossover.DE_VARIANT variant,
      double cr,
      BoundedRandomGenerator<Integer> jRandomGenerator,
      BoundedRandomGenerator<Double> crRandomGenerator) {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(cr, 0.5, variant, jRandomGenerator, crRandomGenerator);
    crossover.setCurrentSolution(solution(1.0, 2.0));
    crossover.setBestSolution(solution(4.0, 6.0));

    List<DoubleSolution> parents =
        List.of(
            solution(3.0, 5.0),
            solution(1.0, 1.0),
            solution(2.0, 4.0),
            solution(0.0, 1.0),
            solution(5.0, 3.0));
    return crossover.execute(parents.subList(0, crossover.numberOfRequiredParents())).get(0);
  }

  @ParameterizedTest(name = "{0}")
  @CsvSource({
    "RAND_1_BIN, 3.0, 6.0",
    "RAND_1_EXP, 3.0, 6.0",
    "RAND_2_BIN, 7.0, 6.5",
    "RAND_2_EXP, 7.0, 6.5",
    "BEST_1_BIN, 5.0, 8.0",
    "BEST_1_EXP, 5.0, 8.0",
    "BEST_2_BIN, 6.0, 9.5",
    "BEST_2_EXP, 6.0, 9.5",
    "RAND_TO_BEST_1_BIN, 3.5, 6.0",
    "RAND_TO_BEST_1_EXP, 3.5, 6.0"
  })
  public void shouldEveryVariantBuildTheChildFromItsMutant(
      DifferentialEvolutionCrossover.DE_VARIANT variant, double expected0, double expected1) {
    DoubleSolution child = execute(variant, 1.0, (a, b) -> a, (a, b) -> a);

    assertEquals(expected0, child.variables().get(0), 1e-12);
    assertEquals(expected1, child.variables().get(1), 1e-12);
  }

  @ParameterizedTest(name = "{0}")
  @CsvSource({"RAND_1_BIN, 6.0", "BEST_1_BIN, 8.0", "RAND_TO_BEST_1_BIN, 6.0"})
  public void shouldTheBinCrossoverChangeOnlyTheVariableOfJrandWhenCrIsZero(
      DifferentialEvolutionCrossover.DE_VARIANT variant, double expected1) {
    // jrand is the last variable
    DoubleSolution child = execute(variant, 0.0, (a, b) -> b, (a, b) -> 0.5);

    assertEquals(1.0, child.variables().get(0), 1e-12);
    assertEquals(expected1, child.variables().get(1), 1e-12);
  }

  @Test
  public void shouldTheExpCrossoverChangeOnlyOneVariableWhenTheProbabilityIsNotReached() {
    DoubleSolution child =
        execute(
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_EXP, 0.5, (a, b) -> b, (a, b) -> b);

    assertEquals(1.0, child.variables().get(0), 1e-12);
    assertEquals(6.0, child.variables().get(1), 1e-12);
  }

  @Test
  public void shouldTheExpCrossoverWrapAroundTheVariables() {
    int[] draws = {0};
    // The crossover starts at the last variable and, as the probability is reached, goes on with
    // the first one
    DoubleSolution child =
        execute(
            DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_EXP,
            0.5,
            (a, b) -> b,
            (a, b) -> draws[0]++ == 0 ? a : b);

    assertEquals(3.0, child.variables().get(0), 1e-12);
    assertEquals(6.0, child.variables().get(1), 1e-12);
  }

  @ParameterizedTest(name = "{0}")
  @CsvSource({"BEST_1_BIN", "BEST_2_EXP", "RAND_TO_BEST_1_BIN"})
  public void shouldTheVariantsThatNeedTheBestSolutionRejectItsAbsence(
      DifferentialEvolutionCrossover.DE_VARIANT variant) {
    DifferentialEvolutionCrossover crossover =
        new DifferentialEvolutionCrossover(1.0, 0.5, variant, (a, b) -> a, (a, b) -> a);
    crossover.setCurrentSolution(solution(1.0, 2.0));
    List<DoubleSolution> parents =
        List.of(
            solution(3.0, 5.0),
            solution(1.0, 1.0),
            solution(2.0, 4.0),
            solution(0.0, 1.0),
            solution(5.0, 3.0));

    assertThrows(
        NullParameterException.class,
        () -> crossover.execute(parents.subList(0, crossover.numberOfRequiredParents())));
  }

  /*
  	@Test
  	public void shouldJMetalRandomGeneratorNotBeUsedWhenCustomRandomGeneratorProvided() {
  		// Configuration
  		double cr = 0.5;
  		double f = 0.5;
  		DifferentialEvolutionCrossover.DE_VARIANT variant = DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN ;

  		List<Pair<Double, Double>> bounds = Arrays.asList(new ImmutablePair<>(0.0, 1.0)) ;

  		DoubleSolution currentSolution = new DefaultDoubleSolution(bounds, 2) ;
  		List<DoubleSolution> parentSolutions = new LinkedList<>();

  		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));
  		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));
  		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));
  		parentSolutions.add(new DefaultDoubleSolution(bounds, 2));

  		// Check configuration leads to use default generator by default
  		final int[] defaultUses = { 0 };
  		JMetalRandom defaultGenerator = JMetalRandom.getInstance();
  		AuditableRandomGenerator auditor = new AuditableRandomGenerator(defaultGenerator.getRandomGenerator());
  		defaultGenerator.setRandomGenerator(auditor);
  		auditor.addListener((a) -> defaultUses[0]++);

  		DifferentialEvolutionCrossover crossover1 = new DifferentialEvolutionCrossover(cr, f, variant);
  		crossover1.setCurrentSolution(currentSolution);
  		crossover1.execute(parentSolutions);
  		assertTrue("No use of the default generator", defaultUses[0] > 0);

  		// Test same configuration uses custom generator instead
  		defaultUses[0] = 0;
  		final int[] custom1Uses = { 0 };
  		final int[] custom2Uses = { 0 };
  		DifferentialEvolutionCrossover crossover2 = new DifferentialEvolutionCrossover(cr, f, variant, (a, b) -> {
  			custom1Uses[0]++;
  			return new Random().nextInt(b - a + 1) + a;
  		}, (a, b) -> {
  			custom2Uses[0]++;
  			return new Random().nextDouble() * (b - a) + a;
  		}, new RepairDoubleSolutionWithBoundValue());
  		crossover2.setCurrentSolution(currentSolution);
  		crossover2.execute(parentSolutions);
  		assertTrue("Default random generator used", defaultUses[0] == 0);
  		assertTrue("No use of the custom generator 1", custom1Uses[0] > 0);
  		assertTrue("No use of the custom generator 2", custom2Uses[0] > 0);
  	}
  */
}
