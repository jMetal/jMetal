package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class  PerturbationParameter extends CategoricalParameter {
  public PerturbationParameter(String[] args, List<String> perturbationStrategies) {
    super("perturbation", args, perturbationStrategies);
  }

  public Perturbation getParameter() {
    Perturbation result;

    if ("frequencySelectionMutationBasedPerturbation".equals(getValue())) {
      var mutationParameter = (MutationParameter) findSpecificParameter("mutation");
      var mutationOperator =
          mutationParameter.getDoubleSolutionParameter();

      var frequencyOfApplication = (int) findSpecificParameter(
          "frequencyOfApplicationOfMutationOperator").getValue();

      result =
          new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfApplication);
    } else {
      throw new JMetalException("Perturbation component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public @NotNull String getName() {
    return "perturbation";
  }
}
