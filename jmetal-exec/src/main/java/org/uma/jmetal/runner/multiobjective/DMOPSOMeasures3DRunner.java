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

import java.util.List;

import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.uma.jmetal.algorithm.multiobjective.dmopso.DMOPSO;
import org.uma.jmetal.algorithm.multiobjective.dmopso.DMOPSOMeasures;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.runner.AbstractAlgorithmRunner;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.chartcontainer.ChartContainer3D;

/**
 * Class for configuring and running the DMOPSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */

public class DMOPSOMeasures3DRunner extends AbstractAlgorithmRunner {
    /**
     * @param args
     *            Command line arguments.
     * @throws org.uma.jmetal.util.JMetalException
     * @throws java.io.IOException
     * @throws SecurityException
     *             Invoking command: java
     *             org.uma.jmetal.runner.multiobjective.DMOPSORunner problemName
     *             [referenceFront]
     */
    public static void main(String[] args) throws Exception {
        DoubleProblem problem;
        DMOPSO algorithm;

        String referenceParetoFront = "";

        String problemName;
        if (args.length == 1) {
            problemName = args[0];
        } else if (args.length == 2) {
            problemName = args[0];
            referenceParetoFront = args[1];
        } else {
            problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2";
            referenceParetoFront = "jmetal-problem/src/test/resources/pareto_fronts/DTLZ2.3D.pf";
        }

        problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

        algorithm = new DMOPSOMeasures(problem, 300, 150, 0.0, 0.1, 0.0, 1.0, 1.5, 2.5, 1.5, 2.5, 0.1, 0.4, -1.0, -1.0,
                DMOPSO.FunctionType.TCHE, "MOEAD_Weights", 2);

        /* Measure management */
        MeasureManager measureManager = ((DMOPSOMeasures) algorithm).getMeasureManager();

        // CountingMeasure currentEvalution = (CountingMeasure)
        // measureManager.<Long>getPullMeasure("currentEvaluation");
        // DurationMeasure currentComputingTime = (DurationMeasure)
        // measureManager
        // .<Long>getPullMeasure("currentExecutionTime");

        BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
                .<List<DoubleSolution>>getPushMeasure("currentPopulation");

        ChartContainer3D chart = new ChartContainer3D(algorithm.getName(), 200, 600, 400);
        chart.DisplayReferenceFront(referenceParetoFront);
        solutionListMeasure.register(new Chart3DListener(chart));

        /* End of measure management */

        AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm).execute();
        chart.SaveChart("./chart", BitmapFormat.PNG);

        List<DoubleSolution> population = algorithm.getResult();
        long computingTime = algorithmRunner.getComputingTime();

        JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

        printFinalSolutionSet(population);
        if (!referenceParetoFront.equals("")) {
            printQualityIndicators(population, referenceParetoFront);
        }

    }

    private static class Chart3DListener implements MeasureListener<List<DoubleSolution>> {
        private ChartContainer3D chart;
        private int iteration = 0;

        public Chart3DListener(ChartContainer3D chart) {
            this.chart = chart;
            initChart();
        }

        private void initChart() {
            if (this.chart != null) {
                double[] xData = new double[] { 0 };
                double[] yData = new double[] { 0 };
                double[] zData = new double[] { 0 };
                this.chart.InitChart(xData, yData, zData);
                this.chart.getChart(0).setTitle("Iteration = " + iteration);
                this.chart.getChart(1).setTitle("Iteration = " + iteration);
                this.chart.getChart(2).setTitle("Iteration = " + iteration);
            }
        }

        private void refreshChart(List<DoubleSolution> solutionList) {
            if (this.chart != null) {
                double[] xData = SolutionListUtils.getObjectiveArrayFromSolutionList(solutionList, 0);
                double[] yData = SolutionListUtils.getObjectiveArrayFromSolutionList(solutionList, 1);
                double[] zData = SolutionListUtils.getObjectiveArrayFromSolutionList(solutionList, 2);
                this.chart.RefreshChart(xData, yData, zData);
                iteration++;
                this.chart.getChart(0).setTitle("Iteration = " + iteration);
                this.chart.getChart(1).setTitle("Iteration = " + iteration);
                this.chart.getChart(2).setTitle("Iteration = " + iteration);
            }
        }

        @Override
        synchronized public void measureGenerated(List<DoubleSolution> solutions) {
            refreshChart(solutions);
        }
    }
}
