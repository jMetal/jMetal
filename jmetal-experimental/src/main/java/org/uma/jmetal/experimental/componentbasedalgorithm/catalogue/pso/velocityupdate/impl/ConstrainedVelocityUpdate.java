package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.archive.BoundedArchive;
import org.uma.jmetal.util.bounds.Bounds;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;

import java.util.List;

/**
 * Method implementing a constrained velocity update. This scheme is used in, for example, SMPSO.
 ** TODO: Finalizar documentacion
 * @author Antonio J. Nebro
 * @author Daniel Doblas
 */
public class ConstrainedVelocityUpdate implements VelocityUpdate {
  protected double c1Max;
  protected double c1Min;
  protected double c2Max;
  protected double c2Min;
  protected double r1Max;
  protected double r1Min;
  protected double r2Max;
  protected double r2Min;
  protected double weightMax;
  protected double weightMin;

  protected JMetalRandom randomGenerator;

  protected double[] deltaMax;
  protected double[] deltaMin;

  /**
   * Constructor
   * @param r1Min double min value of uniformly distributed random number. Usually number in range [0,1].
   * @param r1Max double max value of uniformly distributed random number. Usually number in range [0,1].
   * @param r2Min double min value of uniformly distributed random number. Usually number in range [0,1].
   * @param r2Max double max value of uniformly distributed random number. Usually number in range [0,1].
   * @param c1Min: Min value for c1.
   * @param c1Max: Max value for c1.
   * @param c2Min: Min value for c2.
   * @param c2Max: Max value for c2.
   * @param weightMin: Min value for inertia.
   * @param weightMax: Max value for inertia.
   */
  public ConstrainedVelocityUpdate(double r1Min,
                                   double r1Max,
                                   double r2Min,
                                   double r2Max,
                                   double c1Min,
                                   double c1Max,
                                   double c2Min,
                                   double c2Max,
                                   double weightMin,
                                   double weightMax,
                                   DoubleProblem problem) {
    this.r1Max = r1Max;
    this.r1Min = r1Min;
    this.r2Max = r2Max;
    this.r2Min = r2Min;
    this.c1Max = c1Max;
    this.c1Min = c1Min;
    this.c2Max = c2Max;
    this.c2Min = c2Min;
    this.weightMax = weightMax;
    this.weightMin = weightMin;

    this.randomGenerator = JMetalRandom.getInstance() ;

    deltaMax = new double[problem.getNumberOfVariables()];
    deltaMin = new double[problem.getNumberOfVariables()];
    for (int i = 0; i < problem.getNumberOfVariables(); i++) {
      Bounds<Double> bounds = problem.getBoundsForVariables().get(i);
      deltaMax[i] = (bounds.getUpperBound() - bounds.getLowerBound()) / 2.0;
      deltaMin[i] = -deltaMax[i];
    }
  }

  @Override
  /**
   * Update the velocity of the particle.
   * @param swarm: List of possible solutions.
   * @param speed: Matrix for particle speed.
   * @param localBest: List of local best particles.
   * @param leaders: List of global best particles.
   * @return Updated speed.
   */
  public double[][] update(List<DoubleSolution> swarm, double[][] speed, DoubleSolution[] localBest, BoundedArchive<DoubleSolution> leaders) {
    double r1, r2, c1, c2;
    DoubleSolution bestGlobal;

    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution)localBest[i].copy();

      bestGlobal = selectGlobalBest(leaders);

      r1 = randomGenerator.nextDouble(r1Min, r1Max);
      r2 = randomGenerator.nextDouble(r2Min, r2Max);
      c1 = randomGenerator.nextDouble(c1Min, c1Max);
      c2 = randomGenerator.nextDouble(c2Min, c2Max);

      for (int var = 0; var < particle.variables().size(); var++) {
        speed[i][var] =
                velocityConstriction(
                        constrictionCoefficient(c1, c2)
                                * (weightMax * speed[i][var]
                                + c1 * r1 * (bestParticle.variables().get(var) - particle.variables().get(var))
                                + c2 * r2 * (bestGlobal.variables().get(var) - particle.variables().get(var))),
                        deltaMax,
                        deltaMin,
                        var);
      }
    }

    return speed ;
  }

  /**
   *
   * @param leaders: List of global best particles.
   * @return Best Solution's list updated.
   */
  protected DoubleSolution selectGlobalBest(BoundedArchive<DoubleSolution> leaders)  {
    DoubleSolution one, two;
    DoubleSolution bestGlobal;
    int pos1 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    int pos2 = randomGenerator.nextInt(0, leaders.getSolutionList().size() - 1);
    one = leaders.getSolutionList().get(pos1);
    two = leaders.getSolutionList().get(pos2);

    if (leaders.getComparator().compare(one, two) < 1) {
      bestGlobal = (DoubleSolution) one.copy();
    } else {
      bestGlobal = (DoubleSolution) two.copy();
    }

    return bestGlobal;
  }

  /**
   *
   * @param v
   * @param deltaMax
   * @param deltaMin
   * @param variableIndex
   * @return
   */
  private double velocityConstriction(
          double v, double[] deltaMax, double[] deltaMin, int variableIndex) {
    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v > dmax) {
      result = dmax;
    }

    if (v < dmin) {
      result = dmin;
    }

    return result;
  }

  /**
   *
   * @param c1
   * @param c2
   * @return Coefficient constriction
   */
  protected double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }
}
