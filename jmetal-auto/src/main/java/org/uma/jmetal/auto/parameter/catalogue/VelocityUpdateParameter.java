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
  public VelocityUpdateParameter(List<String> validValues) {
    super("velocityUpdate", validValues);
  }

  public VelocityUpdate getParameter() {
    switch (value()) {
      case "defaultVelocityUpdate":
        double c1Min = (double) findGlobalParameter("c1Min").value();
        double c1Max = (double) findGlobalParameter("c1Max").value();
        double c2Min = (double) findGlobalParameter("c2Min").value();
        double c2Max = (double) findGlobalParameter("c2Max").value();
        return new DefaultVelocityUpdate(c1Min, c1Max, c2Min, c2Max);
      case "constrainedVelocityUpdate":
        c1Min = (double) findGlobalParameter("c1Min").value();
        c1Max = (double) findGlobalParameter("c1Max").value();
        c2Min = (double) findGlobalParameter("c2Min").value();
        c2Max = (double) findGlobalParameter("c2Max").value();
        DoubleProblem problem = (DoubleProblem) getNonConfigurableParameter("problem");
        return new ConstrainedVelocityUpdate(c1Min, c1Max, c2Min, c2Max, problem) {
        };
      case "SPSO2011VelocityUpdate":
        c1Min = (double) findGlobalParameter("c1Min").value();
        c1Max = (double) findGlobalParameter("c1Max").value();
        c2Min = (double) findGlobalParameter("c2Min").value();
        c2Max = (double) findGlobalParameter("c2Max").value();
        problem = (DoubleProblem) getNonConfigurableParameter("problem");
        return new SPS2011VelocityUpdate(c1Min, c1Max, c2Min, c2Max, problem) {
        };
      default:
        throw new JMetalException(value() + " is not a valid velocity update strategy");
    }
  }
}
