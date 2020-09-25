package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DifferentialEvolutionCrossoverParameter extends CategoricalParameter {
  public DifferentialEvolutionCrossoverParameter(String[] args) {
    super("differentialEvolutionCrossover", args, List.of("DE"));
  }

  public DifferentialEvolutionCrossover getParameter() {
    DifferentialEvolutionCrossover result;
    Double cr = (Double) findSpecificParameter("cr").getValue();
    Double f  = (Double) findSpecificParameter("f").getValue() ;

    result = new DifferentialEvolutionCrossover(cr, f, DifferentialEvolutionCrossover.DE_VARIANT.RAND_1_BIN) ;

    return result;
  }

  @Override
  public String getName() {
    return "differentialEvolutionCrossover";
  }
}
