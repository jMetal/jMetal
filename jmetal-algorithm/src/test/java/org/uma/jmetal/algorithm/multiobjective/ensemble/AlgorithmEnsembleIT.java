package org.uma.jmetal.algorithm.multiobjective.ensemble;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.checking.exception.InvalidConditionException;
import org.uma.jmetal.util.checking.exception.NullParameterException;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AlgorithmEnsembleIT {

  @Test(expected = NullParameterException.class)
  public void shouldConstructorRaiseAnExceptionIfTheAlgorithmListIsNull() {
    new AlgorithmEnsemble<DoubleSolution>(null, new NonDominatedSolutionListArchive<>());
  }

  @Test(expected = NullParameterException.class)
  public void shouldConstructorRaiseAnExceptionIfTheArchiveIsNull() {
    List<Algorithm<List<DoubleSolution>>> algorithmArrayList = new ArrayList<>();
    algorithmArrayList.add(null);
    new AlgorithmEnsemble<DoubleSolution>(algorithmArrayList, null);
  }

  @Test(expected = InvalidConditionException.class)
  public void shouldConstructorRaiseAnExceptionIfTheListOfAlgorithmsIsEmpty() {
    List<Algorithm<List<DoubleSolution>>> algorithmArrayList = new ArrayList<>();
    new AlgorithmEnsemble<DoubleSolution>(
        algorithmArrayList, new NonDominatedSolutionListArchive<>());
  }

  @Test
  public void shouldConstructorCreateAValidAlgorithmListWithOneAlgorithm() {
    Problem<DoubleSolution> problem = new ZDT1();
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 240;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(20000);

    Algorithm<List<DoubleSolution>> algorithm =
        new NSGAII<>(
            problem,
            populationSize,
            offspringPopulationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination);

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(algorithm);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, new NonDominatedSolutionListArchive<>());

    assertEquals(1, algorithmEnsemble.getAlgorithmList().size());
  }

  @Test
  public void shouldEnsembleWithOneAlgorithmReturnAValidResult() {
    Problem<DoubleSolution> problem = new ZDT1();
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(20000);

    Algorithm<List<DoubleSolution>> nsgaII =
        new NSGAII<>(
            problem,
            populationSize,
            offspringPopulationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination);

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(nsgaII);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
        new AlgorithmEnsemble<>(algorithmList, new NonDominatedSolutionListArchive<>());

    algorithmEnsemble.run();

    assertTrue(algorithmEnsemble.getResult().size() >= (populationSize - 5));
    for (DoubleSolution solution : algorithmEnsemble.getResult()) {
      assertEquals(nsgaII.getName(), solution.getAttribute("ALGORITHM_NAME"));
    }
  }

  @Test
  public void shouldEnsembleWithTwoAlgorithmsReturnAValidResult() {
    Problem<DoubleSolution> problem = new ZDT1();
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(20000);

    Algorithm<List<DoubleSolution>> nsgaII =
        new NSGAII<>(
            problem,
            populationSize,
            offspringPopulationSize,
            new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            termination);

    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
        new CrowdingDistanceArchive<DoubleSolution>(swarmSize);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

    Algorithm<List<DoubleSolution>> smpso =
        new SMPSO(
            (DoubleProblem) problem,
            swarmSize,
            leadersArchive,
            new PolynomialMutation(mutationProbability, mutationDistributionIndex),
            evaluation,
            termination);

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
}
