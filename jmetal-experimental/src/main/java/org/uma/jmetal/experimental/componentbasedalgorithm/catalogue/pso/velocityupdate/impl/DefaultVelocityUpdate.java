package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl;

import java.util.List;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestselection.GlobalBestSelection;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

/**
 * Method implementing a constrained velocity update. This scheme is used in, for example, SMPSO.
 * <p>
 * TODO How to compute deltaMax and deltaMin
 *
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class DefaultVelocityUpdate implements VelocityUpdate {

  protected double c1Max;
  protected double c1Min;
  protected double c2Max;
  protected double c2Min;
  protected double weightMax;
  protected double weightMin;

  protected JMetalRandom randomGenerator;

  /**
   * Constructor
   *
   * @param c1Min:     Min value for c1.
   * @param c1Max:     Max value for c1.
   * @param c2Min:     Min value for c2.
   * @param c2Max:     Max value for c2.
   * @param weightMin: Min value for inertia.
   * @param weightMax: Max value for inertia.
   */
  public DefaultVelocityUpdate(double c1Min,
      double c1Max,
      double c2Min,
      double c2Max,
      double weightMin,
      double weightMax) {
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;
    this.weightMax = weightMax;
    this.weightMin = weightMin;

    this.randomGenerator = JMetalRandom.getInstance();
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
      BoundedArchive<DoubleSolution> leaders, GlobalBestSelection globalBestSelection) {
    double r1, r2, c1, c2;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) localBest[i].copy();

      bestGlobal = globalBestSelection.select(leaders.getSolutionList()) ;

      r1 = randomGenerator.nextDouble(0, 1);
      r2 = randomGenerator.nextDouble(0, 1);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);

      double inertiaWeight = randomGenerator.nextDouble(weightMin, weightMax);

      for (int var = 0; var < particle.variables().size(); var++) {
        speed[i][var] = inertiaWeight * speed[i][var]
            + c1 * r1 * (bestParticle.variables().get(var) - particle.variables().get(var))
            + c2 * r2 * (bestGlobal.variables().get(var) - particle.variables().get(var));
      }
    }

    return speed;
  }
}
