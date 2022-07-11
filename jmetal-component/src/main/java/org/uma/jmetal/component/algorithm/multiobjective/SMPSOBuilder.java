package org.uma.jmetal.component.algorithm.multiobjective;

import org.uma.jmetal.component.algorithm.ParticleSwarmOptimizationAlgorithm;
import org.uma.jmetal.component.catalogue.common.evaluation.Evaluation;
import org.uma.jmetal.component.catalogue.common.evaluation.impl.SequentialEvaluation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.impl.BinaryTournamentGlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.component.catalogue.pso.positionupdate.PositionUpdate;
import org.uma.jmetal.component.catalogue.pso.positionupdate.impl.DefaultPositionUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DefaultDominanceComparator;
import org.uma.jmetal.util.termination.Termination;
import org.uma.jmetal.util.termination.impl.TerminationByEvaluations;

/**
 * Class to configure and build an instance of the SMPSO algorithm
 */
public class SMPSOBuilder {
  private final String name ;
  private SolutionsCreation<DoubleSolution> swarmInitialization;
  private Evaluation<DoubleSolution> evaluation;
  private Termination termination;
  private VelocityInitialization velocityInitialization;
  private LocalBestInitialization localBestInitialization;
  private GlobalBestInitialization globalBestInitialization;
  private InertiaWeightComputingStrategy inertiaWeightComputingStrategy;
  private VelocityUpdate velocityUpdate;
  private PositionUpdate positionUpdate;
  private Perturbation perturbation;
  private GlobalBestUpdate globalBestUpdate;
  private LocalBestUpdate localBestUpdate;
  private GlobalBestSelection globalBestSelection;
  private BoundedArchive<DoubleSolution> externalArchive;

  public SMPSOBuilder(DoubleProblem problem, int swarmSize) {
    name = "SMPSO";

    swarmInitialization = new RandomSolutionsCreation<>(problem, swarmSize);
    evaluation = new SequentialEvaluation<>(problem);
    termination = new TerminationByEvaluations(25000);
    velocityInitialization = new DefaultVelocityInitialization();
    localBestInitialization = new DefaultLocalBestInitialization();
    globalBestInitialization = new DefaultGlobalBestInitialization();

    externalArchive = new CrowdingDistanceArchive<>(swarmSize);
    //globalBestSelection ; //= new TournamentGlobalBestSelection(2, externalArchive.getComparator());
    globalBestSelection = new BinaryTournamentGlobalBestSelection(externalArchive.getComparator()) ;
    //globalBestSelection = new RandomGlobalBestSelection() ;

    double r1Min = 0.0;
    double r1Max = 1.0;
    double r2Min = 0.0;
    double r2Max = 1.0;
    double c1Min = 1.5;
    double c1Max = 2.5;
    double c2Min = 1.5;
    double c2Max = 2.5;
    double weight = 0.1;
    inertiaWeightComputingStrategy = new ConstantValueStrategy(weight) ;

    velocityUpdate = new ConstrainedVelocityUpdate(r1Min, r1Max, r2Min, r2Max, c1Min, c1Max,
        c2Min, c2Max, problem);

    double velocityChangeWhenLowerLimitIsReached = -1.0;
    double velocityChangeWhenUpperLimitIsReached = -1.0;
    positionUpdate = new DefaultPositionUpdate(velocityChangeWhenLowerLimitIsReached,
        velocityChangeWhenUpperLimitIsReached, problem.getVariableBounds());

    int frequencyOfMutation = 6;
    MutationOperator<DoubleSolution> mutationOperator = new PolynomialMutation(1.0 / problem.getNumberOfVariables(), 20.0) ;
    perturbation = new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfMutation);
    globalBestUpdate = new DefaultGlobalBestUpdate();
    localBestUpdate = new DefaultLocalBestUpdate(new DefaultDominanceComparator<>());
  }

  public SMPSOBuilder setTermination(Termination termination) {
    this.termination = termination;

    return this;
  }

  public SMPSOBuilder setArchive(BoundedArchive<DoubleSolution> externalArchive) {
    this.externalArchive = externalArchive;

    return this;
  }

  public SMPSOBuilder setEvaluation(Evaluation<DoubleSolution> evaluation) {
    this.evaluation = evaluation;

    return this;
  }

  public SMPSOBuilder setPerturbation(Perturbation perturbation) {
    this.perturbation = perturbation ;

    return this ;
  }

  public SMPSOBuilder setPositionUpdate(PositionUpdate positionUpdate) {
    this.positionUpdate = positionUpdate ;

    return this ;
  }

  public SMPSOBuilder setGlobalBestInitialization(GlobalBestInitialization globalBestInitialization) {
    this.globalBestInitialization = globalBestInitialization ;

    return this ;
  }

  public SMPSOBuilder setGlobalBestUpdate(GlobalBestUpdate globalBestUpdate) {
    this.globalBestUpdate = globalBestUpdate ;

    return this ;
  }

  public ParticleSwarmOptimizationAlgorithm build() {
    return new ParticleSwarmOptimizationAlgorithm(name, swarmInitialization, evaluation, termination,
        velocityInitialization,
        localBestInitialization,
        globalBestInitialization,
        inertiaWeightComputingStrategy,
        velocityUpdate,
        positionUpdate,
        perturbation,
        globalBestUpdate,
        localBestUpdate,
        globalBestSelection,
        externalArchive);
  }
}
