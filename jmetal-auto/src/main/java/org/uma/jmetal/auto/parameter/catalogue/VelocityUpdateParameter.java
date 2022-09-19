package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.DefaultVelocityUpdate;
import org.uma.jmetal.component.catalogue.pso.velocityupdate.impl.SPS2011VelocityUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class VelocityUpdateParameter extends CategoricalParameter {
  public VelocityUpdateParameter(String[] args, List<String> validValues) {
    super("velocityUpdate", args, validValues);
  }

  public VelocityUpdate getParameter() {
    switch (getValue()) {
      case "defaultVelocityUpdate":
        double c1Min = (double) findGlobalParameter("c1Min").getValue();
        double c1Max = (double) findGlobalParameter("c1Max").getValue();
        double c2Min = (double) findGlobalParameter("c2Min").getValue();
        double c2Max = (double) findGlobalParameter("c2Max").getValue();
        return new DefaultVelocityUpdate(c1Min, c1Max, c2Min, c2Max);
      case "constrainedVelocityUpdate":
        c1Min = (double) findGlobalParameter("c1Min").getValue();
        c1Max = (double) findGlobalParameter("c1Max").getValue();
        c2Min = (double) findGlobalParameter("c2Min").getValue();
        c2Max = (double) findGlobalParameter("c2Max").getValue();
        DoubleProblem problem = (DoubleProblem) getNonConfigurableParameter("problem");
        return new ConstrainedVelocityUpdate(c1Min, c1Max, c2Min, c2Max, problem) {
        };
      case "SPSO2011VelocityUpdate":
        c1Min = (double) findGlobalParameter("c1Min").getValue();
        c1Max = (double) findGlobalParameter("c1Max").getValue();
        c2Min = (double) findGlobalParameter("c2Min").getValue();
        c2Max = (double) findGlobalParameter("c2Max").getValue();
        problem = (DoubleProblem) getNonConfigurableParameter("problem");
        return new SPS2011VelocityUpdate(c1Min, c1Max, c2Min, c2Max, problem) {
        };
      default:
        throw new JMetalException(getValue() + " is not a valid velocity update strategy");
    }
  }
}
