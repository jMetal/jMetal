package org.uma.jmetal.experimental.auto.algorithm.omopso;

import java.util.ArrayList;
import org.uma.jmetal.experimental.auto.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.experimental.auto.parameter.catalogue.CompositeDoubleSolutionMutation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.impl.TournamentGlobalBestSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.inertiaweightcomputingstrategy.impl.RandomSelectedValueStrategy;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl.DefaultVelocityUpdate;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.NullMutation;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.problem.multiobjective.wfg.WFG2;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.fileoutput.SolutionListOutput;
import org.uma.jmetal.util.fileoutput.impl.DefaultFileOutputContext;
import org.uma.jmetal.util.observer.impl.RunTimeChartObserver;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

/**
 * @author  Daniel Doblas Jiménez <dandobjim@uma.es>
 */
public class ComponentBasedOMOPSO {
  public static void main(String[] args) {
    DoubleProblem problem = new WFG2();
    String referenceFrontFileName = "resources/referenceFrontsCSV/WFG2.2D.csv" ;
    int swarmSize = 100;
    int maximumNumberOfEvaluations = 25000;

    var swarmInitialization = new RandomSolutionsCreation<>(problem, swarmSize);
    var evaluation = new SequentialEvaluation<>(problem);
    var termination = new TerminationByEvaluations(maximumNumberOfEvaluations);
    var velocityInitialization = new DefaultVelocityInitialization();
    var localBestInitialization = new DefaultLocalBestInitialization();
    var globalBestInitialization = new DefaultGlobalBestInitialization();

    BoundedArchive<DoubleSolution> externalArchive = new CrowdingDistanceArchive<>(swarmSize);
    var globalBestSelection = new TournamentGlobalBestSelection(2, externalArchive.getComparator()) ;

    ArrayList<MutationOperator<DoubleSolution>> operators = new ArrayList<>();
    operators.add(new UniformMutation(1.0 / problem.getNumberOfVariables(), 0.5));
    operators.add(new NonUniformMutation(1.0 / problem.getNumberOfVariables(), 0.5, 250));
    operators.add(new NullMutation<>());
    CompositeDoubleSolutionMutation mutation = new CompositeDoubleSolutionMutation(operators);

    double c1Min = 1.5;
    double c1Max = 2.0;
    double c2Min = 1.5;
    double c2Max = 2.0;
    double weightMin = 0.1;
    double weightMax = 0.5;

    var inertiaWeightStrategy = new RandomSelectedValueStrategy(weightMin, weightMax) ;

    var velocityUpdate = new DefaultVelocityUpdate(c1Min, c1Max, c2Min, c2Max);

    double velocityChangeWhenLowerLimitIsReached = -1.0;
    double velocityChangeWhenUpperLimitIsReached = -1.0;
    var positionUpdate = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached,
        velocityChangeWhenUpperLimitIsReached, problem.getVariableBounds());
    int frequencyOfMutation = 7;
    var perturbation = new FrequencySelectionMutationBasedPerturbation(
        new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0), frequencyOfMutation);
    //var perturbation = new FrequencySelectionMutationBasedPerturbation(mutation);
    var globalBestUpdate = new DefaultGlobalBestUpdate();
    var localBestUpdate = new DefaultLocalBestUpdate(new DefaultDominanceComparator<>());

    var omopso = new ParticleSwarmOptimizationAlgorithm("OMOPSO", swarmInitialization,
        evaluation,
        termination,
        velocityInitialization,
        localBestInitialization,
        globalBestInitialization,
        inertiaWeightStrategy,
        velocityUpdate,
        positionUpdate,
        perturbation,
        globalBestUpdate,
        localBestUpdate,
        globalBestSelection,
        externalArchive);

    RunTimeChartObserver<DoubleSolution> runTimeChartObserver = new RunTimeChartObserver<>("OMOPSO",
        80, 500, referenceFrontFileName);

    omopso.getObservable().register(runTimeChartObserver);
    omopso.run();

    System.out.println("Total computing time: " + omopso.getTotalComputingTime());

    new SolutionListOutput(omopso.getResult())
        .setVarFileOutputContext(new DefaultFileOutputContext("VAR.csv", ","))
        .setFunFileOutputContext(new DefaultFileOutputContext("FUN.csv", ","))
        .print();

    System.exit(0);
  }
}
