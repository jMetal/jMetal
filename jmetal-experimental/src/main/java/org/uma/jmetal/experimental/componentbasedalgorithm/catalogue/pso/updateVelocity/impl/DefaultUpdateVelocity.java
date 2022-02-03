package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.updateVelocity.impl;

import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.updateVelocity.UpdateVelocity;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.List;

public class DefaultUpdateVelocity implements UpdateVelocity {
  private JMetalRandom randomGenerator;

  /**
   * Update de velocity
   * @param swarm
   * @param speed
   * @param r1Min
   * @param r1Max
   * @param r2Min
   * @param r2Max
   * @param c1Min
   * @param c1Max
   * @param c2Min
   * @param c2Max
   * @param wmax
   * @param wmin
   * @param bestGlobal
   * @param localBest
   * @param deltaMax
   * @param deltaMin
   * @param variableIndex
   * @param iterations
   * @param maxIterations
   * @return speed updated
   */
  @Override
  public double[][] update(
      List<DoubleSolution> swarm,
      double[][] speed,
      double r1Min,
      double r1Max,
      double r2Min,
      double r2Max,
      double c1Min,
      double c1Max,
      double c2Min,
      double c2Max,
      double wmax,
      double wmin,
      DoubleSolution bestGlobal,
      DoubleSolution[] localBest,
      double[] deltaMax,
      double[] deltaMin,
      int variableIndex,
      int iterations,
      int maxIterations) {
    for (int i = 0; i < swarm.size(); i++) {
      DoubleSolution particle = (DoubleSolution) swarm.get(i).copy();
      DoubleSolution bestParticle = (DoubleSolution) localBest[i].copy();

      double r1 = randomGenerator.nextDouble(r1Min, r1Max);
      double r2 = randomGenerator.nextDouble(r2Min, r2Max);
      double c1 = randomGenerator.nextDouble(c1Min, c1Max);
      double c2 = randomGenerator.nextDouble(c2Min, c2Max);

      for (int var = 0; var < particle.variables().size(); var++) {
        speed[i][var] =
            velocityConstriction(
                constrictionCoefficient(c1, c2)
                    * (inertiaWeight(iterations, maxIterations, wmax, wmin) * speed[i][var]
                        + c1
                            * r1
                            * (bestParticle.variables().get(var) - particle.variables().get(var))
                        + c2
                            * r2
                            * (bestGlobal.variables().get(var) - particle.variables().get(var))),
                deltaMax,
                deltaMin,
                var);
      }
    }

    return speed;
  }

  /**
   * Get the velocity constriction for velocity update
   * @param v
   * @param deltaMax
   * @param deltaMin
   * @param variableIndex
   * @return velocity Constriction
   */
  private double velocityConstriction(
      double v, double[] deltaMax, double[] deltaMin, int variableIndex) {
    double result;

    double dmax = deltaMax[variableIndex];
    double dmin = deltaMin[variableIndex];

    result = v;

    if (v >= dmax) {
      result = dmax;
    }
    if (v <= dmin) {
      result = dmin;
    }

    return result;
  }

  /**
   * Get the inertia weigth for velocity update
   * @param iter
   * @param miter
   * @param wma
   * @param wmin
   * @return Inertia Weigth
   */
  private double inertiaWeight(int iter, int miter, double wma, double wmin) {
    return wma;
  }

  /**
   * Get the constriction coefficiento for velocity update
   * @param c1
   * @param c2
   * @return Constriction Coefficient
   */
  private double constrictionCoefficient(double c1, double c2) {
    double rho = c1 + c2;
    if (rho <= 4) {
      return 1.0;
    } else {
      return 2 / (2 - rho - Math.sqrt(Math.pow(rho, 2.0) - 4.0 * rho));
    }
  }
}
