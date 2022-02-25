package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.Variation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.VelocityInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.velocityinitialization.impl.DefaultVelocityInitialization;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

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
