package org.uma.jmetal.operator.crossover;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.uma.jmetal.operator.crossover.impl.UnimodalNormalDistributionCrossover;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.doubleproblem.impl.FakeDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.repairsolution.impl.RepairDoubleSolutionWithBoundValue;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

@ExtendWith(MockitoExtension.class)
class UnimodalNormalDistributionCrossoverTest {
  private static final double EPSILON = 0.0000000000001;
  private static final double DEFAULT_ZETA = 0.5;
  private static final double DEFAULT_ETA = 0.35;
  private static final double CROSSOVER_PROBABILITY = 0.9;

  @Test
  void shouldConstructorAssignTheCorrectProbabilityValue() {
    final double crossoverProbability = 0.25;
    final UnimodalNormalDistributionCrossover crossover = 
        new UnimodalNormalDistributionCrossover(crossoverProbability);
    assertEquals(crossoverProbability, crossover.crossoverProbability());
  }

  @Test
  void shouldConstructorAssignTheDefaultZetaValue() {
    final UnimodalNormalDistributionCrossover crossover = 
        new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY);
    assertEquals(DEFAULT_ZETA, crossover.zeta(), EPSILON);
  }

  @Test
  void shouldConstructorAssignTheDefaultEtaValue() {
    final UnimodalNormalDistributionCrossover crossover = 
        new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY);
    assertEquals(DEFAULT_ETA, crossover.eta(), EPSILON);
  }

  @Test
  void shouldConstructorAssignTheCorrectZetaValue() {
    final double zeta = 0.25;
    final UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(
            CROSSOVER_PROBABILITY, zeta, DEFAULT_ETA, new RepairDoubleSolutionWithBoundValue());
    assertEquals(zeta, crossover.zeta(), EPSILON);
  }

  @Test
  void shouldConstructorAssignTheCorrectEtaValue() {
    final double eta = 0.2;
    final UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(
            CROSSOVER_PROBABILITY, DEFAULT_ZETA, eta, new RepairDoubleSolutionWithBoundValue());
    assertEquals(eta, crossover.eta(), EPSILON);
  }

  @Test
  void shouldConstructorWhenPassedANegativeZetaValueThrowAnException() {
    final double zeta = -0.1;
    assertThrows(
        InvalidConditionException.class,
        () ->
            new UnimodalNormalDistributionCrossover(
                CROSSOVER_PROBABILITY, zeta, DEFAULT_ETA, new RepairDoubleSolutionWithBoundValue()));
  }

  @Test
  void shouldConstructorWhenPassedANegativeEtaValueThrowAnException() {
    final double eta = -0.1;
    assertThrows(
        InvalidConditionException.class,
        () ->
            new UnimodalNormalDistributionCrossover(
                CROSSOVER_PROBABILITY, DEFAULT_ZETA, eta, new RepairDoubleSolutionWithBoundValue()));
  }

  @Test
  void shouldConstructorWhenPassedNullRepairDoubleSolutionThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () -> new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY, DEFAULT_ZETA, DEFAULT_ETA, null));
  }

  @Test
  void shouldConstructorWhenPassedNullRandomGeneratorThrowAnException() {
    assertThrows(
        NullParameterException.class,
        () ->
            new UnimodalNormalDistributionCrossover(
                CROSSOVER_PROBABILITY,
                DEFAULT_ZETA,
                DEFAULT_ETA,
                new RepairDoubleSolutionWithBoundValue(),
                null));
  }

  @Test
  void shouldExecuteWithThreeParentsReturnTwoOffspring() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY);

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    DoubleSolution parent3 = problem.createSolution();
    
    parent1.variables().set(0, 1.0);
    parent2.variables().set(0, 2.0);
    parent3.variables().set(0, 1.5);

    List<DoubleSolution> parents = List.of(parent1, parent2, parent3);
    List<DoubleSolution> offspring = crossover.execute(parents);

    assertNotNull(offspring);
    assertEquals(2, offspring.size());
    assertNotSame(parent1, offspring.get(0));
    assertNotSame(parent2, offspring.get(1));
  }

  @Test
  void shouldExecuteWithIdenticalParentsReturnOffspringWithSameValues() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    // Using zero zeta and eta to ensure no variation
    UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(1.0, 0.0, 0.0, new RepairDoubleSolutionWithBoundValue());

    DoubleSolution parent1 = problem.createSolution();
    DoubleSolution parent2 = problem.createSolution();
    DoubleSolution parent3 = problem.createSolution();
    
    double value = 1.5;
    parent1.variables().set(0, value);
    parent2.variables().set(0, value);
    parent3.variables().set(0, value);

    List<DoubleSolution> parents = List.of(parent1, parent2, parent3);
    List<DoubleSolution> offspring = crossover.execute(parents);

    assertEquals(value, offspring.get(0).variables().get(0), EPSILON);
    assertEquals(value, offspring.get(1).variables().get(0), EPSILON);
  }

  @Test
  void shouldExecuteWithLessThanThreeParentsThrowAnException() {
    DoubleProblem problem = new FakeDoubleProblem(1, 2, 0);
    UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY);

    List<DoubleSolution> parents = List.of(problem.createSolution(), problem.createSolution());
    
    assertThrows(InvalidConditionException.class, () -> crossover.execute(parents));
  }

  @Test
  void shouldGetNumberOfRequiredParentsReturnTheRightValue() {
    UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY);
    assertEquals(3, crossover.numberOfRequiredParents());
  }

  @Test
  void shouldGetNumberOfGeneratedChildrenReturnTheRightValue() {
    UnimodalNormalDistributionCrossover crossover =
        new UnimodalNormalDistributionCrossover(CROSSOVER_PROBABILITY);
    assertEquals(2, crossover.numberOfGeneratedChildren());
  }
}
