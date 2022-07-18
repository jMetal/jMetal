package org.uma.jmetal.problem.singleobjective;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
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

    @NotNull List<Double> lowerLimit = new ArrayList<>() ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

      for (int i = 0; i < numberOfVariables; i++) {
          lowerLimit.add(-5.12);
          upperLimit.add(5.12);
      }

      setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(@NotNull DoubleSolution solution) {
      double sum = 0.0;
      for (Double v : solution.variables()) {
          double v1 = v;
          double v2 = v1 * v1;
          sum += v2;
      }

      solution.objectives()[0] = sum;

    return solution ;
  }
}

