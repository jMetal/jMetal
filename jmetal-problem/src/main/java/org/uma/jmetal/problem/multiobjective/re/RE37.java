package org.uma.jmetal.problem.multiobjective.re;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Class representing problem RE37. Source: Ryoji Tanabe and Hisao Ishibuchi, An easy-to-use
 * real-world multi-objective optimization problem suite, Applied Soft Computing, Vol. 89, pp.
 * 106078 (2020). DOI: https://doi.org/10.1016/j.asoc.2020.106078
 *
 * @author Antonio J. Nebro
 */
public class RE37 extends AbstractDoubleProblem {

  /** Constructor */
  public RE37() {
    int numberOfVariables = 4 ;
    setNumberOfObjectives(3);
    setNumberOfConstraints(0);
    setName("RE37");

    List<Double> lowerLimit = new ArrayList<>(numberOfVariables);
    @NotNull List<Double> upperLimit = new ArrayList<>(numberOfVariables);

    for (int i = 0; i < numberOfVariables; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(1.0);
    }

    setVariableBounds(lowerLimit, upperLimit);
  }

  /** Evaluate() method */
  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double xAlpha = solution.variables().get(0);
    double xHA = solution.variables().get(1);
    double xOA = solution.variables().get(2);
    double xOPTT = solution.variables().get(3);

    solution.objectives()[0] =
        0.692
            + (0.477 * xAlpha)
            - (0.687 * xHA)
            - (0.080 * xOA)
            - (0.0650 * xOPTT)
            - (0.167 * xAlpha * xAlpha)
            - (0.0129 * xHA * xAlpha)
            + (0.0796 * xHA * xHA)
            - (0.0634 * xOA * xAlpha)
            - (0.0257 * xOA * xHA)
            + (0.0877 * xOA * xOA)
            - (0.0521 * xOPTT * xAlpha)
            + (0.00156 * xOPTT * xHA)
            + (0.00198 * xOPTT * xOA)
            + (0.0184 * xOPTT * xOPTT);
    solution.objectives()[1] =
        0.153
            - (0.322 * xAlpha)
            + (0.396 * xHA)
            + (0.424 * xOA)
            + (0.0226 * xOPTT)
            + (0.175 * xAlpha * xAlpha)
            + (0.0185 * xHA * xAlpha)
            - (0.0701 * xHA * xHA)
            - (0.251 * xOA * xAlpha)
            + (0.179 * xOA * xHA)
            + (0.0150 * xOA * xOA)
            + (0.0134 * xOPTT * xAlpha)
            + (0.0296 * xOPTT * xHA)
            + (0.0752 * xOPTT * xOA)
            + (0.0192 * xOPTT * xOPTT);
    solution.objectives()[2] =
        0.370
            - (0.205 * xAlpha)
            + (0.0307 * xHA)
            + (0.108 * xOA)
            + (1.019 * xOPTT)
            - (0.135 * xAlpha * xAlpha)
            + (0.0141 * xHA * xAlpha)
            + (0.0998 * xHA * xHA)
            + (0.208 * xOA * xAlpha)
            - (0.0301 * xOA * xHA)
            - (0.226 * xOA * xOA)
            + (0.353 * xOPTT * xAlpha)
            - (0.0497 * xOPTT * xOA)
            - (0.423 * xOPTT * xOPTT)
            + (0.202 * xHA * xAlpha * xAlpha)
            - (0.281 * xOA * xAlpha * xAlpha)
            - (0.342 * xHA * xHA * xAlpha)
            - (0.245 * xHA * xHA * xOA)
            + (0.281 * xOA * xOA * xHA)
            - (0.184 * xOPTT * xOPTT * xAlpha)
            - (0.281 * xHA * xAlpha * xOA);

    return solution;
  }
}
