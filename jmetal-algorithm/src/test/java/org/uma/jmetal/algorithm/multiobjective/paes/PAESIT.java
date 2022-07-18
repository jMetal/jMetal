package org.uma.jmetal.algorithm.multiobjective.paes;

import static org.junit.Assert.assertTrue;
import static org.uma.jmetal.util.AbstractAlgorithmRunner.printFinalSolutionSet;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.archive.impl.GenericBoundedArchive;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;

public class PAESIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    var problem = new Kursawe();
    CrossoverOperator<DoubleSolution> crossover;

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PAES<>(problem, 25000, 100, 5, mutation);

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is Kursawe, and usually PAES, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 99);
  }

  @Test
  @Ignore
  public void shouldTheHypervolumeHaveAMinimumValue() throws Exception {
    var problem = new ZDT1();

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PAES<>(problem, 25000, 100, 5, mutation);
    algorithm.run();

    var population = algorithm.getResult();

    org.uma.jmetal.qualityindicator.QualityIndicator hypervolume =
            new org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and OMOPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.6);
  }

  @Test
  @Ignore
  public void shouldTheCrowdingDistanceVariantWorkProperly() throws Exception {
    var problem = new ZDT1();

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm =
        new PAES<>(
            problem,
            25000,
            new GenericBoundedArchive<>(100, new CrowdingDistanceDensityEstimator<>()),
            mutation);
    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT1.csv", ","));

    // Rationale: the default problem is ZDT1, and PAES, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.6

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.6);
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem()
      throws Exception {
    var problem = new ConstrEx();
    CrossoverOperator<DoubleSolution> crossover;

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PAES<>(problem, 25000, 100, 5, mutation);

    algorithm.run();

    var population = algorithm.getResult() ;

    var referenceFrontFileName = "../resources/referenceFrontsCSV/ConstrEx.csv" ;

    printFinalSolutionSet(population);

    var referenceFront = VectorUtils.readVectors(referenceFrontFileName, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    // Rationale: the default problem is ConstrEx, and APES, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.7

    var normalizedFront =
            NormalizeUtils.normalize(
                    SolutionListUtils.getMatrixWithObjectiveValues(population),
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));


    var hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 85) ;
    assertTrue(hv > 0.7) ;
  }
}
