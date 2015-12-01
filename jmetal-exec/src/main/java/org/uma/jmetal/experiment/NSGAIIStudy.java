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
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.multiobjective.zdt.*;
import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.experiment.ExperimentConfiguration;
import org.uma.jmetal.util.experiment.ExperimentConfigurationBuilder;
import org.uma.jmetal.util.experiment.component.ComputeQualityIndicators;
import org.uma.jmetal.util.experiment.component.ExecuteAlgorithms;
import org.uma.jmetal.util.experiment.component.GenerateLatexTablesWithStatistics;
import org.uma.jmetal.util.experiment.component.GenerateWilcoxonTestTables;
import org.uma.jmetal.util.experiment.util.TaggedAlgorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Example of experimental study based on solving the ZDT problems with four versions of NSGA-II, each
 * of them applying a different crossover probability (from 0.7 to 1.0).
 *
 * This experiment assumes that the reference Pareto front are known, so the names of files containing
 * them and the directory where they are located must be specified.
 *
 * Six quality indicators are used for performance assessment.
 *
 * The steps to carry out the experiment are:
 * 1. Configure the experiment
 * 2. Execute the algorithms
 * 3. Compute que quality indicators
 * 4. Generate Latex tables reporting means and medians
 * 5. Generate Latex tables with the result of applying the Wilcoxon Rank Sum Test
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NSGAIIStudy  {
  public static void main(String[] args) throws IOException {
    List<Problem<DoubleSolution>> problemList = Arrays.<Problem<DoubleSolution>>asList(new ZDT1(), new ZDT2(),
        new ZDT3(), new ZDT4(), new ZDT6()) ;

    List<TaggedAlgorithm<List<DoubleSolution>>> algorithmList = configureAlgorithmList(problemList) ;

    List<String> referenceFrontFileNames = Arrays.asList("ZDT1.pf", "ZDT2.pf", "ZDT3.pf", "ZDT4.pf", "ZDT6.pf") ;

    ExperimentConfiguration<DoubleSolution, List<DoubleSolution>> configuration =
        new ExperimentConfigurationBuilder<DoubleSolution, List<DoubleSolution>>("NSGAIIStudy")
            .setAlgorithmList(algorithmList)
            .setProblemList(problemList)
            .setExperimentBaseDirectory("experiment")
            .setOutputParetoFrontFileName("FUN")
            .setOutputParetoSetFileName("VAR")
            .setReferenceFrontDirectory("/pareto_fronts")
            .setReferenceFrontFileNames(referenceFrontFileNames)
            .setIndicatorList(Arrays.asList(
                new Epsilon<>(), new Spread<>(), new GenerationalDistance<>(), new Hypervolume<>(),
                new InvertedGenerationalDistance<>(), new InvertedGenerationalDistancePlus<>()))
            .setIndependentRuns(25)
            .setNumberOfCores(8)
            .build();

    //new ExecuteAlgorithms<>(configuration).run();
    //new ComputeQualityIndicators<>(configuration).run() ;
    new GenerateLatexTablesWithStatistics(configuration).run() ;
    new GenerateWilcoxonTestTables<>(configuration).run() ;
  }

  /**
   * The algorithm list is composed of pairs {@link Algorithm} + {@link Problem} which form part of a
   * {@link TaggedAlgorithm}, which is a decorator for class {@link Algorithm}. The {@link TaggedAlgorithm}
   * has an optional tag component, that can be set as it is shown in this example, where four variants of a
   * same algorithm are defined.
   *
   * @param problemList
   * @return
   */
  static List<TaggedAlgorithm<List<DoubleSolution>>> configureAlgorithmList(List<Problem<DoubleSolution>> problemList) {
    List<TaggedAlgorithm<List<DoubleSolution>>> algorithms = new ArrayList<>() ;
    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i), new SBXCrossover(1.0, 20.0),
          new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
          .setMaxEvaluations(25000)
          .setPopulationSize(100)
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "NSGAIIa", problemList.get(i)));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i), new SBXCrossover(0.9, 20.0),
          new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
          .setMaxEvaluations(25000)
          .setPopulationSize(100)
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "NSGAIIb", problemList.get(i)));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i), new SBXCrossover(0.8, 50.0),
          new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
          .setMaxEvaluations(25000)
          .setPopulationSize(100)
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "NSGAIIc", problemList.get(i)));
    }

    for (int i = 0; i < problemList.size(); i++) {
      Algorithm<List<DoubleSolution>> algorithm = new NSGAIIBuilder<>(problemList.get(i), new SBXCrossover(0.7, 50.0),
          new PolynomialMutation(1.0 / problemList.get(i).getNumberOfVariables(), 20.0))
          .setMaxEvaluations(25000)
          .setPopulationSize(100)
          .build();
      algorithms.add(new TaggedAlgorithm<List<DoubleSolution>>(algorithm, "NSGAIId", problemList.get(i)));
    }

    return algorithms ;
  }
}