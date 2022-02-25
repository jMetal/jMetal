package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.VelocityUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl.ConstrainedVelocityUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityupdate.impl.DefaultVelocityUpdate;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;

public class VelocityUpdateParameter extends CategoricalParameter {

    public VelocityUpdateParameter(String[] args, List<String> validValues) {
    super("velocityUpdate", args, validValues);
  }

  public VelocityUpdate getParameter() {
    switch (getValue()) {
      case "defaultVelocityUpdate":
        double c1Min = (double) findGlobalParameter("c1Min").getValue() ;
        double c1Max = (double) findGlobalParameter("c1Max").getValue() ;
        double c2Min = (double) findGlobalParameter("c2Min").getValue() ;
        double c2Max = (double) findGlobalParameter("c2Max").getValue() ;
        double wMin = (double) findGlobalParameter("wMin").getValue() ;
        double wMax = (double) findGlobalParameter("wMax").getValue() ;
        return new DefaultVelocityUpdate(c1Min, c1Max, c2Min, c2Max, wMin, wMax);
      case "constrainedVelocityUpdate":
        // TODO
      default:
        throw new RuntimeException(getValue() + " is not a valid velocity update strategy");
    }
  }
}
