package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.Perturbation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.perturbation.impl.FrequencySelectionMutationBasedPerturbation;
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
        var mutationParameter = (MutationParameter) findSpecificParameter("mutation");
        var mutationOperator =
            mutationParameter.getParameter();

        var frequencyOfApplication = (int)findSpecificParameter("frequencyOfApplicationOfMutationOperator").getValue() ;

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
