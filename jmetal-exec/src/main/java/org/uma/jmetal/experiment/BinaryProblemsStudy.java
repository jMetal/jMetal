//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.experiment;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.mocell.MOCellBuilder;
import org.uma.jmetal.algorithm.multiobjective.mochc.MOCHCBuilder;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.HUXCrossover;
import org.uma.jmetal.operator.impl.crossover.SinglePointCrossover;
import org.uma.jmetal.operator.impl.mutation.BitFlipMutation;
import org.uma.jmetal.operator.impl.selection.RandomSelection;
import org.uma.jmetal.operator.impl.selection.RankingAndCrowdingSelection;
import org.uma.jmetal.problem.BinaryProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.OneZeroMax;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT5;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.BinarySolution;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.experiment.Experiment;
import org.uma.jmetal.util.experiment.ExperimentBuilder;
import org.uma.jmetal.util.experiment.component.*;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with five algorithms: NSGAII, SPEA2, MOCell,
 * SMPSO and AbYSS
 *
 * This experiment assumes that the reference Pareto front are not known, so the names of files containing
 * them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Generate the reference Pareto fronts
 * 4. Compute que quality indicators
 * 5. Generate Latex tables reporting means and medians
 * 6. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 * 7. Generate Latex tables with the ranking obtained by applying the Friedman test
 * 8. Generate R scripts to obtain boxplots
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class BinaryProblemsStudy {
  private static final int INDEPENDENT_RUNS = 25 ;

  public static void main(String[] args) throws IOException {
    if (args.length != 2) {
      throw new JMetalException("Needed arguments: experimentBaseDirectory referenceFrontDirectory") ;
    }
    String experimentBaseDirectory = args[0] ;
    String referenceFrontDirectory = args[1] ;

    List<Problem<BinarySolution>> problemList = Arrays.<Problem<BinarySolution>>asList(
        new ZDT5(), new OneZeroMax(512)) ;

    List<TaggedAlgorithm<List<BinarySolution>>> algorithmList = configureAlgorithmList(problemList, INDEPENDENT_RUNS) ;

    Experiment<BinarySolution, List<BinarySolution>> experiment =
        new ExperimentBuilder<BinarySolution, List<BinarySolution>>("BinaryProblemsStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory(experimentBaseDirectory)
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory(referenceFrontDirectory)
            .setIndicatorList(Arrays.asList(
                new Epsilon<BinarySolution>(), new Spread<BinarySolution>(), new GenerationalDistance<BinarySolution>(),
                new PISAHypervolume<BinarySolution>(),
                new InvertedGenerationalDistance<BinarySolution>(),
                new InvertedGenerationalDistancePlus<BinarySolution>())
            )
            .setIndependentRuns(INDEPENDENT_RUNS)
            .setNumberOfCores(8)
            .build();

    new ExecuteAlgorithms<>(experiment).run();
    new GenerateReferenceParetoFront(experiment).run();
    new ComputeQualityIndicators<>(experiment).run() ;
    new GenerateLatexTablesWithStatistics(experiment).run() ;
    new GenerateWilcoxonTestTablesWithR<>(experiment).run() ;
    new GenerateFriedmanTestTables<>(experiment).run();
    new GenerateBoxplotsWithR<>(experiment).setRows(1).setColumns(2).setDisplayNotch().run() ;
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}.
   *
   * @param problemList
   * @return
   */
  static List<TaggedAlgorithm<List<BinarySolution>>> configureAlgorithmList(
      List<Problem<BinarySolution>> problemList,
      int independentRuns) {
    List<TaggedAlgorithm<List<BinarySolution>>> algorithms = new ArrayList<>() ;

    for (int run = 0; run < independentRuns; run++) {
      for (Problem<BinarySolution> problem : problemList) {
        Algorithm<List<BinarySolution>> algorithm = new NSGAIIBuilder<BinarySolution>(problem, new SinglePointCrossover(1.0),
            new BitFlipMutation(1.0 / ((BinaryProblem) problem).getNumberOfBits(0)))
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build();
        algorithms.add(new TaggedAlgorithm<List<BinarySolution>>(algorithm, problem, run));
      }

      for (Problem<BinarySolution> problem : problemList) {
        Algorithm<List<BinarySolution>> algorithm = new SPEA2Builder<BinarySolution>(problem, new SinglePointCrossover(1.0),
            new BitFlipMutation(1.0 / ((BinaryProblem) problem).getNumberOfBits(0)))
            .setMaxIterations(250)
            .setPopulationSize(100)
            .build();
        algorithms.add(new TaggedAlgorithm<List<BinarySolution>>(algorithm, problem, run));
      }

      for (Problem<BinarySolution> problem : problemList) {
        Algorithm<List<BinarySolution>> algorithm = new MOCellBuilder<BinarySolution>(problem, new SinglePointCrossover(1.0),
            new BitFlipMutation(1.0 / ((BinaryProblem) problem).getNumberOfBits(0)))
            .setPopulationSize(100)
            .setMaxEvaluations(25000)
            .setArchive(new CrowdingDistanceArchive<BinarySolution>(100))
            .build();
        algorithms.add(new TaggedAlgorithm<List<BinarySolution>>(algorithm, problem, run));
      }

      for (Problem<BinarySolution> problem : problemList) {
        CrossoverOperator<BinarySolution> crossoverOperator;
        MutationOperator<BinarySolution> mutationOperator;
        SelectionOperator<List<BinarySolution>, BinarySolution> parentsSelection;
        SelectionOperator<List<BinarySolution>, List<BinarySolution>> newGenerationSelection;

        crossoverOperator = new HUXCrossover(1.0);
        parentsSelection = new RandomSelection<BinarySolution>();
        newGenerationSelection = new RankingAndCrowdingSelection<BinarySolution>(100);
        mutationOperator = new BitFlipMutation(0.35);

        Algorithm<List<BinarySolution>> algorithm = new MOCHCBuilder(((BinaryProblem) problem))
            .setInitialConvergenceCount(0.25)
            .setConvergenceValue(3)
            .setPreservedPopulation(0.05)
            .setPopulationSize(100)
            .setMaxEvaluations(25000)
            .setCrossover(crossoverOperator)
            .setNewGenerationSelection(newGenerationSelection)
            .setCataclysmicMutation(mutationOperator)
            .setParentSelection(parentsSelection)
            .setEvaluator(new SequentialSolutionListEvaluator<BinarySolution>())
            .build();

        algorithms.add(new TaggedAlgorithm<List<BinarySolution>>(algorithm, problem, run));
      }
    }
    return algorithms ;
  }
}
