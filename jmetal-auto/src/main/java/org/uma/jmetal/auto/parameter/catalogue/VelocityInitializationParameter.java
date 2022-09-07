package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.RandomVelocityInitialization;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class VelocityInitializationParameter extends CategoricalParameter {

  public VelocityInitializationParameter(String[] args, List<String> variationStrategies) {
    super("velocityInitialization", args, variationStrategies);
  }

  public VelocityInitialization getParameter() {
    VelocityInitialization result;

    if ("defaultVelocityInitialization".equals(getValue())) {
      result = new DefaultVelocityInitialization();
    } else if ("randomVelocityInitialization".equals(getValue())) {
      result = new RandomVelocityInitialization();
    } else {
      throw new JMetalException("Velocity initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "velocity initialization";
  }
}
