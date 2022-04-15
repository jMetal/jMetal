package org.uma.jmetal.algorithm.multiobjective.omopso;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.uma.jmetal.algorithm.impl.AbstractParticleSwarmOptimization;
import org.uma.jmetal.operator.mutation.impl.NonUniformMutation;
import org.uma.jmetal.operator.mutation.impl.UniformMutation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.impl.CrowdingDistanceArchive;
import org.uma.jmetal.util.archive.impl.NonDominatedSolutionListArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.DominanceWithConstraintsComparator;
import org.uma.jmetal.util.comparator.dominanceComparator.impl.EpsilonDominanceComparator;
import org.uma.jmetal.util.densityestimator.DensityEstimator;
import org.uma.jmetal.util.densityestimator.impl.CrowdingDistanceDensityEstimator;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/** Class implementing the OMOPSO algorithm */
@SuppressWarnings("serial")
public class OMOPSO extends AbstractParticleSwarmOptimization<DoubleSolution, List<DoubleSolution>> {

  private DoubleProblem problem;

  SolutionListEvaluator<DoubleSolution> evaluator;

  private final int swarmSize;
  private int maxIterations;
  private int currentIteration;

  private DoubleSolution[] localBest;
  private CrowdingDistanceArchive<DoubleSolution> leaderArchive;
  private NonDominatedSolutionListArchive<DoubleSolution> epsilonArchive;

  private double[][] speed;

  private final Comparator<DoubleSolution> dominanceComparator;
  private final Comparator<DoubleSolution> crowdingDistanceComparator;

  private final UniformMutation uniformMutation;
  private final NonUniformMutation nonUniformMutation;

  private double eta ;

  private JMetalRandom randomGenerator;
  private DensityEstimator<DoubleSolution> crowdingDistance;

  /** Constructor */
  public OMOPSO(DoubleProblem problem, SolutionListEvaluator<DoubleSolution> evaluator,
      int swarmSize, int maxIterations, int archiveSize, double eta, UniformMutation uniformMutation,
      NonUniformMutation nonUniformMutation) {
    this.problem = problem ;
    this.evaluator = evaluator ;

    this.swarmSize = swarmSize ;
    this.maxIterations = maxIterations ;

    this.eta = eta ;

    this.uniformMutation = uniformMutation ;
    this.nonUniformMutation = nonUniformMutation ;

    localBest = new DoubleSolution[swarmSize];
    leaderArchive = new CrowdingDistanceArchive<>(archiveSize);
    epsilonArchive = new NonDominatedSolutionListArchive<>(new EpsilonDominanceComparator<>(eta));

    crowdingDistance = new CrowdingDistanceDensityEstimator<>();

    dominanceComparator = new DominanceWithConstraintsComparator<>();
    crowdingDistanceComparator = crowdingDistance.getComparator();

    speed = new double[swarmSize][problem.getNumberOfVariables()];

    randomGenerator = JMetalRandom.getInstance() ;
  }


  @Override protected void initProgress() {
    currentIteration = 1;
    crowdingDistance.compute(leaderArchive.getSolutionList());
  }

  @Override protected void updateProgress() {
    currentIteration += 1;
    crowdingDistance.compute(leaderArchive.getSolutionList());
  }

  @Override protected boolean isStoppingConditionReached() {
    return currentIteration >= maxIterations;
  }

  @Override
  protected List<DoubleSolution> createInitialSwarm() {
    List<DoubleSolution> swarm = new ArrayList<>(swarmSize);

    DoubleSolution newSolution;
    for (int i = 0; i < swarmSize; i++) {
      newSolution = problem.createSolution();
      swarm.add(newSolution);
    }

    return swarm;
  }

  @Override
  protected List<DoubleSolution> evaluateSwarm(List<DoubleSolution> swarm) {
    swarm = evaluator.evaluate(swarm, problem);
    return swarm ;
  }

  @Override public List<DoubleSolution> getResult() {
    //return this.leaderArchive.getSolutionList();
      return this.epsilonArchive.getSolutionList();
  }

  @Override
  protected void initializeLeader(List<DoubleSolution> swarm) {
    for (DoubleSolution solution : swarm) {
      DoubleSolution particle = (DoubleSolution) solution.copy();
      if (leaderArchive.add(particle)) {
        epsilonArchive.add((DoubleSolution) particle.copy());
      }
    }
  }

