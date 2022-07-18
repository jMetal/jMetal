package org.uma.jmetal.algorithm.multiobjective.artificialdecisionmaker;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Ignore;
import org.junit.Test;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.InteractiveAlgorithm;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGA;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.artificialdecisionmaker.impl.ArtificialDecisionMakerDecisionTree;
import org.uma.jmetal.util.artificialdecisionmaker.impl.ArtificiallDecisionMakerBuilder;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.point.impl.IdealPoint;
import org.uma.jmetal.util.point.impl.NadirPoint;

public class ArtificiallDecisionMakerIT {
  Algorithm<List<DoubleSolution>> algorithm;

  @Ignore
  @Test
  public void shouldTheAlgorithmReturnANumberOfSolutionsWhenSolvingASimpleProblem() throws Exception {
      InteractiveAlgorithm<DoubleSolution, List<DoubleSolution>> algorithmRun;
    var numberIterations = 1;
    var numberObjectives = 3;
    var numberVariables = 7;
    var weightsName = "";
    var populationSize = 100;

      Problem<DoubleSolution> problem = new DTLZ1(numberVariables, numberObjectives);

    var crossoverProbability = 0.9;
    var crossoverDistributionIndex = 20.0;
      CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables();
    var mutationDistributionIndex = 20.0;
      MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

      SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
              new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    var idealPoint = new IdealPoint(problem.getNumberOfObjectives());
    idealPoint.update(problem.createSolution().objectives());
    var nadirPoint = new NadirPoint(problem.getNumberOfObjectives());
    nadirPoint.update(problem.createSolution().objectives());
    var considerationProbability = 0.1;
    List<Double> rankingCoeficient = new ArrayList<>();
    var bound = problem.getNumberOfObjectives();
    for (var i1 = 0; i1 < bound; i1++) {
      Double aDouble = 1.0 / problem.getNumberOfObjectives();
      rankingCoeficient.add(aDouble);
    }

    for (var cont = 0; cont < numberIterations; cont++) {
      List<Double> referencePoint = new ArrayList<>();

      var epsilon = 0.01;
      List<Double> asp = new ArrayList<>();
      for (var i = 0; i < problem.getNumberOfObjectives(); i++) {
        asp.add(0.0);//initialize asp to ideal
        referencePoint.add(0.0);//initialization
      }

      algorithmRun = new WASFGA<>(problem, populationSize, 200, crossover, mutation,
          selection, new SequentialSolutionListEvaluator<>(), epsilon, referencePoint, weightsName);

      algorithm = new ArtificiallDecisionMakerBuilder<>(problem, algorithmRun)
          .setConsiderationProbability(considerationProbability)
          .setMaxEvaluations(11)
          .setTolerance(0.001)
          .setAsp(asp)
          .build();
      algorithm.run();

      var referencePoints = ((ArtificialDecisionMakerDecisionTree<DoubleSolution>) algorithm)
          .getReferencePoints();

      assertTrue(referencePoints.size() >= numberObjectives * numberIterations);
    }
  }

}
