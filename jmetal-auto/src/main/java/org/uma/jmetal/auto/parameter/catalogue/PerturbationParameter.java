package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import component.catalogue.pso.perturbation.Perturbation;
import component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
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
      MutationParameter mutationParameter = (MutationParameter) findSpecificParameter("mutation");
      MutationOperator<DoubleSolution> mutationOperator =
          mutationParameter.getParameter();

      int frequencyOfApplication = (int) findSpecificParameter(
          "frequencyOfApplicationOfMutationOperator").getValue();

      result =
          new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfApplication);
    } else {
      throw new JMetalException("Perturbation component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "perturbation";
  }
}
