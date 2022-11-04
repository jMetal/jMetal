package org.uma.jmetal.auto.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOEAD;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
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
import org.uma.jmetal.lab.visualization.StudyVisualizer;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ3;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ4;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ5;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ6;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ7;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.Check;

public class MOEAD3DStudy {
  private static final int INDEPENDENT_RUNS = 15;

  public static void main(String[] args) throws IOException {
    Check.that(args.length == 1, "Missing argument: experimentBaseDirectory");

    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = new ArrayList<>();


    problemList.add(new ExperimentProblem<>(new DTLZ1()).setReferenceFront("DTLZ1.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ2()).setReferenceFront("DTLZ2.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ3()).setReferenceFront("DTLZ3.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ4()).setReferenceFront("DTLZ4.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ5()).setReferenceFront("DTLZ5.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ6()).setReferenceFront("DTLZ6.3D.csv"));
    problemList.add(new ExperimentProblem<>(new DTLZ7()).setReferenceFront("DTLZ7.3D.csv"));
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
        configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
        new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("moeadDTLZ3DExperiment")
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
                new InvertedGenerationalDistance(),
                new InvertedGenerationalDistancePlus()))
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    new ExecuteAlgorithms<>(experiment).run();

    new ComputeQualityIndicators<>(experiment).run();
    new GenerateLatexTablesWithStatistics(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(3).setColumns(3).setDisplayNotch().run();
    new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run();
  }
  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
      List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (ExperimentProblem<DoubleSolution> experimentProblem : problemList) {
        moead(algorithms, run, experimentProblem);
        moeadDE(algorithms, run, experimentProblem);
        autoMOEAD(algorithms, run, experimentProblem);
      }
    }
    return algorithms;
  }

  private static void moead(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName "+  experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 35000 "
            + "--algorithmResult population "
            + "--populationSize 91 "
            + "--offspringPopulationSize 1 "
            + "--createInitialSolutions random "
            + "--neighborhoodSize 20 "
            + "--maximumNumberOfReplacedSolutions 2 "
            + "--aggregationFunction penaltyBoundaryIntersection "
            + "--pbiTheta 5.0 "
            + "--neighborhoodSelectionProbability 0.9 "
            + "--variation crossoverAndMutationVariation "
            + "--selection populationAndNeighborhoodMatingPoolSelection "
            + "--crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    var autoAlgorithm = new AutoMOEAD();
    autoAlgorithm.parseAndCheckParameters(parameters);

    EvolutionaryAlgorithm<DoubleSolution> algorithm = autoAlgorithm.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "MOEAD", experimentProblem, run));
  }

  private static void moeadDE(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 35000 "
            + "--algorithmResult population "
            + "--populationSize 91 "
            + "--offspringPopulationSize 1 "
            + "--createInitialSolutions random "
            + "--neighborhoodSize 20 "
            + "--maximumNumberOfReplacedSolutions 2 "
            + "--aggregationFunction tschebyscheff "
            + "--neighborhoodSelectionProbability 0.9 "
            + "--variation differentialEvolutionVariation "
            + "--differentialEvolutionCrossover RAND_1_BIN "
            + "--CR 1.0 "
            + "--F 0.5 "
            + "--selection populationAndNeighborhoodMatingPoolSelection "
            + "--crossover differentialEvolutionCrossover "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--mutationRepairStrategy bounds "
            + "--polynomialMutationDistributionIndex 20.0 ")
            .split("\\s+");

    var autoAlgorithm = new AutoMOEAD();
    autoAlgorithm.parseAndCheckParameters(parameters);

    EvolutionaryAlgorithm<DoubleSolution> algorithm = autoAlgorithm.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "MOEADDE", experimentProblem, run));
  }

  private static void autoMOEAD(List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run, ExperimentProblem<DoubleSolution> experimentProblem) {
    String[] parameters =
        ("--problemName " + experimentProblem.getProblem().getClass().getName() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName "+ experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 35000 "
            + "--algorithmResult externalArchive "
            + "--externalArchive unboundedArchive "
            + "--populationSize 91 "
            + "--offspringPopulationSize 1 "
            + "--createInitialSolutions random "
            + "--neighborhoodSize 30 "
            + "--maximumNumberOfReplacedSolutions 5 "
            + "--aggregationFunction tschebyscheff "
            + "--variation differentialEvolutionVariation "
            + "--differentialEvolutionCrossover RAND_2_BIN "
            + "--CR 0.1199 "
            + "--F 0.6261 "
            + "--neighborhoodSelectionProbability 0.0016 "
            + "--selection populationAndNeighborhoodMatingPoolSelection "
            + "--nonUniformMutationPerturbation 0.8439 "
            + "--mutation nonUniform "
            + "--mutationProbabilityFactor 0.7089 "
            + "--mutationRepairStrategy round ")
            .split("\\s+");

    var autoAlgorithm = new AutoMOEAD();
    autoAlgorithm.parseAndCheckParameters(parameters);

    EvolutionaryAlgorithm<DoubleSolution> algorithm = autoAlgorithm.create();

    algorithms.add(
        new ExperimentAlgorithm<>(algorithm, "AutoMOEAD", experimentProblem, run));
  }
}
