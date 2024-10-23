package org.uma.jmetal.auto.autoconfigurablealgorithm.studies;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.lab.experiment.Experiment;
import org.uma.jmetal.lab.experiment.ExperimentBuilder;
import org.uma.jmetal.lab.experiment.component.impl.ComputeQualityIndicators;
import org.uma.jmetal.lab.experiment.component.impl.ExecuteAlgorithms;
import org.uma.jmetal.lab.experiment.component.impl.GenerateBoxplotsWithR;
import org.uma.jmetal.lab.experiment.component.impl.GenerateFriedmanHolmTestTables;
import org.uma.jmetal.lab.experiment.component.impl.GenerateHtmlPages;
import org.uma.jmetal.lab.experiment.component.impl.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.lab.experiment.component.impl.GenerateWilcoxonTestTablesWithR;
import org.uma.jmetal.lab.experiment.studies.DTLZStudy;
import org.uma.jmetal.lab.experiment.util.ExperimentAlgorithm;
import org.uma.jmetal.lab.experiment.util.ExperimentProblem;
import org.uma.jmetal.lab.visualization.StudyVisualizer;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT10_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT11_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT12_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT13_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT14_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT15_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT16_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT17_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT18_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT19_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT1_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT20_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT2_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT3_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT4_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT5_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT6_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT7_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT8_2D;
import org.uma.jmetal.problem.multiobjective.zcat.ZCAT9_2D;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.GenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistance;
import org.uma.jmetal.qualityindicator.impl.InvertedGenerationalDistancePlus;
import org.uma.jmetal.qualityindicator.impl.NormalizedHypervolume;
import org.uma.jmetal.qualityindicator.impl.Spread;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Example of experimental study based on solving the ZDT problems with the algorithms NSGAII,
 * MOEA/D, and SMPSO.
 *
 * This experiment assumes that the reference Pareto front are known and that, given a problem named
 * P, there is a corresponding file called P.csv containing its corresponding Pareto front. If this
 * is not the case, please refer to class {@link DTLZStudy} to see an example of how to explicitly
 * indicate the name of those files.
 *
 * Five quality indicators are used for performance assessment: {@link Epsilon}, {@link Spread},
 * {@link GenerationalDistance}, {@link PISAHypervolume}, and {@link InvertedGenerationalDistancePlus}.
 *
 * The steps to carry out are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Compute que quality indicators
 * 4. Generate Latex tables reporting means and medians, and tables with statistical tests
 * 5. Generate HTML pages with tables, boxplots, and fronts.
 *
 * @author Antonio J. Nebro
 */

