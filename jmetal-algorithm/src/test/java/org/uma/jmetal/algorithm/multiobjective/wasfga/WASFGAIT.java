package org.uma.jmetal.algorithm.multiobjective.wasfga;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.VectorUtils;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public class WASFGAIT {

  @Test(expected = Exception.class)
  @Ignore
  public void shouldTheAlgorithmReturnAnExceptionIfIndicatingANonExistingWeightVectorFile() {
    DoubleProblem problem = new ZDT1();

      List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
              new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    var epsilon = 0.01;
    new WASFGA<DoubleSolution>(
        problem,
        100,
        250,
        crossover,
        mutation,
        selection,
        new SequentialSolutionListEvaluator<DoubleSolution>(),
        epsilon,
        referencePoint,
        "nonexistingfilename");
  }

  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem()
      throws Exception {
    DoubleProblem problem = new ZDT1();

      List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
              new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    var epsilon = 0.01;

      Algorithm<List<DoubleSolution>> algorithm = new WASFGA<DoubleSolution>(
              problem,
              100,
              250,
              crossover,
              mutation,
              selection,
              new SequentialSolutionListEvaluator<DoubleSolution>(),
              epsilon,
              referencePoint);
    algorithm.run();

    var population = algorithm.getResult();

    /*
    Rationale: the default problem is ZDT1, and WASFGA, configured with standard settings, should
    return 100 solutions
    */
    assertTrue(population.size() >= 98);
  }

  @Test
  public void shouldTheHypervolumeHaveAMininumValue() throws Exception {
    DoubleProblem problem = new ZDT1();

      List<Double> referencePoint = new ArrayList<>();
    referencePoint.add(0.0);
    referencePoint.add(0.0);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
              new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    var epsilon = 0.01;

      Algorithm<List<DoubleSolution>> algorithm = new WASFGA<DoubleSolution>(
              problem,
              100,
              250,
              crossover,
              mutation,
              selection,
              new SequentialSolutionListEvaluator<DoubleSolution>(),
              epsilon,
              referencePoint);
    algorithm.run();

    var population = algorithm.getResult();

    QualityIndicator hypervolume =
            new PISAHypervolume(
                    VectorUtils.readVectors("../resources/referenceFrontsCSV/ZDT4.csv", ","));

    // Rationale: the default problem is ZDT4, and WASFGA, configured with standard settings, should
    // return find a front with a hypervolume value higher than 0.64

    var hv = hypervolume.compute(SolutionListUtils.getMatrixWithObjectiveValues(population));

    assertTrue(hv > 0.64);
  }
}