  @Override
  protected void initializeParticlesMemory(List<DoubleSolution> swarm)  {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      localBest[i] = particle;
    }
  }

  @Override
  protected void updateVelocity(List<DoubleSolution> swarm)  {
    double r1, r2, W, C1, C2;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      DoubleSolution bestParticle = (DoubleSolution) localBest[i];

      //Select a global localBest for calculate the speed of particle i, bestGlobal
      DoubleSolution one ;
      DoubleSolution two;
      int pos1 = randomGenerator.nextInt(0, leaderArchive.getSolutionList().size() - 1);
      int pos2 = randomGenerator.nextInt(0, leaderArchive.getSolutionList().size() - 1);
      one = leaderArchive.getSolutionList().get(pos1);
      two = leaderArchive.getSolutionList().get(pos2);

      if (crowdingDistanceComparator.compare(one, two) < 1) {
        bestGlobal = one ;
      } else {
        bestGlobal = two ;
      }

      //Parameters for velocity equation
      r1 = randomGenerator.nextDouble();
      r2 = randomGenerator.nextDouble();
      C1 = randomGenerator.nextDouble(1.5, 2.0);
      C2 = randomGenerator.nextDouble(1.5, 2.0);
      W = randomGenerator.nextDouble(0.1, 0.5);
      //

      for (int var = 0; var < particle.variables().size(); var++) {
        //Computing the velocity of this particle
        speed[i][var] = W * speed[i][var] + C1 * r1 * (bestParticle.variables().get(var) -
            particle.variables().get(var)) +
            C2 * r2 * (bestGlobal.variables().get(var) - particle.variables().get(var));
      }
    }
  }

  /** Update the position of each particle */
  @Override
  protected void updatePosition(List<DoubleSolution> swarm)  {
    for (int i = 0; i < swarmSize; i++) {
      DoubleSolution particle = swarm.get(i);
      for (int var = 0; var < particle.variables().size(); var++) {
        particle.variables().set(var, particle.variables().get(var) + speed[i][var]);
        Bounds<Double> bounds = problem.getBoundsForVariables().get(var) ;
        Double lowerBound = bounds.getLowerBound() ;
        Double upperBound = bounds.getUpperBound() ;
        if (particle.variables().get(var) < lowerBound) {
          particle.variables().set(var, lowerBound);
          speed[i][var] = speed[i][var] * -1.0;
        }
        if (particle.variables().get(var) > upperBound) {
          particle.variables().set(var, upperBound);
          speed[i][var] = speed[i][var] * -1.0;
        }
      }
    }
  }

  @Override
  protected void updateParticlesMemory(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      int flag = dominanceComparator.compare(swarm.get(i), localBest[i]);
      if (flag != 1) {
        DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
        localBest[i] = particle;
      }
    }
  }

  @Override protected void initializeVelocity(List<DoubleSolution> swarm) {
    for (int i = 0; i < swarm.size(); i++) {
      for (int j = 0; j < problem.getNumberOfVariables(); j++) {
        speed[i][j] = 0.0;
      }
    }
  }

  /**  Apply a mutation operator to all particles in the swarm (perturbation) */
  @Override
  protected void perturbation(List<DoubleSolution> swarm)  {
    nonUniformMutation.setCurrentIteration(currentIteration);

    for (int i = 0; i < swarm.size(); i++) {
      if (i % 3 == 0) {
        nonUniformMutation.execute(swarm.get(i));
      } else if (i % 3 == 1) {
        uniformMutation.execute(swarm.get(i));
      }
    }
  }

  /**
   * Update leaders method
   * @param swarm List of solutions (swarm)
   */
  @Override protected void updateLeaders(List<DoubleSolution> swarm) {
    for (DoubleSolution solution : swarm) {
      DoubleSolution particle = (DoubleSolution) solution.copy();
      if (leaderArchive.add(particle)) {
        epsilonArchive.add((DoubleSolution) particle.copy());
      }
    }
  }

  @Override public String getName() {
    return "OMOPSO" ;
  }

  @Override public String getDescription() {
    return "Optimized MOPSO" ;
  }

}
