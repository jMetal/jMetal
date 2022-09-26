package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.operator.crossover.impl.DifferentialEvolutionCrossover;

public class DifferentialEvolutionCrossoverParameter extends CategoricalParameter {
  public DifferentialEvolutionCrossoverParameter(String[] args) {
    this(args,
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

  public DifferentialEvolutionCrossoverParameter(String[] args, List<String> variants) {
    super(
        "differentialEvolutionCrossover", args, variants);
  }

  public DifferentialEvolutionCrossover getParameter() {
    DifferentialEvolutionCrossover result;
    Double cr = (Double) findGlobalParameter("CR").getValue();
    Double f = (Double) findGlobalParameter("F").getValue();

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
