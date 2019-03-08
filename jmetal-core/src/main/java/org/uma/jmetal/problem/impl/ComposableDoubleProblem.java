package org.uma.jmetal.problem.impl;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.solution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;
import org.uma.jmetal.util.solutionattribute.impl.OverallConstraintViolation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * This class allows to define a continuous (double) problem dynamically, by adding the ranges of
 * every decision variable, the objective functions and the constraints.  For example, the Schaffer
 * unconstrained problem (1 decision variable, two objectives) can be defined as follows:
 *
 *     ComposableDoubleProblem problem = new ComposableDoubleProblem()
 *         .setName("Schaffer")
 *         .addVariable(-10, 10)
 *         .addVariable(-10, 10)
 *         .addFunction((x) -> x[0] * x[0])
 *         .addFunction((x) -> (x[0] - 2.0) * (x[0] - 2.0));
 *
 * The Srinivas constrained problem can be defined in this way:
 *
 *     ComposableDoubleProblem problem;
 *     problem = new ComposableDoubleProblem()
 *         .setName("Srinivas")
 *         .addVariable(-20.0, 20.0)
 *         .addVariable(-20.0, 20.0)
 *         .addFunction((x) ->  2.0 + (x[0] - 2.0) * (x[0] - 2.0) + (x[1] - 1.0) * (x[1] - 1.0))
 *         .addFunction((x) ->  9.0 * x[0] - (x[1] - 1.0) * (x[1] - 1.0))
 *         .addConstraint((x) -> 1.0 - (x[0] * x[0] + x[1] * x[1]) / 225.0)
 *         .addConstraint((x) -> (3.0 * x[1] - x[0]) / 10.0 - 1.0) ;
 *
 *  Note that this class does not inherits from {@link AbstractDoubleProblem}.
 *
 *  As defined, this class would make possible to add more variables, objectives and constraints
 *  to an existing problem on the fly.
 *
 *  This class does not intend to be a replacement of the existing of {@link AbstractDoubleProblem};
 *  it is merely an alternative way of defining a problem.
 */
@SuppressWarnings("serial")
public class ComposableDoubleProblem implements DoubleProblem {

  private List<Function<Double[], Double>> objectiveFunction ;
  private List<Function<Double[], Double>> constraints ;
  private List<Double> lowerBounds ;
  private List<Double> upperBounds ;
  private String name ;
  private OverallConstraintViolation<DoubleSolution> overallConstraintViolationDegree ;
  private NumberOfViolatedConstraints<DoubleSolution> numberOfViolatedConstraints ;

  public ComposableDoubleProblem() {
    objectiveFunction = new ArrayList<>() ;
    constraints = new ArrayList<>() ;
    lowerBounds = new ArrayList<>() ;
    upperBounds = new ArrayList<>() ;

    overallConstraintViolationDegree = new OverallConstraintViolation<DoubleSolution>() ;
    numberOfViolatedConstraints = new NumberOfViolatedConstraints<DoubleSolution>() ;
    name = "" ;
  }

  public ComposableDoubleProblem addFunction(Function<Double[], Double> objective) {
    objectiveFunction.add(objective) ;
    return this ;
  }

  public ComposableDoubleProblem addConstraint(Function<Double[], Double> constraint) {
    constraints.add(constraint) ;
    return this ;
  }

  public ComposableDoubleProblem addVariable(double lowerBound, double upperBound) {
    lowerBounds.add(lowerBound) ;
    upperBounds.add(upperBound) ;
    return this ;
  }

  public ComposableDoubleProblem setName(String name) {
    this.name = name ;

    return this ;
  }

  @Override
  public int getNumberOfVariables() {
    return lowerBounds.size() ;
  }

  @Override
  public int getNumberOfObjectives() {
    return objectiveFunction.size() ;
  }

  @Override
  public int getNumberOfConstraints() {
    return constraints.size() ;
  }

  @Override
  public String getName() {
    return name;
  }

  public Double getLowerBound(int index) {
    return lowerBounds.get(index);
  }

  public Double getUpperBound(int index) {
    return upperBounds.get(index);
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(this)  ;
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
