package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.InertiaWeightComputingStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.ConstantValueStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.LinearDecreasingStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.LinearIncreasingStrategy;
import org.uma.jmetal.component.catalogue.pso.inertiaweightcomputingstrategy.impl.RandomSelectedValueStrategy;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class InertiaWeightComputingParameter extends CategoricalParameter {
  public InertiaWeightComputingParameter(String[] args, List<String> mutationOperators) {
    super("inertiaWeightComputingStrategy", args, mutationOperators);
  }

  public InertiaWeightComputingStrategy getParameter() {
    InertiaWeightComputingStrategy result;

    switch (getValue()) {
      case "constantValue":
        Double weight = (Double) findSpecificParameter("weight").getValue();
        result = new ConstantValueStrategy(weight) ;
        break;
      case "randomSelectedValue":
        Double weightMin = (Double) findSpecificParameter("weightMin").getValue();
        Double weightMax = (Double) findSpecificParameter("weightMax").getValue();
        result = new RandomSelectedValueStrategy(weightMin, weightMax) ;
        break;
      case "linearDecreasingValue":
        weightMin = (Double) findSpecificParameter("weightMin").getValue();
        weightMax = (Double) findSpecificParameter("weightMax").getValue();
        int iterations = (Integer) getNonConfigurableParameter("maxIterations") ;
        int swarmSize = (Integer) getNonConfigurableParameter("swarmSize") ;
        result = new LinearDecreasingStrategy(weightMin, weightMax, iterations, swarmSize) ;
        break;
      case "linearIncreasingValue":
        weightMin = (Double) findSpecificParameter("weightMin").getValue();
        weightMax = (Double) findSpecificParameter("weightMax").getValue();
        iterations = (Integer) getNonConfigurableParameter("maxIterations");
        swarmSize = (Integer) getNonConfigurableParameter("swarmSize");
        result =new LinearIncreasingStrategy(weightMin, weightMax, iterations, swarmSize) ;
        break;
      default:
        throw new JMetalException("Inertia weight computing strategy does not exist: " + getName());
    }
    return result;
  }
}
