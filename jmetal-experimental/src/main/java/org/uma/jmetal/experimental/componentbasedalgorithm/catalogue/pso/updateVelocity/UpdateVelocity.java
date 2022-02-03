package org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.updateVelocity;

import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.GenericSolutionAttribute;

import java.util.List;

public interface UpdateVelocity {
  public double[][] update(
      List<DoubleSolution> swarm,
      double speed[][],
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
      double deltaMax[],
      double deltaMin[],
      int variableIndex,
      int iterations,
      int maxIterations);


}
