package org.uma.jmetal.problem.multiobjective.lz09;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.errorchecking.JMetalException;

/**
 * Class representing problem LZ09F9
 */
@SuppressWarnings("serial")
public class LZ09F9 extends AbstractDoubleProblem {

  private LZ09 lz09;

  /**
   * Creates a default LZ09F9 problem (30 variables and 2 objectives)
   **/
  public LZ09F9() {
    this(22, 1, 22);
  }

  /**
   * Creates a LZ09F9 problem instance
   */
  public LZ09F9(Integer ptype,
                Integer dtype,
                Integer ltype) throws JMetalException {
    var numberOfVariables = 30;
    setNumberOfObjectives(2);
    setNumberOfConstraints(0);
    setName("LZ09F9");

    lz09 = new LZ09(numberOfVariables,
            getNumberOfObjectives(),
            ptype,
            dtype,
            ltype);

    @NotNull List<Double> lowerLimit = new ArrayList<>(numberOfVariables) ;
    List<Double> upperLimit = new ArrayList<>(numberOfVariables) ;

    for (var i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  public DoubleSolution evaluate(DoubleSolution solution) {
    List<Double> x = new ArrayList<Double>(getNumberOfVariables());
    @NotNull List<Double> y = new ArrayList<Double>(solution.objectives().length);

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

