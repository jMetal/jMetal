package org.uma.jmetal.example.multiobjective.smpso;

import org.uma.jmetal.algorithm.multiobjective.smpso.SMPSO;
import org.uma.jmetal.component.evaluation.Evaluation;
import org.uma.jmetal.component.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.termination.Termination;
import org.uma.jmetal.component.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.AbstractAlgorithmRunner;
import org.uma.jmetal.util.ProblemUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.observer.impl.EvaluationObserver;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;

/**
 * Class for configuring and running the SMPSO algorithm
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class SMPSOWithRealTimeChartExample extends AbstractAlgorithmRunner {
  public static void main(String[] args) throws Exception {
    DoubleProblem problem;
    SMPSO algorithm;
    MutationOperator<DoubleSolution> mutation;

    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT4";
    String referenceParetoFront = "resources/referenceFrontsCSV/ZDT4.pf" ;

    problem = (DoubleProblem) ProblemUtils.<DoubleSolution>loadProblem(problemName);

    int swarmSize = 100 ;
    BoundedArchive<DoubleSolution> leadersArchive = new CrowdingDistanceArchive<DoubleSolution>(swarmSize) ;

    double mutationProbability = 1.0 / problem.getNumberOfVariables() ;
    double mutationDistributionIndex = 20.0 ;
    mutation = new PolynomialMutation(mutationProbability, mutationDistributionIndex) ;

    Evaluation<DoubleSolution> evaluation = new SequentialEvaluation<>(problem) ;
    Termination termination = new TerminationByEvaluations(25000) ;

    algorithm = new SMPSO(problem, swarmSize, leadersArchive, mutation, evaluation, termination) ;

    EvaluationObserver evaluationObserver = new EvaluationObserver(100);
    RunTimeChartObserver<DoubleSolution> runTimeChartObserver =
            new RunTimeChartObserver<>("SMPSO", 80, referenceParetoFront);

    algorithm.getObservable().register(evaluationObserver);
    algorithm.getObservable().register(runTimeChartObserver);

    algorithm.run();

    System.exit(0);
  }
}
