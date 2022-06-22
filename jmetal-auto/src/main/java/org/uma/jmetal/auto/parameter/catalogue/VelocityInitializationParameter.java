package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.auto.component.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.auto.parameter.CategoricalParameter;

public class VelocityInitializationParameter extends CategoricalParameter {
  public VelocityInitializationParameter(String[] args, List<String> variationStrategies) {
    super("velocityInitialization", args, variationStrategies);
  }

  public VelocityInitialization getParameter() {
    VelocityInitialization result;

    switch (getValue()) {
      case "defaultVelocityInitialization":
        result = new DefaultVelocityInitialization() ;
        break;
      default:
        throw new RuntimeException("Velocity initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "velocity initialization";
  }
}
