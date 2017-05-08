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

package org.uma.jmetal.runner.multiobjective;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.wasfga.WASFGAMeasures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.operator.impl.crossover.SBXCrossover;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.operator.impl.selection.BinaryTournamentSelection;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.chartcontainer.ChartContainer;
import org.uma.jmetal.util.comparator.RankingAndCrowdingDistanceComparator;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;

public class WASFGAMeasuresRunner extends AbstractAlgorithmRunner {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
 * @throws IOException 
   */
  public static void main(String[] args) throws JMetalException, IOException {
    Problem<DoubleSolution> problem;
    Algorithm<List<DoubleSolution>> algorithm;
    CrossoverOperator<DoubleSolution> crossover;
    MutationOperator<DoubleSolution> mutation;
    SelectionOperator<List<DoubleSolution>, DoubleSolution> selection;
    String referenceParetoFront = "" ;
    List<Double> referencePoint = null;

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
      referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/ZDT1.pf" ;
    }

    problem = ProblemUtils.<DoubleSolution> loadProblem(problemName);
    
    referencePoint = new ArrayList<>();
    referencePoint.add(0.5);
    referencePoint.add(0.6);

    double crossoverProbability = 0.9 ;
    double crossoverDistributionIndex = 20.0 ;
    crossover = new SBXCrossover(crossoverProbability, crossoverDistributionIndex) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    selection = new BinaryTournamentSelection<DoubleSolution>(new RankingAndCrowdingDistanceComparator<DoubleSolution>());

    algorithm = new WASFGAMeasures<DoubleSolution>(problem, 100, 250, crossover, mutation, selection,new SequentialSolutionListEvaluator<DoubleSolution>(),referencePoint) ;

    
    /* Measure management */
    MeasureManager measureManager = ((WASFGAMeasures<DoubleSolution>) algorithm).getMeasureManager();

    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");
    CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentEvaluation");

    ChartContainer chart = new ChartContainer(algorithm.getName(), 200);
    chart.SetFrontChart(0, 1, referenceParetoFront);
    chart.SetReferencePoint(referencePoint);
    chart.SetVarChart(0, 1);
    chart.InitChart();

    solutionListMeasure.register(new ChartListener(chart));
    iterationMeasure.register(new IterationListener(chart));

    /* End of measure management */
    
    
    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    chart.SaveChart("WASFGA", BitmapFormat.PNG);
    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    printFinalSolutionSet(population);
    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront) ;
    }
  }
  
  private static class ChartListener implements MeasureListener<List<DoubleSolution>> {
      private ChartContainer chart;
      private int iteration = 0;

      public ChartListener(ChartContainer chart) {
          this.chart = chart;
          this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
      }

      private void refreshChart(List<DoubleSolution> solutionList) {
          if (this.chart != null) {
              iteration++;
              this.chart.getFrontChart().setTitle("Iteration: " + this.iteration);
              this.chart.UpdateFrontCharts(solutionList);
              this.chart.RefreshCharts();
          }
      }

      @Override
      synchronized public void measureGenerated(List<DoubleSolution> solutions) {
          refreshChart(solutions);
      }
  }

  private static class IterationListener implements MeasureListener<Long> {
      ChartContainer chart;

      public IterationListener(ChartContainer chart) {
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
