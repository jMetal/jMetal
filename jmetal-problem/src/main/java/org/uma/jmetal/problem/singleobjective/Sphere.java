package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing a Sphere problem.
 */
@SuppressWarnings("serial")
public class Sphere extends AbstractDoubleProblem {
  /** Constructor */
  public Sphere() {
    this(10) ;
  }

  /** Constructor */
  public Sphere(Integer numberOfVariables) {
    setNumberOfObjectives(1);
    setName("Sphere");

    List<Double> lowerLimit = new ArrayList<>() ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    IntStream.range(0, numberOfVariables).forEach(i -> {
      lowerLimit.add(-5.12);
      upperLimit.add(5.12);
    });

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double sum = solution.variables().stream().mapToDouble(v -> v).map(v -> v * v).sum();

      solution.objectives()[0] = sum;

    return solution ;
  }
}

