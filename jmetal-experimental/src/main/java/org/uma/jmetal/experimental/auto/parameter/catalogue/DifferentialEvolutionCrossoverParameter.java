package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.auto.parameter.Parameter;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class DifferentialEvolutionCrossoverParameter extends CategoricalParameter {
  public DifferentialEvolutionCrossoverParameter(String[] args) {
    super(
        "differentialEvolutionCrossover",
        args,
        List.of(
            "RAND_1_BIN",
            "RAND_1_EXP",
            "RAND_2_EXP",
            "BEST_1_BIN",
            "BEST_1_EXP",
            "BEST_2_BIN",
            "BEST_2_EXP",
            "RAND_TO_BEST_1_BIN",
            "RAND_TO_BEST_1_EXP",
            "CURRENT_TO_RAND_1_BIN",
            "CURRENT_TO_RAND_1_EXP"));
  }

  public DifferentialEvolutionCrossover getParameter() {
    DifferentialEvolutionCrossover result;
    Double cr = (Double) findSpecificParameter("cr").getValue();
    Double f = (Double) findSpecificParameter("f").getValue();

    String variant = getValue() ;

    result =
        new DifferentialEvolutionCrossover(
            cr, f, DifferentialEvolutionCrossover.getVariantFromString(variant));

    return result;
  }

  @Override
  public String getName() {
    return "differentialEvolutionCrossover";
  }
}
