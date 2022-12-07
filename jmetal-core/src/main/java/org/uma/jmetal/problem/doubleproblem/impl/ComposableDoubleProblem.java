package org.uma.jmetal.problem.doubleproblem.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;
import org.uma.jmetal.util.bounds.Bounds;

/**
 * This class allows to define a continuous (double) problem dynamically, by adding the ranges of
 * every decision variable, the objective functions and the constraints. For example, the Schaffer
 * unconstrained problem (1 decision variable, two objectives) can be defined as follows:
 *
 * <p>ComposableDoubleProblem problem = new ComposableDoubleProblem() .setName("Schaffer")
 * .addVariable(-10, 10) .addVariable(-10, 10) .addFunction((x) -> x[0] * x[0]) .addFunction((x) ->
 * (x[0] - 2.0) * (x[0] - 2.0));
 *
 * <p>The Srinivas constrained problem can be defined in this way:
 *
 * <p>ComposableDoubleProblem problem; problem = new ComposableDoubleProblem() .setName("Srinivas")
 * .addVariable(-20.0, 20.0) .addVariable(-20.0, 20.0) .addFunction((x) -> 2.0 + (x[0] - 2.0) *
 * (x[0] - 2.0) + (x[1] - 1.0) * (x[1] - 1.0)) .addFunction((x) -> 9.0 * x[0] - (x[1] - 1.0) * (x[1]
 * - 1.0)) .addConstraint((x) -> 1.0 - (x[0] * x[0] + x[1] * x[1]) / 225.0) .addConstraint((x) ->
 * (3.0 * x[1] - x[0]) / 10.0 - 1.0) ;
 *
 * <p>Note that this class does not inherits from {@link AbstractDoubleProblem}.
 *
 * <p>As defined, this class would make possible to add more variables, objectives and constraints
 * to an existing problem on the fly.
 *
 * <p>This class does not intend to be a replacement of the existing of {@link
 * AbstractDoubleProblem}; it is merely an alternative way of defining a problem.
 */
public class ComposableDoubleProblem implements DoubleProblem {
  private List<Function<Double[], Double>> objectiveFunctions;
  private List<Function<Double[], Double>> constraints;
  private List<Bounds<Double>> bounds;
  private String name;

  public ComposableDoubleProblem() {
    objectiveFunctions = new ArrayList<>();
    constraints = new ArrayList<>();
    bounds = new ArrayList<>();

    name = "";
  }

  public ComposableDoubleProblem addFunction(Function<Double[], Double> objective) {
    objectiveFunctions.add(objective);
    return this;
  }

  public ComposableDoubleProblem addConstraint(Function<Double[], Double> constraint) {
    constraints.add(constraint);
    return this;
  }

  public ComposableDoubleProblem addVariable(double lowerBound, double upperBound) {
    bounds.add(Bounds.create(lowerBound, upperBound));
    return this;
  }

  public ComposableDoubleProblem name(String name) {
    this.name = name;

    return this;
  }

  @Override
  public int numberOfVariables() {
    return bounds.size();
  }

  @Override
  public int numberOfObjectives() {
    return objectiveFunctions.size();
  }

  @Override
  public int numberOfConstraints() {
    return constraints.size();
  }

  @Override
  public String name() {
    return name;
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(bounds, numberOfObjectives(), numberOfConstraints());
  }

  @Override
  public List<Bounds<Double>> variableBounds() {
    return bounds;
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    Double[] vars = solution.variables().toArray(new Double[numberOfVariables()]);

    IntStream.range(0, numberOfObjectives())
        .forEach(i -> solution.objectives()[i] =  objectiveFunctions.get(i).apply(vars));

    IntStream.range(0, numberOfConstraints())
        .forEach(i -> solution.constraints()[i] =  constraints.get(i).apply(vars));

    return solution ;
  }
}
