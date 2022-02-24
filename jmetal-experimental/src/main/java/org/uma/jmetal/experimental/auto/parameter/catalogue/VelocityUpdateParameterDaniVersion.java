package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl.DefaultVelocityUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

import java.util.List;

public class VelocityUpdateParameterDaniVersion extends CategoricalParameter {

    public VelocityUpdateParameterDaniVersion(String[] args, List<String> validValues) {
    super("velocityUpdate", args, validValues);
  }

  public VelocityUpdate getParameter(
      DoubleProblem problem,
      double c1Min,
      double c1Max,
      double c2Min,
      double c2Max,
      double wMin,
      double wMax,
      double r1Min,
      double r1Max,
      double r2Min,
      double r2Max) {
    switch (getValue()) {
      case "defaultVelocityUpdate":
        return new DefaultVelocityUpdate(c1Min, c1Max, c2Min, c2Max, wMin, wMax);
      case "constrainedVelocityUpdate":
        return new ConstrainedVelocityUpdate(
            r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, wMin, wMax, problem);
      default:
        throw new RuntimeException(getValue() + " is not a valid initialization strategy");
    }
  }
}
