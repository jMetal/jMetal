package org.uma.jmetal.experimental.ensemble;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.errorchecking.exception.InvalidConditionException;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

public class AlgorithmEnsembleIT {

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheAlgorithmListIsNull() {
    assertThrows(NullParameterException.class,
        () -> new AlgorithmEnsemble<DoubleSolution>(null, new NonDominatedSolutionListArchive<>()));
  }

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheArchiveIsNull() {
    List<Algorithm<List<DoubleSolution>>> algorithmArrayList = new ArrayList<>();
    algorithmArrayList.add(null);
    assertThrows(NullParameterException.class,
        () -> new AlgorithmEnsemble<DoubleSolution>(algorithmArrayList, null));
  }

  @Test
  public void shouldConstructorRaiseAnExceptionIfTheListOfAlgorithmsIsEmpty() {
    List<Algorithm<List<DoubleSolution>>> algorithmArrayList = new ArrayList<>();
    assertThrows(InvalidConditionException.class, () -> new AlgorithmEnsemble<DoubleSolution>(
        algorithmArrayList, new NonDominatedSolutionListArchive<>()));
  }

  @Test
  public void shouldConstructorCreateAValidAlgorithmListWithOneAlgorithm() {
    var problem = new ZDT1();
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    int populationSize = 240;
    int offspringPopulationSize = populationSize;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Algorithm<List<DoubleSolution>> algorithm =
        new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize).build();

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(algorithm);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, new NonDominatedSolutionListArchive<>());

    assertEquals(1, algorithmEnsemble.getAlgorithmList().size());
  }

  @Test
  public void shouldEnsembleWithOneAlgorithmReturnAValidResult() {
    Problem<DoubleSolution> problem = new ZDT1();
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    int populationSize = 240;
    int offspringPopulationSize = populationSize;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Algorithm<List<DoubleSolution>> nsgaii =
        new NSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, populationSize).build();

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(nsgaii);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, new NonDominatedSolutionListArchive<>());

    algorithmEnsemble.run();

    assertTrue(algorithmEnsemble.getResult().size() >= (populationSize - 5));
    for (DoubleSolution solution : algorithmEnsemble.getResult()) {
      assertEquals(nsgaii.getName(), solution.attributes().get("ALGORITHM_NAME"));
    }
  }

  /*
  @Test
  public void shouldEnsembleWithTwoAlgorithmsReturnAValidResult() {
    Problem<DoubleSolution> problem = new ZDT1();
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;

    int populationSize = 240;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Algorithm<List<DoubleSolution>> nsgaII =
        new NSGAIIBuilder<>(problem, crossover, mutation, populationSize).build();

    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
        new CrowdingDistanceArchive<DoubleSolution>(swarmSize);

    Algorithm<List<DoubleSolution>> smpso =
        new SMPSOBuilder((DoubleProblem) problem, leadersArchive).build();

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(nsgaII);
    algorithmList.add(smpso);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, new NonDominatedSolutionListArchive<>());

    algorithmEnsemble.run();

    assertEquals(2, algorithmEnsemble.getAlgorithmList().size());
    assertTrue(algorithmEnsemble.getResult().size() >= (populationSize - 5));
    for (DoubleSolution solution : algorithmEnsemble.getResult()) {
      assertTrue(
          (nsgaII.getName() == solution.getAttribute("ALGORITHM_NAME"))
              || (smpso.getName() == solution.getAttribute("ALGORITHM_NAME")));
    }
  }

   */
}
