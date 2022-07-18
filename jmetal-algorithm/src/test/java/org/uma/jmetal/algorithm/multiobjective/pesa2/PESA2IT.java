package org.uma.jmetal.algorithm.multiobjective.pesa2;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.multiobjective.ConstrEx;
import org.uma.jmetal.problem.multiobjective.Kursawe;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;

public class PESA2IT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    var problem = new Kursawe();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PESA2Builder<>(problem, crossover, mutation).build();

    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is Kursawe, and usually PESA2, configured with standard
    settings, should return 100 solutions
    */
    assertTrue(population.size() >= 99);
  }

  @Test
  public void shouldTheAlgorithmReturnAGoodQualityFrontWhenSolvingAConstrainedProblem()
      throws Exception {
    var problem = new ConstrEx();

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    algorithm = new PESA2Builder<DoubleSolution>(problem, crossover, mutation).build();

    algorithm.run();

    var population = algorithm.getResult() ;

    var referenceFrontFileName = "../resources/referenceFrontsCSV/ConstrEx.csv" ;

    var referenceFront = VectorUtils.readVectors(referenceFrontFileName, ",") ;
    QualityIndicator hypervolume = new PISAHypervolume(referenceFront);

    // Rationale: the default problem is ConstrEx, and PESA-II, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.7

    var normalizedFront =
            NormalizeUtils.normalize(
                    SolutionListUtils.getMatrixWithObjectiveValues(population),
                    NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
                    NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    var hv = hypervolume.compute(normalizedFront);

    assertTrue(population.size() >= 98);
    assertTrue(hv > 0.7);
  }
}
