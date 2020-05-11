package org.uma.jmetal.auto.parameter.catalogue;

import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.initialsolutioncreation.InitialSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.LatinHypercubeSamplingSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.component.initialsolutioncreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

import java.util.List;
import java.util.function.Function;

public class CreateInitialSolutionsParameter extends CategoricalParameter<String> {
  private String[] args ;

  public CreateInitialSolutionsParameter(String args[], List<String> validValues) {
    super("createInitialSolutions", args, validValues) ;
    this.args = args ;
  }

  @Override
  public CategoricalParameter<String>  parse() {
    setValue(on("--createInitialSolutions", args, Function.identity()));
    return this ;
  }

  public InitialSolutionsCreation<DoubleSolution> getParameter(DoubleProblem problem, int populationSize) {
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
