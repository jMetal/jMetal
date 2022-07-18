package org.uma.jmetal.algorithm.multiobjective.mombi2;

import static org.junit.Assert.assertTrue;

import java.util.List;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mombi.MOMBI2;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

public class MOMBI2IT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    var problem = new DTLZ1() ;

    var randomGenerator = JMetalRandom.getInstance() ;
    randomGenerator.setSeed(1450278534242L);

    var crossoverProbability = 0.9 ;
    var crossoverDistributionIndex = 20.0 ;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<>());

    algorithm = new MOMBI2<>(problem,400,crossover,mutation,selection,new SequentialSolutionListEvaluator<>(),
        "../resources/weightVectorFiles/mombi2/weight_03D_12.sld");
    algorithm.run();

    var population = algorithm.getResult() ;

    /*
    Rationale: the default problem is DTLZ1, and MOMBI2, configured with standard
    settings, should return more than 90 solutions
    */
    assertTrue(population.size() >= 91) ;

    randomGenerator.setSeed(System.currentTimeMillis());
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    var problem = new DTLZ1() ;

    var randomGenerator = JMetalRandom.getInstance() ;
    randomGenerator.setSeed(1450278534242L);

    var crossoverProbability = 0.9 ;
    var crossoverDistributionIndex = 20.0 ;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    algorithm = new MOMBI2<>(problem,400,crossover,mutation,selection,new SequentialSolutionListEvaluator<DoubleSolution>(),
        "../resources/weightVectorFiles/mombi2/weight_03D_12.sld");
    algorithm.run();

    var population = algorithm.getResult() ;

    /*
    Rationale: the default problem is DTLZ1, and MOMBI2, configured with standard
    settings, should return 100 solutions
    */
    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/DTLZ1.3D.csv", ","));

    // Rationale: the default problem is DTLZ1 (3 objectives), and MOMBI2, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.96

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.96) ;

    randomGenerator.setSeed(System.currentTimeMillis());
  }
}
