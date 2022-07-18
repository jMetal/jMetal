package org.uma.jmetal.algorithm.multiobjective.abyss;

import static org.junit.Assert.assertTrue;

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
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;

/** Created by ajnebro on 11/6/15. */
public class ABYSSIT {
  Algorithm<List<DoubleSolution>> algorithm;
  DoubleProblem problem;
  CrossoverOperator<DoubleSolution> crossover;
  MutationOperator<DoubleSolution> mutation;
  LocalSearchOperator<DoubleSolution> localSearchOperator;
  Archive<DoubleSolution> archive;

  @Before
  public void setup() {
    problem = new ZDT1();

    var crossoverProbability = 1.0;
    var crossoverDistributionIndex = 20.0;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    archive = new CrowdingDistanceArchive<>(100);

    localSearchOperator = new BasicLocalSearch<>(1, mutation, new DominanceWithConstraintsComparator<>(), problem);
  }

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    var populationSize = 10;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 4;
    var referenceSet2Size = 4;

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

    algorithm = new ABYSSBuilder(problem, archive).build();

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT4, and AbYSS, configured with standard settings, should
    return at least solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMinimumValue() throws Exception {
    var populationSize = 10;
    var numberOfSubRanges = 4;
    var referenceSet1Size = 4;
    var referenceSet2Size = 4;

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

    var population = algorithm.getResult();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));
    
    // Rationale: the default problem is ZDT1, and AbYSS, configured with standard settings,
    // should return find a front with a hypervolume value higher than 0.64

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }
}
