package org.uma.jmetal.component.catalogue.pso.velocityupdate.impl;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.SolutionUtils;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.pseudorandom.impl.ExtendedPseudoRandomGenerator;
import org.uma.jmetal.util.pseudorandom.impl.JavaRandomGenerator;

/**
 * Method implementing a velocity update strategy proposed in Standard PSO 2011.
 *
 * @author Antonio J. Nebro
 */
public class SPS2011VelocityUpdate implements VelocityUpdate {
  protected double c1Max;
  protected double c1Min;
  protected double c2Max;
  protected double c2Min;

  protected JMetalRandom randomGenerator;
  private DoubleProblem problem ;

  /**
   * Constructor
   *
   * @param c1Min:     Min value for c1.
   * @param c1Max:     Max value for c1.
   * @param c2Min:     Min value for c2.
   * @param c2Max:     Max value for c2.
   */
  public SPS2011VelocityUpdate(double c1Min,
      double c1Max,
      double c2Min,
      double c2Max,
      DoubleProblem problem) {
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;

    this.problem = problem ;

    this.randomGenerator = JMetalRandom.getInstance();
    randomGenerator.setRandomGenerator(new ExtendedPseudoRandomGenerator(new JavaRandomGenerator()));

  }

  @Override
  /**
   * Update the velocity of the particle. We assume that r1 and r2 have a random number between 0.0 and 1.0.
   * @param swarm: List of possible solutions.
   * @param speed: Matrix for particle speed.
   * @param localBest: List of local best particles.
   * @param leaders: List of global best particles.
   * @return Updated speed.
   */
  public double[][] update(List<DoubleSolution> swarm, double[][] speed, DoubleSolution[] localBest,
      BoundedArchive<DoubleSolution> leaders, GlobalBestSelection globalBestSelection,
      InertiaWeightComputingStrategy inertiaWeightComputingStrategy) {
    double r1, r2, c1, c2;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution globalBestParticle = globalBestSelection.select(leaders.getSolutionList()) ;

      r1 = randomGenerator.nextDouble(0, 1);
      r2 = randomGenerator.nextDouble(0, 1);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);

      DoubleSolution gravityCenter = problem.createSolution() ;
      for (int j = 0; j < particle.variables().size(); j++) {
        double value = (particle.variables().get(j) +
            particle.variables().get(j) + c1 * r1 * (localBest[i].variables().get(j)
            - particle.variables().get(j))
            + particle.variables().get(j) + c2 * r2 * (globalBestParticle.variables().get(j)
            - particle.variables().get(j))) / 3.0;

        gravityCenter.variables().set(j, value);
      }

      double radius = SolutionUtils.distanceBetweenSolutionsInObjectiveSpace(gravityCenter, particle);

      double[] random = ((ExtendedPseudoRandomGenerator)randomGenerator.getRandomGenerator()).randSphere(problem.numberOfVariables());

      DoubleSolution randomParticle = problem.createSolution() ;
      for (int j = 0; j < particle.variables().size(); j++) {
        randomParticle.variables().set(j, gravityCenter.variables().get(j) + radius * random[j]);
      }

      double inertiaWeight = inertiaWeightComputingStrategy.compute() ;
      for (int j = 0; j < particle.variables().size(); j++) {
        speed[i][j] =
            inertiaWeight * speed[i][j] + randomParticle.variables().get(j) - particle.variables().get(j);
      }
    }

    return speed;
  }
}
