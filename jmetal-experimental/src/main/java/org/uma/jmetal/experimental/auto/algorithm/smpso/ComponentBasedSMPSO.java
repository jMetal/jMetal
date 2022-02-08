package org.uma.jmetal.experimental.auto.algorithm.smpso;

import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl.MutationBasedPerturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ1;
import org.uma.jmetal.problem.multiobjective.dtlz.DTLZ2;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT1;
import org.uma.jmetal.problem.multiobjective.zdt.ZDT4;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

public class ComponentBasedSMPSO {
  public static void main(String[] args) {
    JMetalRandom.getInstance().setSeed(1);

    DoubleProblem problem = new ZDT4();
    String referenceFrontFileName = "resources/referenceFrontsCSV/ZDT4.csv" ;
    int swarmSize = 100;
    int maximumNumberOfEvaluations = 25000;

    var swarmInitialization = new RandomSolutionsCreation<>(problem, swarmSize);
    var evaluation = new SequentialEvaluation<>(problem);
    var termination = new TerminationByEvaluations(maximumNumberOfEvaluations);
    var velocityInitialization = new DefaultVelocityInitialization();
    var localBestInitialization = new DefaultLocalBestInitialization();
    var globalBestInitialization = new DefaultGlobalBestInitialization();

    double r1Min = 0.0;
    double r1Max = 1.0;
    double r2Min = 0.0;
    double r2Max = 1.0;
    double c1Min = 1.5;
    double c1Max = 2.5;
    double c2Min = 1.5;
    double c2Max = 2.5;
    double weightMin = 0.1;
    double weightMax = 0.1;
    var velocityUpdate = new ConstrainedVelocityUpdate(r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin, weightMax, problem);

    double velocityChangeWhenLowerLimitIsReached = -1.0;
    double velocityChangeWhenUpperLimitIsReached = -1.0;
    var positionUpdate = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached, velocityChangeWhenUpperLimitIsReached, problem.getBoundsForVariables());

    var perturbation = new MutationBasedPerturbation(new PolynomialMutation(1.0/problem.getNumberOfVariables(), 20.0)) ;
    var globalBestUpdate = new DefaultGlobalBestUpdate() ;
    var localBestUpdate = new DefaultLocalBestUpdate(new DominanceComparator<>()) ;

    BoundedArchive<DoubleSolution> externalArchive = new CrowdingDistanceArchive<>(swarmSize) ;

    var smpso = new ParticleSwarmOptimizationAlgorithm("SMPSO",
            swarmInitialization,
            evaluation,
            termination,
            velocityInitialization,
            localBestInitialization,
            globalBestInitialization,
            velocityUpdate,
            positionUpdate,
            perturbation,
            globalBestUpdate,
            localBestUpdate,
            externalArchive);

    RunTimeChartObserver<DoubleSolution> runTimeChartObserver = new RunTimeChartObserver<>("SMPSO", 80, 100, referenceFrontFileName);
    smpso.getObservable().register(runTimeChartObserver);

    smpso.run();

    System.out.println("Total computing time: " + smpso.getTotalComputingTime()) ;

    new SolutionListOutput(smpso.getResult())
            .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
            .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
            .print();

    System.exit(0);
  }
}
