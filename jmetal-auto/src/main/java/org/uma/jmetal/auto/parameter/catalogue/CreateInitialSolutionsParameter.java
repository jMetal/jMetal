package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.LatinHypercubeSamplingSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.catalogue.common.solutionscreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.problem.binaryproblem.BinaryProblem;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.binarysolution.BinarySolution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class CreateInitialSolutionsParameter extends CategoricalParameter {

  public CreateInitialSolutionsParameter(String[] args, List<String> validValues) {
    this("createInitialSolutions", args, validValues);
  }

  public CreateInitialSolutionsParameter(String parameterName, String[] args,
      List<String> validValues) {
    super(parameterName, args, validValues);
  }

  public SolutionsCreation<? extends DoubleSolution> getParameter(DoubleProblem problem, int populationSize) {
    switch (getValue()) {
      case "random":
        return new RandomSolutionsCreation<>(problem, populationSize);
      case "scatterSearch":
        return new ScatterSearchSolutionsCreation(problem, populationSize, 4);
      case "latinHypercubeSampling":
        return new LatinHypercubeSamplingSolutionsCreation(problem, populationSize);
      default:
        throw new JMetalException(
            getValue() + " is not a valid initialization strategy");
    }
  }

  public SolutionsCreation<? extends BinarySolution> getParameter(BinaryProblem problem, int populationSize) {
    switch (getValue()) {
      case "random":
        return new RandomSolutionsCreation<>(problem, populationSize);
      default:
        throw new JMetalException(
            getValue() + " is not a valid initialization strategy");
    }
  }
}