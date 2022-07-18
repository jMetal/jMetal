package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.component.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class VelocityInitializationParameter extends CategoricalParameter {
  public VelocityInitializationParameter(String[] args, List<String> variationStrategies) {
    super("velocityInitialization", args, variationStrategies);
  }

  public @NotNull VelocityInitialization getParameter() {
    VelocityInitialization result;

    if ("defaultVelocityInitialization".equals(getValue())) {
      result = new DefaultVelocityInitialization();
    } else {
      throw new JMetalException("Velocity initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public @NotNull String getName() {
    return "velocity initialization";
  }
}
