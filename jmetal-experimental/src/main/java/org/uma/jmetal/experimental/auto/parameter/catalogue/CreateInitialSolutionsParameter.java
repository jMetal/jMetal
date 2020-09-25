package org.uma.jmetal.experimental.auto.parameter.catalogue;

import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.LatinHypercubeSamplingSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.solutionscreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.function.Function;

public class CreateInitialSolutionsParameter extends CategoricalParameter {

  public CreateInitialSolutionsParameter(String[] args, List<String> validValues) {
    super("createInitialSolutions", args, validValues) ;
  }

  public SolutionsCreation<DoubleSolution> getParameter(DoubleProblem problem, int populationSize) {
    switch (getValue()) {
      case "random":
        return new RandomSolutionsCreation<>(problem, populationSize);
      case "scatterSearch":
        return new ScatterSearchSolutionsCreation(problem, populationSize, 4);
      case "latinHypercubeSampling":
        return new LatinHypercubeSamplingSolutionsCreation(problem, populationSize);
      default:
        throw new RuntimeException(
            getValue() + " is not a valid initialization strategy");
    }
  }

  @Override
  public String getName() {
    return "createInitialSolutions";
  }
}
