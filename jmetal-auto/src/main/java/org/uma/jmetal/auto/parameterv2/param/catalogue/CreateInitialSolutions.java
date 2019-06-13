package org.uma.jmetal.auto.parameterv2.param.catalogue;

import org.uma.jmetal.auto.component.initialsolutionscreation.InitialSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.LatinHypercubeSamplingSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.auto.component.initialsolutionscreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.auto.parameterv2.param.CategoricalParameter;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.function.Function;

public class CreateInitialSolutions extends CategoricalParameter<String> {
  private String[] args ;

  public CreateInitialSolutions(String args[], List<String> validValues) {
    super(validValues) ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String>  parse() {
    value = on("--createInitialSolutions", args, Function.identity());
    return this ;
  }

  public InitialSolutionsCreation<DoubleSolution> getParameter(DoubleProblem problem, int populationSize) {
    switch (getValue()) {
      case "random":
        return new RandomSolutionsCreation(problem, populationSize);
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
