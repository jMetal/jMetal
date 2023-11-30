package org.uma.jmetal.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADDEBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.SMPSOBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.SMSEMOABuilder;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.component.catalogue.ea.replacement.impl.RankingAndDensityEstimatorReplacement;
import org.uma.jmetal.component.catalogue.ea.selection.impl.NaryTournamentSelection;
import org.uma.jmetal.component.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.impl.NaryTournamentGlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.SPSO2007VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.DefaultVelocityUpdate;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateHtmlPages;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.operator.crossover.impl.BLXAlphaCrossover;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.LinkedPolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT1;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT10;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT11;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT12;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT13;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT14;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT15;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT16;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT17;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT18;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT19;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT2;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT20;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT3;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT4;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT5;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT6;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT7;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT8;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT9;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.aggregationfunction.impl.PenaltyBoundaryIntersection;
import org.uma.jmetal.util.aggregationfunction.impl.Tschebyscheff;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.comparator.MultiComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.ranking.impl.FastNonDominatedSortRanking;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

public class ZCAT2DStudy {

  private static final int INDEPENDENT_RUNS = 15;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    int numberOfObjectives = 2 ;
    int numberOfVariables = 30 ;
    boolean complicatedParetoSet = false ;
    int level = 2 ;
    boolean bias = false ;
    boolean imbalance = false ;
    
    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZCAT1(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT1.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT2(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT2.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT3(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT3.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT4(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT4.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT5(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT5.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT6(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT6.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT7(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT7.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT8(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT8.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT9(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT9.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT10(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT10.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT11(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT11.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT12(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT12.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT13(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT13.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT14(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT14.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT15(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT15.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT16(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT16.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT17(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT17.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT18(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT18.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT19(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT19.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT20(numberOfObjectives, numberOfVariables, complicatedParetoSet, level, bias, imbalance)).setReferenceFront("ZCAT20.2D.csv"));

    int maximumNumberOfEvaluations = 100000 ;
    int populationSize = 100 ;

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList, populationSize, maximumNumberOfEvaluations);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZCAT2DLevel")
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setReferenceFrontDirectory("resources/referenceFrontsCSV")
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setOutputParetoFrontFileName("FUN")
                    .setOutputParetoSetFileName("VAR")
                    .setIndicatorList(Arrays.asList(
                            new Epsilon(),
                            new PISAHypervolume(),
                            new NormalizedHypervolume(),
                            new InvertedGenerationalDistancePlus()))
                    .setIndependentRuns(INDEPENDENT_RUNS)
                    .setNumberOfCores(8)
                    .build();

    new ExecuteAlgorithms<>(experiment).run();
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(5).setColumns(4).setDisplayNotch().run();
    new GenerateHtmlPages<>(experiment).run() ;
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
      List<ExperimentProblem<DoubleSolution>> problemList, int populationSize, int maximumNumberOfEvaluations) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();

    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (var experimentProblem : problemList) {
        nsgaII(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
        smsemoa(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
        moead(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
        moeadde(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
        smpso(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
        //ansgaII(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
        //amopso(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
      }
    }

    return algorithms;
  }

  private static void nsgaII(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    EvolutionaryAlgorithm<DoubleSolution> algorithm = new org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder<>(
        experimentProblem.getProblem(),
        populationSize,
        offspringPopulationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .build();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
  }

  private static void smsemoa(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {
    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    var algorithm = new SMSEMOABuilder<>(
        experimentProblem.getProblem(),
        populationSize,
        crossover,
        mutation)
        .setTermination(termination)
        .build();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
  }

  private static void moead(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;
    var crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(populationSize) ;
    boolean normalizeObjectives = true ;

    EvolutionaryAlgorithm<DoubleSolution> algorithm = new MOEADBuilder<>(
        experimentProblem.getProblem(),
        populationSize,
        crossover,
        mutation,
        weightVectorDirectory,
        sequenceGenerator, normalizeObjectives)
        .setTermination(termination)
        .setAggregationFunction(new PenaltyBoundaryIntersection(5.0, normalizeObjectives))
        .build();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
  }

  private static void moeadde(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {

    double cr = 1.0 ;
    double f = 0.5 ;

    double mutationProbability = 1.0 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    String weightVectorDirectory = "resources/weightVectorFiles/moead";
    SequenceGenerator<Integer> sequenceGenerator = new IntegerPermutationGenerator(populationSize) ;

    boolean normalizeObjectives = true ;
    EvolutionaryAlgorithm<DoubleSolution> algorithm = new MOEADDEBuilder(
        experimentProblem.getProblem(),
        populationSize,
        cr,
        f,
        mutation,
        weightVectorDirectory,
        sequenceGenerator, normalizeObjectives)
        .setTermination(termination)
        .setMaximumNumberOfReplacedSolutionsy(2)
        .setNeighborhoodSelectionProbability(0.9)
        .setNeighborhoodSize(20)
        .setAggregationFunction(new Tschebyscheff(normalizeObjectives))
        .build() ;

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
  }

  private static void smpso(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {
    int swarmSize = populationSize ;
    Termination termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    ParticleSwarmOptimizationAlgorithm algorithm = new SMPSOBuilder(
        (DoubleProblem) experimentProblem.getProblem(),
        swarmSize)
        .setTermination(termination)
        .build();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, experimentProblem, run));
  }

  private static void ansgaII(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {

    var name = "ANSGAII";

    var densityEstimator = new CrowdingDistanceDensityEstimator<DoubleSolution>();
    var ranking = new FastNonDominatedSortRanking<DoubleSolution>();

    var createInitialPopulation = new RandomSolutionsCreation<>(experimentProblem.getProblem(), populationSize);

    var replacement =
        new RankingAndDensityEstimatorReplacement<DoubleSolution>(
            ranking, densityEstimator, Replacement.RemovalPolicy.ONE_SHOT);

    double crossoverProbability = 0.86;
    double alphaValue = 0.55;
    var crossover = new BLXAlphaCrossover(crossoverProbability, alphaValue);

    double mutationProbability = 0.161 / experimentProblem.getProblem().numberOfVariables();
    double mutationDistributionIndex = 20.0;
    var mutation = new LinkedPolynomialMutation(mutationProbability, mutationDistributionIndex);

    var variation =
        new CrossoverAndMutationVariation<>(
            100, crossover, mutation);

    int tournamentSize = 4 ;
    var selection =
        new NaryTournamentSelection<>(
            tournamentSize,
            variation.getMatingPoolSize(),
            new MultiComparator<>(
                Arrays.asList(
                    Comparator.comparing(ranking::getRank),
                    Comparator.comparing(densityEstimator::value).reversed())));

    var termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    var evaluation = new SequentialEvaluation<>(experimentProblem.getProblem());

    var algorithm = new EvolutionaryAlgorithm<DoubleSolution>(name, createInitialPopulation, evaluation, termination,
        selection, variation, replacement);
    algorithms.add(
        new ExperimentAlgorithm<>(algorithm,"ANSGAII",experimentProblem, run));
  }

  private static void amopso(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem, int populationSize, int maximumNumberOfEvaluations) {

    var name = "AMOPSO";
    var problem = experimentProblem.getProblem() ;
    var swarmSize = 39 ;

    var swarmInitialization = new RandomSolutionsCreation<>(problem, swarmSize);
    var evaluation = new SequentialEvaluation<>(problem);
    var termination = new TerminationByEvaluations(maximumNumberOfEvaluations);
    var velocityInitialization = new SPSO2007VelocityInitialization();
    var localBestInitialization = new DefaultLocalBestInitialization();
    var globalBestInitialization = new DefaultGlobalBestInitialization();

    var archive = new HypervolumeArchive<DoubleSolution>(populationSize, new org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume<>());
    var globalBestSelection = new NaryTournamentGlobalBestSelection(9, archive.comparator()) ;

    double r1Min = 0.0;
    double r1Max = 1.0;
    double r2Min = 0.0;
    double r2Max = 1.0;
    double c1Min = 1.89;
    double c1Max = 2.25;
    double c2Min = 1.5;
    double c2Max = 2.9;
    double weight = 0.12;
    var inertiaWeightComputingStrategy = new ConstantValueStrategy(weight) ;

    var velocityUpdate = new DefaultVelocityUpdate(c1Min, c1Max, c2Min, c2Max);

    double velocityChangeWhenLowerLimitIsReached = 0.02;
    double velocityChangeWhenUpperLimitIsReached = -0.9;
    var positionUpdate = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached,
        velocityChangeWhenUpperLimitIsReached, ((DoubleProblem)problem).variableBounds());

    int frequencyOfMutation = 10;
    MutationOperator<DoubleSolution> mutationOperator = new NonUniformMutation(1.65 / problem.numberOfVariables(), 0.1, maximumNumberOfEvaluations/swarmSize) ;
    var perturbation = new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfMutation);
    var globalBestUpdate = new DefaultGlobalBestUpdate();
    var localBestUpdate = new DefaultLocalBestUpdate(new DefaultDominanceComparator<>());

    var algorithm = new ParticleSwarmOptimizationAlgorithm(name, swarmInitialization, evaluation, termination,
        velocityInitialization,
        localBestInitialization,
        globalBestInitialization,
        inertiaWeightComputingStrategy,
        velocityUpdate,
        positionUpdate,
        perturbation,
        globalBestUpdate,
        localBestUpdate,
        globalBestSelection,
        archive);

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm,"AMOPSO",experimentProblem, run));
  }
}
