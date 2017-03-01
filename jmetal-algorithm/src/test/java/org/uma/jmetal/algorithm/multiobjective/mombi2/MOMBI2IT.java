package org.uma.jmetal.algorithm.multiobjective.mombi2;

import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mombi.MOMBI2;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class MOMBI2IT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
    DTLZ1 problem = new DTLZ1() ;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    JMetalRandom randomGenerator = JMetalRandom.getInstance() ;
    randomGenerator.setSeed(1450278534242L);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    algorithm = new MOMBI2<>(problem,400,crossover,mutation,selection,new SequentialSolutionListEvaluator<DoubleSolution>(),
        "mombi2-weights/weight/weight_03D_12.sld");
    new AlgorithmRunner.Executor(algorithm).execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;

    /*
    Rationale: the default problem is DTLZ1, and MOMBI2, configured with standard
    settings, should return more than 90 solutions
    */
    assertTrue(population.size() >= 91) ;

    randomGenerator.setSeed(System.currentTimeMillis());
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DTLZ1 problem = new DTLZ1() ;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;

    JMetalRandom randomGenerator = JMetalRandom.getInstance() ;
    randomGenerator.setSeed(1450278534242L);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    algorithm = new MOMBI2<>(problem,400,crossover,mutation,selection,new SequentialSolutionListEvaluator<DoubleSolution>(),
        "mombi2-weights/weight/weight_03D_12.sld");
    new AlgorithmRunner.Executor(algorithm).execute() ;

    List<DoubleSolution> population = algorithm.getResult() ;

    /*
    Rationale: the default problem is DTLZ1, and MOMBI2, configured with standard
    settings, should return 100 solutions
    */
    QualityIndicator<List<DoubleSolution>, Double> hypervolume =
        new PISAHypervolume<>("/pareto_fronts/DTLZ1.3D.pf") ;

    // Rationale: the default problem is ZDT4, and SMPSO, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.96

    double hv = (Double)hypervolume.evaluate(population) ;

    assertTrue(hv > 0.968) ;

    randomGenerator.setSeed(System.currentTimeMillis());
  }
}
