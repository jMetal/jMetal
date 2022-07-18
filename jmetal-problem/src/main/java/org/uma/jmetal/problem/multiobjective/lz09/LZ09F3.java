package org.uma.jmetal.problem.multiobjective.lz09;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LZ09F3
 */
@SuppressWarnings("serial")
public class LZ09F3 extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F3 problem (30 variables and 2 objectives)
   **/
  public LZ09F3() {
    this(21, 1, 23);
  }

  /**
   * Creates a LZ09F3 problem instance
   */
  public LZ09F3(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    var numberOfVariables = 30;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F3");

    lz09 = new LZ09(numberOfVariables,
            getNumberOfObjectives(),
            ptype,
            dtype,
            ltype);

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public @NotNull DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    List<Double> y = new ArrayList<Double>(solution.objectives().length);

    for (var i = 0; i < getNumberOfVariables(); i++) {
      x.add(solution.variables().get(i));
      y.add(0.0);
    }

    lz09.objective(x, y);

    for (var i = 0; i < solution.objectives().length; i++) {
      solution.objectives()[i] = y.get(i);
    }
    return solution ;
  }
}


