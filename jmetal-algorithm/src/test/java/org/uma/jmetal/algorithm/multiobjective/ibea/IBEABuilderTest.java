package org.uma.jmetal.algorithm.multiobjective.ibea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

class IBEABuilderTest {
  private IBEABuilder builder;
  private FakeDoubleProblem problem;
  private Fitness<DoubleSolution> fitness;

  @BeforeEach
  void setUp() {
    problem = new FakeDoubleProblem(2, 2, 0);
    builder = new IBEABuilder(problem);
    fitness = new Fitness<>();
  }

  @Test
  void buildReturnsPlainIBEAByDefault() {
    assertEquals(IBEABuilder.IBEAVariant.IBEA, builder.getVariant());
    assertEquals("IBEA", builder.build().name());
  }

  @Test
  void setVariantBuildsModifiedIBEA() {
    builder.setVariant(IBEABuilder.IBEAVariant.MIBEA);

    assertEquals(IBEABuilder.IBEAVariant.MIBEA, builder.getVariant());
    assertEquals("mIBEA", builder.build().name());
  }

  @Test
  void defaultSelectionUsesFitnessComparator() {
    DoubleSolution dominatingSolution = problem.createSolution();
    dominatingSolution.objectives()[0] = 0.0;
    dominatingSolution.objectives()[1] = 0.0;

    DoubleSolution lowerFitnessSolution = problem.createSolution();
    lowerFitnessSolution.objectives()[0] = 1.0;
    lowerFitnessSolution.objectives()[1] = 1.0;

    fitness.setAttribute(dominatingSolution, 2.0);
    fitness.setAttribute(lowerFitnessSolution, 1.0);

    DoubleSolution selectedSolution =
        builder.getSelection().execute(List.of(dominatingSolution, lowerFitnessSolution));

    assertSame(lowerFitnessSolution, selectedSolution);
  }
}
