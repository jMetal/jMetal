package org.uma.jmetal.algorithm.multiobjective.abyss;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.localsearch.LocalSearchOperator;
import org.uma.jmetal.operator.localsearch.impl.BasicLocalSearch;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.Tanaka;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.comparator.constraintcomparator.impl.OverallConstraintViolationDegreeComparator;

/**
 * Created by ajnebro on 11/6/15.
 */
public class ABYSSConstrainedProblemIT {

  Algorithm<List<DoubleSolution>> algorithm;
  DoubleProblem problem;
  CrossoverOperator<DoubleSolution> crossover;
  MutationOperator<DoubleSolution> mutation;
  LocalSearchOperator<DoubleSolution> localSearchOperator;
  Archive<DoubleSolution> archive;

  @Before
  public void setup() {
    problem = new Tanaka();

    double crossoverProbability = 1.0;
    double crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    archive = new CrowdingDistanceArchive<>(100);

    Comparator<DoubleSolution> comparator = new DominanceWithConstraintsComparator<>(
        new OverallConstraintViolationDegreeComparator<>());

    localSearchOperator = new BasicLocalSearch<>(1, mutation, comparator, problem);
  }

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingAConstrainedProblem() {
    int populationSize = 10;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 4;
    int referenceSet2Size = 4;

    algorithm =
        new ABYSS(
            problem,
            25000,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            100,
            archive,
            localSearchOperator,
            crossover,
            numberOfSubRanges);

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    /*
    Rationale: the default problem is Tanaka, and AbYSS, configured with standard settings, should
    return more than 90 solutions
    */
    assertTrue(population.size() >= 90);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValue() throws Exception {
    int populationSize = 10;
    int numberOfSubRanges = 4;
    int referenceSet1Size = 4;
    int referenceSet2Size = 4;

    algorithm =
        new ABYSS(
            problem,
            25000,
            populationSize,
            referenceSet1Size,
            referenceSet2Size,
            100,
            archive,
            localSearchOperator,
            crossover,
            numberOfSubRanges);

    algorithm.run();

    List<DoubleSolution> population = algorithm.getResult();

    QualityIndicator hypervolume =
        new PISAHypervolume(
            VectorUtils.readVectors("../resources/referenceFrontsCSV/Tanaka.csv", ","));

    // Rationale: the default problem is Tanaka, and AbYSS, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.22

    double hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.22);
  }
}