public class ZCATStudy {
  private static final int INDEPENDENT_RUNS = 25;

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      throw new JMetalException("Missing argument: experimentBaseDirectory");
    }
    String experimentBaseDirectory = args[0];

    List<ExperimentProblem<DoubleSolution>> problemList = List.of(
        new ExperimentProblem<>(new ZCAT1_2D()).setReferenceFront("ZCAT1.2D.csv"),
        new ExperimentProblem<>(new ZCAT2_2D()).setReferenceFront("ZCAT2.2D.csv"),
        new ExperimentProblem<>(new ZCAT3_2D()).setReferenceFront("ZCAT3.2D.csv"),
        new ExperimentProblem<>(new ZCAT4_2D()).setReferenceFront("ZCAT4.2D.csv"),
        new ExperimentProblem<>(new ZCAT5_2D()).setReferenceFront("ZCAT5.2D.csv"),
        new ExperimentProblem<>(new ZCAT6_2D()).setReferenceFront("ZCAT6.2D.csv"),
        new ExperimentProblem<>(new ZCAT7_2D()).setReferenceFront("ZCAT7.2D.csv"),
        new ExperimentProblem<>(new ZCAT8_2D()).setReferenceFront("ZCAT8.2D.csv"),
        new ExperimentProblem<>(new ZCAT9_2D()).setReferenceFront("ZCAT9.2D.csv"),
        new ExperimentProblem<>(new ZCAT10_2D()).setReferenceFront("ZCAT10.2D.csv"),
        new ExperimentProblem<>(new ZCAT11_2D()).setReferenceFront("ZCAT11.2D.csv"),
        new ExperimentProblem<>(new ZCAT12_2D()).setReferenceFront("ZCAT12.2D.csv"),
        new ExperimentProblem<>(new ZCAT13_2D()).setReferenceFront("ZCAT13.2D.csv"),
        new ExperimentProblem<>(new ZCAT14_2D()).setReferenceFront("ZCAT14.2D.csv"),
        new ExperimentProblem<>(new ZCAT15_2D()).setReferenceFront("ZCAT15.2D.csv"),
        new ExperimentProblem<>(new ZCAT16_2D()).setReferenceFront("ZCAT16.2D.csv"),
        new ExperimentProblem<>(new ZCAT17_2D()).setReferenceFront("ZCAT17.2D.csv"),
        new ExperimentProblem<>(new ZCAT18_2D()).setReferenceFront("ZCAT18.2D.csv"),
        new ExperimentProblem<>(new ZCAT19_2D()).setReferenceFront("ZCAT19.2D.csv"),
        new ExperimentProblem<>(new ZCAT20_2D()).setReferenceFront("ZCAT20.2D.csv")
    );

    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithmList =
            configureAlgorithmList(problemList);

    Experiment<DoubleSolution, List<DoubleSolution>> experiment =
            new ExperimentBuilder<DoubleSolution, List<DoubleSolution>>("ZCATStudyParetoSet")
                    .setAlgorithmList(algorithmList)
                    .setProblemList(problemList)
                    .setReferenceFrontDirectory("resources/referenceFrontsCSV")
                    .setExperimentBaseDirectory(experimentBaseDirectory)
                    .setOutputParetoFrontFileName("FUN")
                    .setOutputParetoSetFileName("VAR")
                    .setIndicatorList(List.of(
                            new Epsilon(),
                            new Spread(),
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
    new GenerateFriedmanHolmTestTables<>(experiment).run();
    new GenerateWilcoxonTestTablesWithR<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(5).setColumns(4).run();
    //new GenerateHtmlPages<>(experiment, StudyVisualizer.TYPE_OF_FRONT_TO_SHOW.MEDIAN).run() ;
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of
   * a {@link ExperimentAlgorithm}, which is a decorator for class {@link Algorithm}.
   */
  static List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> configureAlgorithmList(
          List<ExperimentProblem<DoubleSolution>> problemList) {
    List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms = new ArrayList<>();
    for (int run = 0; run < INDEPENDENT_RUNS; run++) {
      for (var experimentProblem : problemList) {
        nsgaii(algorithms, run, experimentProblem);
        nsgaiiirace1(algorithms, run, experimentProblem);
        nsgaiiirace2(algorithms, run, experimentProblem);
      }
    }
    return algorithms;
  }


  private static void nsgaii(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    String[] parameters =
        ("--problemName " + "org.uma.jmetal.problem.multiobjective.zcat." + experimentProblem.getProblem().name() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--populationSize 100 "
            + "--algorithmResult population  "
            + "--createInitialSolutions random "
            + "--offspringPopulationSize 100 "
            + "--variation crossoverAndMutationVariation --crossover SBX "
            + "--crossoverProbability 0.9 "
            + "--crossoverRepairStrategy bounds "
            + "--sbxDistributionIndex 20.0 "
            + "--mutation polynomial "
            + "--mutationProbabilityFactor 1.0 "
            + "--polynomialMutationDistributionIndex 20.0 "
            + "--mutationRepairStrategy bounds "
            + "--selection tournament "
            + "--selectionTournamentSize 2 \n")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    algorithms.add(new ExperimentAlgorithm<>(nsgaII, "NSGAII", experimentProblem, run));
  }

  private static void nsgaiiirace2(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    String[] parameters =
        ("--problemName " + "org.uma.jmetal.problem.multiobjective.zcat." + experimentProblem.getProblem().name() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--populationSize 100 "
            + "--algorithmResult externalArchive "
            + "--populationSizeWithArchive 100 "
            + "--externalArchive crowdingDistanceArchive "
            + "--createInitialSolutions scatterSearch "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 100 "
            + "--crossover BLX_ALPHA "
            + "--crossoverProbability 0.9916 "
            + "--crossoverRepairStrategy bounds "
            + "--blxAlphaCrossoverAlphaValue 0.4046 "
            + "--mutation linkedPolynomial "
            + "--mutationProbabilityFactor 1.2227 "
            + "--mutationRepairStrategy round "
            + "--linkedPolynomialMutationDistributionIndex 288.2862 "
            + "--selection tournament --selectionTournamentSize 2")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    algorithms.add(new ExperimentAlgorithm<>(nsgaII, "Irace2", experimentProblem, run));
  }

  private static void nsgaiiirace1(
      List<ExperimentAlgorithm<DoubleSolution, List<DoubleSolution>>> algorithms, int run,
      ExperimentProblem<DoubleSolution> experimentProblem) {

    String[] parameters =
        ("--problemName " + "org.uma.jmetal.problem.multiobjective.zcat." + experimentProblem.getProblem().name() + " "
            + "--randomGeneratorSeed 12 "
            + "--referenceFrontFileName " + experimentProblem.getReferenceFront() + " "
            + "--maximumNumberOfEvaluations 10000 "
            + "--populationSize 100 "
            + "--algorithmResult externalArchive "
            + "--populationSizeWithArchive 100 "
            + "--externalArchive crowdingDistanceArchive "
            + "--createInitialSolutions scatterSearch "
            + "--variation crossoverAndMutationVariation "
            + "--offspringPopulationSize 100 "
            + "--crossover BLX_ALPHA "
            + "--crossoverProbability 0.9997 "
            + "--crossoverRepairStrategy bounds "
            + "--blxAlphaCrossoverAlphaValue 0.399 "
            + "--mutation uniform "
            + "--mutationProbabilityFactor 1.1618 "
            + "--mutationRepairStrategy bounds "
            + "--uniformMutationPerturbation 0.0026 "
            + "--selection random")
            .split("\\s+");

    AutoNSGAII autoNSGAII = new AutoNSGAII();
    autoNSGAII.parse(parameters);

    EvolutionaryAlgorithm<DoubleSolution> nsgaII = autoNSGAII.create();

    algorithms.add(new ExperimentAlgorithm<>(nsgaII, "Irace1", experimentProblem, run));
  }

}
