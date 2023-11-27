package org.uma.jmetal.component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.MOEADBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.SMPSOBuilder;
import org.uma.jmetal.component.algorithm.multiobjective.SMSEMOABuilder;
import org.uma.jmetal.component.catalogue.common.termination.Termination;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
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
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.wfg.WFG1;
import org.uma.jmetal.problem.multiobjective.wfg.WFG2;
import org.uma.jmetal.problem.multiobjective.wfg.WFG3;
import org.uma.jmetal.problem.multiobjective.wfg.WFG4;
import org.uma.jmetal.problem.multiobjective.wfg.WFG5;
import org.uma.jmetal.problem.multiobjective.wfg.WFG6;
import org.uma.jmetal.problem.multiobjective.wfg.WFG7;
import org.uma.jmetal.problem.multiobjective.wfg.WFG8;
import org.uma.jmetal.problem.multiobjective.wfg.WFG9;
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
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.sequencegenerator.SequenceGenerator;
import org.uma.jmetal.util.sequencegenerator.impl.IntegerPermutationGenerator;

public class ZCAT2DStudy {

  private static final int INDEPENDENT_RUNS = 5;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();
    problemList.add(new ExperimentProblem<>(new ZCAT1(2, 30)).setReferenceFront("ZCAT1.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT2(2, 30)).setReferenceFront("ZCAT2.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT3(2, 30)).setReferenceFront("ZCAT3.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT4(2, 30)).setReferenceFront("ZCAT4.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT5(2, 30)).setReferenceFront("ZCAT5.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT6(2, 30)).setReferenceFront("ZCAT6.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT7(2, 30)).setReferenceFront("ZCAT7.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT8(2, 30)).setReferenceFront("ZCAT8.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT9(2, 30)).setReferenceFront("ZCAT9.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT10(2, 30)).setReferenceFront("ZCAT10.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT11(2, 30)).setReferenceFront("ZCAT11.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT12(2, 30)).setReferenceFront("ZCAT12.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT13(2, 30)).setReferenceFront("ZCAT13.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT14(2, 30)).setReferenceFront("ZCAT14.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT15(2, 30)).setReferenceFront("ZCAT15.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT16(2, 30)).setReferenceFront("ZCAT16.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT17(2, 30)).setReferenceFront("ZCAT17.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT18(2, 30)).setReferenceFront("ZCAT18.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT19(2, 30)).setReferenceFront("ZCAT19.2D.csv"));
    problemList.add(new ExperimentProblem<>(new ZCAT20(2, 30)).setReferenceFront("ZCAT20.2D.csv"));

    int maximumNumberOfEvaluations = 1000 ;
    int populationSize = 100 ;

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList, populationSize, maximumNumberOfEvaluations);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZCAT2D")
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
    /*
    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(5).setColumns(4).setDisplayNotch().run();
    new GenerateHtmlPages<>(experiment).run() ;

     */
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
        smpso(algorithms, run, experimentProblem, populationSize, maximumNumberOfEvaluations);
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
    boolean normalizeObjectives = false ;

    EvolutionaryAlgorithm<DoubleSolution> algorithm = new MOEADBuilder<>(
        experimentProblem.getProblem(),
        populationSize,
        crossover,
        mutation,
        weightVectorDirectory,
        sequenceGenerator, normalizeObjectives)
        .setTermination(termination)
        .build();

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
}
