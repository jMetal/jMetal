package org.uma.jmetal.problem.doubleproblem.impl;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.uma.jmetal.problem.doubleproblem.DoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.solution.doublesolution.impl.DefaultDoubleSolution;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

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
@SuppressWarnings("serial")
public class ComposableDoubleProblem implements DoubleProblem {

  private List<Function<Double[], Double>> objectiveFunctions;
  private List<Function<Double[], Double>> constraints;
  private List<Pair<Double, Double>> bounds;
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
    bounds.add(new ImmutablePair<>(lowerBound, upperBound));
    return this;
  }

  public ComposableDoubleProblem setName(String name) {
    this.name = name;

    return this;
  }

  @Override
  public int getNumberOfVariables() {
    return bounds.size();
  }

  @Override
  public int getNumberOfObjectives() {
    return objectiveFunctions.size();
  }

  @Override
  public int getNumberOfConstraints() {
    return constraints.size();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Double getLowerBound(int index) {
    return bounds.get(index).getLeft();
  }

  @Override
  public Double getUpperBound(int index) {
    return bounds.get(index).getRight();
  }

  @Override
  public DoubleSolution createSolution() {
    return new DefaultDoubleSolution(bounds, getNumberOfObjectives(), getNumberOfConstraints());
  }

  @Override
  public List<Pair<Double, Double>> getBounds() {
    return bounds;
  }

  @Override
  public void evaluate(DoubleSolution solution) {
    Double[] vars = solution.getVariables().toArray(new Double[getNumberOfVariables()]);

    IntStream.range(0, getNumberOfObjectives())
        .forEach(i -> solution.setObjective(i, objectiveFunctions.get(i).apply(vars)));

    IntStream.range(0, getNumberOfConstraints())
        .forEach(i -> solution.setConstraint(i, constraints.get(i).apply(vars)));
  }
}
