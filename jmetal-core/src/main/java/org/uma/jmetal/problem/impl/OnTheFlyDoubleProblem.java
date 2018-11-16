package org.uma.jmetal.problem.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

public class OnTheFlyDoubleProblem extends AbstractDoubleProblem {

  private List<Function<Double[], Double>> objectiveFunction ;
  private List<Function<Double[], Double>> constraints ;
  public OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree ;
  public NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints ;

  public OnTheFlyDoubleProblem(int numberOfVariables, int numberOfObjectives) {
    this(numberOfVariables, numberOfObjectives, 0) ;
  }

  public OnTheFlyDoubleProblem(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
    setNumberOfVariables(numberOfVariables);
    setNumberOfObjectives(numberOfObjectives);
    setNumberOfConstraints(numberOfConstraints);
    objectiveFunction = new ArrayList<>(numberOfObjectives) ;
    constraints = new ArrayList<>(numberOfConstraints) ;

    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>() ;
  }

  public void addFunction(Function<Double[], Double> objective) {
    objectiveFunction.add(objective) ;
  }

  public void addConstraint(Function<Double[], Double> constraint) {
    constraints.add(constraint) ;
  }

  public void setLowerBounds(List<Double> lowerBounds) {
    setLowerLimit(lowerBounds);
  }

  public void setUpperBounds(List<Double> upperBounds) {
    setUpperLimit(upperBounds);
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    Double[] vars = solution.getVariables().toArray(new Double[getNumberOfVariables()]);

    IntStream.range(0, getNumberOfObjectives())
        .forEach(i -> solution.setObjective(i, objectiveFunction.get(i).apply(vars)));

    if (getNumberOfConstraints() > 0) {
      double overallConstraintViolation = 0.0 ;
      int violatedConstraints = 0 ;
      for (int i = 0; i < getNumberOfConstraints(); i++) {
        double violationDegree = constraints.get(i).apply(vars) ;
        if (violationDegree < 0) {
          overallConstraintViolation += violationDegree ;
          violatedConstraints ++ ;
        }
      }
      overallConstraintViolationDegree.setAttribute(solution, overallConstraintViolation);
      numberOfViolatedConstraints.setAttribute(solution, violatedConstraints);
    }
  }
}
