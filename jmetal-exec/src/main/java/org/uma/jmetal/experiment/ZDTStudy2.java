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
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIBuilder;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOBuilder;
import org.uma.jmetal.algorithm.multiobjective.spea2.SPEA2Builder;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.ExperimentConfigurationBuilder;
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
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class ZDTStudy2 {
  public static void main(String[] args) throws IOException {
    List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(new ZDT1(), new ZDT2(),
        new ZDT3(), new ZDT4(), new ZDT6()) ;

    List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList) ;

    ExperimentConfiguration<DoubleSolution, List<DoubleSolution>> configuration =
        new ExperimentConfigurationBuilder<DoubleSolution, List<DoubleSolution>>("ZDT2Study")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory("experiment")
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setIndicatorList(Arrays.asList(
                new Epsilon<DoubleSolution>(), new Spread<DoubleSolution>(), new GenerationalDistance<DoubleSolution>(),
                new PISAHypervolume<DoubleSolution>(),
                new InvertedGenerationalDistance<DoubleSolution>(), new InvertedGenerationalDistancePlus<DoubleSolution>()))
            .setIndependentRuns(2)
            .setNumberOfCores(8)
            .build();

    new ExecuteAlgorithms<>(configuration).run();
    new GenerateReferenceParetoFront(configuration).run();
    new ComputeQualityIndicators<>(configuration).run() ;
    new GenerateLatexTablesWithStatistics(configuration).run() ;
    new GenerateWilcoxonTestTables<>(configuration).run() ;
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}.
   *
   * @param problemList
   * @return
   */
  static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(List<Problem<DoubleSolution>> problemList) {
    List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>() ;

    for (Problem<DoubleSolution> problem : problemList) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<DoubleSolution>(problem, new SBXCrossover(1.0, 20.0),
          new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problem));
    }

    for (Problem<DoubleSolution> problem : problemList) {
      Algorithm<List<DoubleSolution>> algorithm = new SPEA2Builder<DoubleSolution>(problem, new SBXCrossover(1.0, 10.0),
          new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0))
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problem));
    }

    for (Problem<DoubleSolution> problem : problemList) {
      Algorithm<List<DoubleSolution>> algorithm = new SMPSOBuilder((DoubleProblem) problem,
          new CrowdingDistanceArchive<DoubleSolution>(100))
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, problem));
    }

    return algorithms ;
  }
}
