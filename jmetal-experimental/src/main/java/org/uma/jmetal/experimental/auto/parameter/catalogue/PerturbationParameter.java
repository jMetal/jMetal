package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.Variation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.ea.variation.impl.CrossoverAndMutationVariation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.operator.crossover.CrossoverOperator;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

public class PerturbationParameter extends CategoricalParameter {
  public PerturbationParameter(String[] args, List<String> perturbationStrategies) {
    super("perturbation", args, perturbationStrategies);
  }

  public Perturbation getParameter() {
    Perturbation result;

    switch (getValue()) {
      case "frequencySelectionMutationBasedPerturbation":
        MutationParameter mutationParameter = (MutationParameter) findSpecificParameter("mutation");
        MutationOperator<DoubleSolution> mutationOperator =
            mutationParameter.getParameter();

        int frequencyOfApplication = (int)findSpecificParameter("frequencyOfApplicationOfMutationOperator").getValue() ;

        result =
            new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfApplication) ;

        break;
      default:
        throw new RuntimeException("Perturbation component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "perturbation";
  }
}
