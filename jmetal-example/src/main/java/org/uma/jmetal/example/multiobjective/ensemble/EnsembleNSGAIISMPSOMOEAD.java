package org.uma.jmetal.example.multiobjective.ensemble;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.algorithm.multiobjective.abyss.ABYSSBuilder;
import org.uma.jmetal.algorithm.multiobjective.ensemble.AlgorithmEnsemble;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAII;
import org.uma.jmetal.algorithm.multiobjective.nsgaii.NSGAIIWithArchive;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSOWithArchive;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.example.AlgorithmRunner;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.archive.Archive;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 *   @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class EnsembleNSGAIISMPSOMOEAD extends AbstractAlgorithmRunner {

  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    Algorithm<List<DoubleSolution>> algorithm;
    String problemName ;

    problemName = "org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1";
    String referenceParetoFront = "resources/referenceFronts/DTLZ1.3D.pf" ;

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution> loadProblem(problemName);

    Archive<DoubleSolution> archive = new NonDominatedSolutionListArchive<>() ;

    double crossoverProbability = 0.9;
    double crossoverDistributionIndex = 20.0;

    double mutationProbability = 1.0 / problem.getNumberOfVariables();
    double mutationDistributionIndex = 20.0;

    int populationSize = 100;
    int offspringPopulationSize = populationSize;

    Termination termination = new TerminationByEvaluations(50000);

    Algorithm<List<DoubleSolution>> nsgaII =
            new NSGAIIWithArchive<>(
                    problem,
                    populationSize,
                    offspringPopulationSize,
                    new SBXCrossover(crossoverProbability, crossoverDistributionIndex),
                    new PolynomialMutation(mutationProbability, mutationDistributionIndex),
                    termination, new NonDominatedSolutionListArchive<>());

    int swarmSize = 100;
    BoundedArchive<DoubleSolution> leadersArchive =
            new CrowdingDistanceArchive<DoubleSolution>(swarmSize);

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>();

    Algorithm<List<DoubleSolution>> smpso =
            new SMPSOWithArchive(
                    (DoubleProblem) problem,
                    swarmSize,
                    leadersArchive,
                    new PolynomialMutation(mutationProbability, mutationDistributionIndex),
                    evaluation,
                    termination, new NonDominatedSolutionListArchive<>());

    List<Algorithm<List<DoubleSolution>>> algorithmList = new ArrayList<>();
    algorithmList.add(nsgaII);
    algorithmList.add(smpso);

    AlgorithmEnsemble<DoubleSolution> algorithmEnsemble =
            new AlgorithmEnsemble<>(algorithmList, archive);

    algorithmEnsemble.run();

    List<DoubleSolution> population = SolutionListUtils.distanceBasedSubsetSelection(algorithmEnsemble.getResult(), 100);
    JMetalLogger.logger.info("Total execution time : " + algorithmEnsemble.getTotalComputingTime() + "ms");

    new SolutionListOutput(population)
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    JMetalLogger.logger.info("Random seed: " + JMetalRandom.getInstance().getSeed());
    JMetalLogger.logger.info("Objectives values have been written to file FUN.csv");
    JMetalLogger.logger.info("Variables values have been written to file VAR.csv");

    if (!referenceParetoFront.equals("")) {
      printQualityIndicators(population, referenceParetoFront);
    }
  }
}
