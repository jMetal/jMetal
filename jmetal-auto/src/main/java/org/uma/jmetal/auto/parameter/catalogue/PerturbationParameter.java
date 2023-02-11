package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.component.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
import org.uma.jmetal.operator.mutation.MutationOperator;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class  PerturbationParameter extends CategoricalParameter {
  public PerturbationParameter(List<String> perturbationStrategies) {
    super("perturbation", perturbationStrategies);
  }

  public Perturbation getParameter() {
    Perturbation result;

    if ("frequencySelectionMutationBasedPerturbation".equals(value())) {
      MutationParameter mutationParameter = (MutationParameter) findSpecificParameter("mutation");
      MutationOperator<DoubleSolution> mutationOperator =
          mutationParameter.getDoubleSolutionParameter();

      int frequencyOfApplication = (int) findSpecificParameter(
          "frequencyOfApplicationOfMutationOperator").value();

      result =
          new FrequencySelectionMutationBasedPerturbation(mutationOperator, frequencyOfApplication);
    } else {
      throw new JMetalException("Perturbation component unknown: " + value());
    }

    return result;
  }

  @Override
  public String name() {
    return "perturbation";
  }
}
