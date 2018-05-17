package org.uma.jmetal.runner.multiobjective;

import org.knowm.xchart.BitmapEncoder;
import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.newideas.cosine.rsmpso.algorithm.SMPSORP;
import org.uma.jmetal.newideas.cosine.rsmpso.archive.archivewithreferencepoint.ArchiveWithReferencePoint;
import org.uma.jmetal.newideas.cosine.rsmpso.archive.archivewithreferencepoint.impl.CrowdingDistanceArchiveWithReferencePoint;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.mutation.PolynomialMutation;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.AlgorithmRunner;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.impl.HypervolumeArchive;
import org.uma.jmetal.util.evaluator.impl.SequentialSolutionListEvaluator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SMPSORPWithMultipleReferencePointsAndChartsRunner2 {
  /**
   * @param args Command line arguments.
   * @throws JMetalException
 * @throws IOException
   */
  public static void main(String[] args) throws JMetalException, IOException {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    MutationOperator<DoubleSolution> mutation;
    String referenceParetoFront = "" ;

    JMetalRandom.getInstance().setSeed(3);

    String problemName ;
    if (args.length == 1) {
      problemName = args[0];
    } else if (args.length == 2) {
      problemName = args[0] ;
      referenceParetoFront = args[1] ;
    } else {
      problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2" ;
      referenceParetoFront = "DTLZ2.2D.pf" ;
      problemName = "org.uma.jmetal.problem.multiobjective.wfg.WFG5" ;
      referenceParetoFront = "WFG5.2D.pf" ;
      problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1" ;
      referenceParetoFront = "ZDT1.pf" ;
      //problemName = "org.uma.jmetal.problem.multiobjective.lz09.LZ09F2" ;
      //referenceParetoFront = "LZ09_F2.pf" ;
    }

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);
    //problem = new DTLZ4(7, 3);
    //referenceParetoFront = "DTLZ4.2D.pf" ;
    //problem = new WFG4(2, 4, 3);
    //referenceParetoFront = "WFG4.2D.pf" ;

    List<List<Double>> referencePoints;
    referencePoints = new ArrayList<>();
    //referencePoints.add(Arrays.asList(0.0, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.0, 0.0)) ;
    referencePoints.add(Arrays.asList(0.1, 1.0)) ;
    referencePoints.add(Arrays.asList(0.4, 0.5)) ;
    referencePoints.add(Arrays.asList(0.7, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.8, 0.2)) ;
    //referencePoints.add(Arrays.asList(0.6, 1.0)) ;
    //referencePoints.add(Arrays.asList(0.7, 0.7)) ;
    //referencePoints.add(Arrays.asList(0.7, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.2, 0.7)) ;
    //referencePoints.add(Arrays.asList(0.8, 0.3)) ;
    //referencePoints.add(Arrays.asList(0.1, 0.1)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.2)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.5, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.0, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.25, 0.0, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.0, 0.25, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.0, 0.0, 0.25)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.5)) ;

/*

    referencePoints.add(Arrays.asList(0.0, 0.0, 2.0)) ;
    referencePoints.add(Arrays.asList(0.75, 0.0, 2.0)) ;
    referencePoints.add(Arrays.asList(0.0, 0.75, 2.0)) ;
    referencePoints.add(Arrays.asList(0.75, 0.75, 2.0)) ;
    //referencePoints.add(Arrays.asList(0.1, 0.1, 0.1)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.0)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.5)) ;

    //referencePoints.add(Arrays.asList(2000.0, 2000.0)) ;
    //referencePoints.add(Arrays.asList(0.2, 0.2)) ;
    //referencePoints.add(Arrays.asList(0.0, 0.5)) ;
    //referencePoints.add(Arrays.asList(0.5, 0.0)) ;
/*

    referencePoints.add(Arrays.asList(0.2, 1.0)) ;
    referencePoints.add(Arrays.asList(0.6, 0.6)) ;
    referencePoints.add(Arrays.asList(1.0, 0.2)) ;
*/
    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    int maxIterations = 250;
    int swarmSize = 100 ;

    List<ArchiveWithReferencePoint<DoubleSolution>> archivesWithReferencePoints = new ArrayList<>();

    for (int i = 0 ; i < referencePoints.size(); i++) {
      archivesWithReferencePoints.add(
          new CrowdingDistanceArchiveWithReferencePoint<DoubleSolution>(
              swarmSize/referencePoints.size(), referencePoints.get(i))) ;
          //new HypervolumeArchiveWithReferencePoint<>(
          //        swarmSize/referencePoints.size(), referencePoints.get(i))) ;
          //new CosineDistanceArchiveWithReferencePoint<>(
          //        swarmSize/referencePoints.size()/1,
          //            new NadirPoint(problem.getNumberOfObjectives()),
          //    referencePoints.get(i))) ;
      //new AngleDistanceArchiveWithReferencePoint<>(
      //    swarmSize/referencePoints.size(),
      //    new IdealPoint(problem.getNumberOfObjectives()),
      //    referencePoints.get(i))) ;
    }

    algorithm = new SMPSORP(problem,
            swarmSize,
            archivesWithReferencePoints,
            referencePoints,
            mutation,
            maxIterations,
            0.0, 1.0,
            0.0, 1.0,
            2.5, 1.5,
            2.5, 1.5,
            0.1, 0.1,
            -1.0, -1.0,
            new SequentialSolutionListEvaluator<>() );

    /* Measure management */
    MeasureManager measureManager = ((SMPSORP) algorithm).getMeasureManager();

    BasicMeasure<List<DoubleSolution>> solutionListMeasure = (BasicMeasure<List<DoubleSolution>>) measureManager
            .<List<DoubleSolution>>getPushMeasure("currentPopulation");
    CountingMeasure iterationMeasure = (CountingMeasure) measureManager.<Long>getPushMeasure("currentIteration");

    ChartContainer chart = new ChartContainer(algorithm.getName(), 80);
    chart.setFrontChart(0, 1, referenceParetoFront);
    chart.setReferencePoint(referencePoints);
    chart.initChart();

    solutionListMeasure.register(new ChartListener(chart));
    iterationMeasure.register(new IterationListener(chart));

    /* End of measure management */

    AlgorithmRunner algorithmRunner = new AlgorithmRunner.Executor(algorithm)
            .execute() ;

    chart.saveChart("RSMPSO", BitmapEncoder.BitmapFormat.PNG);
    List<DoubleSolution> population = algorithm.getResult() ;
    long computingTime = algorithmRunner.getComputingTime() ;

    JMetalLogger.logger.info("Total execution time: " + computingTime + "ms");

    //((SMPSORP) algorithm).removeDominatedSolutionsInArchives();

    new SolutionListOutput(population)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.tsv"))
            .print();

    for (int i = 0 ; i < archivesWithReferencePoints.size(); i++) {
      new SolutionListOutput(archivesWithReferencePoints.get(i).getSolutionList())
          .setSeparator("\t")
          .setVarFileOutputContext(new DefaultFileOutputContext("VAR" + i + ".tsv"))
          .setFunFileOutputContext(new DefaultFileOutputContext("FUN" + i + ".tsv"))
          .print();
    }

    HypervolumeArchive<DoubleSolution> resultArchive = new HypervolumeArchive<>(80, new PISAHypervolume<>());
    for (ArchiveWithReferencePoint<DoubleSolution> archive : archivesWithReferencePoints) {
      for (DoubleSolution solution: archive.getSolutionList()) {
        resultArchive.add(solution) ;
      }
    }

    new SolutionListOutput(resultArchive.getSolutionList())
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("JOIN.tsv"))
            .print();

    System.exit(0);
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
        this.chart.updateFrontCharts(solutionList);
        this.chart.refreshCharts();

        new SolutionListOutput(solutionList)
            .setSeparator("\t")
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR." + iteration + ".tsv"))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN." + iteration + ".tsv"))
            .print();
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
