package org.uma.jmetal.operator.crossover;

import org.junit.Test;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    assertEquals(1.0, crossover.getCrossoverProbability(), EPSILON);
  }

  @Test
  public void shouldGetNumberOfGeneratedChildrenReturnOne() {
    DifferentialEvolutionCrossover crossover = new DifferentialEvolutionCrossover();
    assertEquals(1.0, crossover.getNumberOfGeneratedChildren(), EPSILON);
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
