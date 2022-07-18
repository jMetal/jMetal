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

package org.uma.jmetal.algorithm.examples.multiobjective.nsgaii;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.knowm.xchart.BitmapEncoder;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.examples.AlgorithmRunner;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAII;
import org.uma.jmetal.algorithm.multiobjective.rnsgaii.RNSGAIIBuilder;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.selection.SelectionOperator;
import org.uma.jmetal.operator.selection.impl.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemFactory;
import org.uma.jmetal.util.chartcontainer.ChartContainerWithReferencePoints;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.MeasureManager;
import org.uma.jmetal.util.measure.impl.BasicMeasure;
import org.uma.jmetal.util.measure.impl.CountingMeasure;

/**
 * Class to configure and run the R-NSGA-II algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Cristobal Barba <cbarba@lcc.uma.es>
 */
public class RNSGAIIWithChartsRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
   * @throws FileNotFoundException
   * Invoking command:
    java org.uma.jmetal.runner.multiobjective.nsgaii.RNSGAIIRunner problemName [referenceFront]
   */
  public static void main(String[] args) throws JMetalException, IOException {
    var referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv" ;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "resources/referenceFrontsCSV/ZDT1.csv" ;
    }

    Problem<DoubleSolution> problem = (DoubleProblem) ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossoverProbability = 0.9 ;
    var crossoverDistributionIndex = 20.0 ;
    CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex);

    var mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    var mutationDistributionIndex = 20.0 ;
    MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex);

    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection = new BinaryTournamentSelection<DoubleSolution>(
            new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    List<Double> referencePoint = new ArrayList<>() ;

    // Example fig 3 of Farina and Deb's paper
    referencePoint.add(0.1) ;
    referencePoint.add(0.6) ;

    referencePoint.add(0.3) ;
    referencePoint.add(0.6) ;

    referencePoint.add(0.5) ;
    referencePoint.add(0.2) ;

    referencePoint.add(0.7) ;
    referencePoint.add(0.2) ;

    referencePoint.add(0.9) ;
    referencePoint.add(0.0) ;

    var epsilon= 0.0045;

    Algorithm<List<DoubleSolution>> algorithm = new RNSGAIIBuilder<DoubleSolution>(problem, crossover, mutation, referencePoint, epsilon)
            .setSelectionOperator(selection)
            .setMaxEvaluations(25000)
            .setPopulationSize(100)
            .build();

    /* Measure management */
    var measureManager = ((RNSGAII<DoubleSolution>) algorithm).getMeasureManager();

    var solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");
    var iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

    @NotNull ChartContainerWithReferencePoints chart = new ChartContainerWithReferencePoints(algorithm.getName(), 80);
    chart.setFrontChart(0, 1, referenceParetoFront);
    chart.setReferencePoint(convertReferencePointListToListOfLists(referencePoint, problem.getNumberOfObjectives()));
    chart.initChart();

    solutionListMeasure.register(new ChartListener(chart));
    iterationMeasure.register(new IterationListener(chart));
    /* End of measure management */

    var algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    chart.saveChart("RNSGAII", BitmapEncoder.BitmapFormat.PNG);
    var population = algorithm.getResult() ;
    var computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    System.exit(0);
  }

  private static List<List<Double>> convertReferencePointListToListOfLists(List<Double> referencePoints, int numberOfObjectives) {
    List<List<Double>> referencePointList = new ArrayList<>();

    for (var i = 0; i <= (referencePoints.size() - numberOfObjectives); i+=numberOfObjectives) {
        @NotNull List<Double> newReferencePoint = new ArrayList<>(numberOfObjectives);
      var bound = (i + numberOfObjectives);
        for (var i1 = i; i1 < bound; i1++) {
          var aDouble = referencePoints.get(i1);
            newReferencePoint.add(aDouble);
        }

        referencePointList.add(newReferencePoint) ;
    }

    return referencePointList ;
  }

  private static class ChartListener implements MeasureListener<List<DoubleSolution>> {
    private ChartContainerWithReferencePoints chart;
    private int iteration = 0;

    public ChartListener(ChartContainerWithReferencePoints chart) {
      this.chart = chart;
      this.chart.getFrontChart().setTitle("Evaluation: " + this.iteration);
    }

    private void refreshChart(List<DoubleSolution> solutionList) {
      if (this.chart != null) {
        iteration++;
        this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
        this.chart.updateFrontCharts(solutionList);
        this.chart.refreshCharts();

        new SolutionListOutput(solutionList)
                .setVarFileOutputContext(new DefaultFileOutputContext("VAR." + iteration + ".tsv"))
                .setFunFileOutputContext(new DefaultFileOutputContext("FUN." + iteration + ".tsv"))
                .print();
      }
    }

    @Override
    synchronized public void measureGenerated(@NotNull List<DoubleSolution> solutions) {
      refreshChart(solutions);
    }
  }

  private static class IterationListener implements MeasureListener<Long> {
    ChartContainerWithReferencePoints chart;

    public IterationListener(ChartContainerWithReferencePoints chart) {
      this.chart = chart;
      this.chart.getFrontChart().setTitle("Iteration: " + 0);
    }

    @Override
    synchronized public void measureGenerated(Long iteration) {
      if (this.chart != null) {
        this.chart.getFrontChart().setTitle("Iteration: " + iteration);
      }
    }
  }
}
