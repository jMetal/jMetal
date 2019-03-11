package org.uma.jmetal.auto.algorithm.impl;

import org.uma.jmetal.auto.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.auto.createinitialsolutions.CreateInitialSolutions;
import org.uma.jmetal.auto.createinitialsolutions.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.evaluation.Evaluation;
import org.uma.jmetal.auto.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.auto.selection.MatingPoolSelection;
import org.uma.jmetal.auto.termination.Termination;
import org.uma.jmetal.auto.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.auto.util.densityestimator.DensityEstimator;
import org.uma.jmetal.auto.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.auto.util.ranking.Ranking;
import org.uma.jmetal.auto.util.ranking.impl.DominanceRanking;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.selection.NaryTournamentSelection;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import picocli.CommandLine.Option;

import java.util.Arrays;
import java.util.List;

enum CreateInitialSolutionsStrategyList {random}

enum MutationList {polynomial, uniform}

enum SelectionList {random, tournament}

public class AutoNSGAII {
  int populationSize = 100;
  DoubleProblem problem = new ZDT1();

  Termination termination = new TerminationByEvaluations(250000);

  Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem);

  Ranking<DoubleSolution> ranking = new DominanceRanking<>(new DominanceComparator<>()) ;
  DensityEstimator<DoubleSolution> densityEstimator = new CrowdingDistanceDensityEstimator<>() ;

  MultiComparator<DoubleSolution> rankingAndCrowdingComparator = new MultiComparator<>(
      Arrays.asList(
          ranking.getSolutionComparator(),
          densityEstimator.getSolutionComparator()));

  @Option(names = {"--offspringPopulationSize"},
      description = "offspring population size (default: ${DEFAULT-VALUE})")
  private int offspringPopulationSize = populationSize;

  @Option(names = {"--createInitialSolutions"}, required = true, description = "Crossover: ${COMPLETION-CANDIDATES}")
  private CreateInitialSolutionsStrategyList createInitialSolutions = CreateInitialSolutionsStrategyList.random;

  @Option(names = {"--selection"}, required = true, description = "Crossover: ${COMPLETION-CANDIDATES}")
  private SelectionList selection;

  @Option(names = {"--selectionTournamentSize"}, description = "Selection: number of selected solutions")
  private int selectionTournamentSize = 2;


  EvolutionaryAlgorithm<DoubleSolution> configureAndGetAlgorithm(DoubleProblem problem, int populationSize, int maxEvaluations) {
    EvolutionaryAlgorithm<DoubleSolution> nsgaii = new EvolutionaryAlgorithm<>(
        "NSGAII",
        evaluation,
        getCreateInitialSolutionStrategy(),
        termination,
        getSelection(),
        null,
        null);//replacement);

    return nsgaii;
  }

  MatingPoolSelection<DoubleSolution> getSelection() {
    switch (selection) {
      case random:
        return new RandomSelection<>();
      case tournament:
        return new NaryTournamentSelection<>(selectionTournamentSize, new RankingAndCrowdingDistanceComparator<>());
      default:
        throw new RuntimeException(selection + " is not a valid selection operator");
    }
  }

  CreateInitialSolutions<DoubleSolution> getCreateInitialSolutionStrategy() {
    switch (createInitialSolutions) {
      case random:
        return new RandomSolutionsCreation(problem, populationSize);
      default:
        throw new RuntimeException(createInitialSolutions + " is not a valid initialization strategy");
    }
  }
}
