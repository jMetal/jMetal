package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.SolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.LatinHypercubeSamplingSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.RandomSolutionsCreation;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.common.solutionscreation.impl.ScatterSearchSolutionsCreation;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;


public class CreateSwarmInitialSolutions extends CategoricalParameter{
    public CreateSwarmInitialSolutions(String[] args, List<String> validValues) {
        super("swarmInitialization", args, validValues) ;
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
}
