package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.Arrays;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Problem BulkCarrierDesignProblem (RCM17)
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 *
 * Note: the reference MATLAB code negates all 9 constraints ({@code g = -g;}) after computing them
 * in "value <= threshold" form; that negation cancels out with the sign flip needed for jMetal's
 * convention (feasible iff constraint value &gt;= 0), so the constraints below are assigned directly
 * from the pre-negation MATLAB expressions.
 */
public class RCM17BulkCarrierDesignProblem extends AbstractDoubleProblem {

  public RCM17BulkCarrierDesignProblem() {
    numberOfObjectives(3);
    numberOfConstraints(9);
    name("BulkCarrierDesignProblem");

    List<Double> lowerLimit = Arrays.asList(150.0, 20.0, 13.0, 10.0, 14.0, 0.63);
    List<Double> upperLimit = Arrays.asList(274.32, 32.31, 25.0, 11.71, 18.0, 0.75);

    variableBounds(lowerLimit, upperLimit);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double l = solution.variables().get(0);
    double b = solution.variables().get(1);
    double d = solution.variables().get(2);
    double t = solution.variables().get(3);
    double vK = solution.variables().get(4);
    double cB = solution.variables().get(5);

    double a = 4977.06 * cB * cB - 8105.61 * cB + 4456.51;
    double bb = -10847.2 * cB * cB + 12817 * cB - 6960.32;
    double fN = 0.5144 / Math.sqrt(9.8065 * l);
    double p = (Math.pow(1.025 * l * b * t * cB, 2.0 / 3) * Math.pow(vK, 3)) / (a + bb * fN);

    double wS = 0.034 * Math.pow(l, 1.7) * Math.pow(b, 0.6) * Math.pow(d, 0.4) * Math.pow(cB, 0.5);
    double wO = Math.pow(l, 0.8) * Math.pow(b, 0.6) * Math.pow(d, 0.3) * Math.pow(cB, 0.1);
    double wM = 0.17 * Math.pow(p, 0.9);
    double ls = wS + wO + wM;

    double dWt = 1.025 * l * b * t * cB - ls;
    double fC = 0.19 * 24 * p / 1000 + 0.2;
    double dCwt = dWt - fC * ((5000 * vK) / 24 + 5) - 2 * Math.sqrt(dWt);
    double rTrp = 350 / ((5000 * vK) / 24 + 2 * (dCwt / 8000 + 0.5));
    double ac = dCwt * rTrp;
    double sD = 5000 * vK / 24;

    double cC = 0.2 * 1.3 * (2000 * Math.pow(wS, 0.85) + 3500 * wO + 2400 * Math.pow(p, 0.8));
    double cR = 40000 * Math.pow(dWt, 0.3);
    double cV = (1.05 * 100 * fC * sD + 6.3 * Math.pow(dWt, 0.8)) * rTrp;

    solution.objectives()[0] = (cC + cR + cV) / ac;
    solution.objectives()[1] = ls;
    solution.objectives()[2] = -ac;

    solution.constraints()[0] = l / b - 6;
    solution.constraints()[1] = 15 - l / d;
    solution.constraints()[2] = 19 - l / t;
    solution.constraints()[3] = 0.45 * Math.pow(dWt, 0.31) - t;
    solution.constraints()[4] = 0.7 * d + 0.7 - t;
    solution.constraints()[5] = 0.32 - fN;
    solution.constraints()[6] = 0.53 * t + ((0.085 * cB - 0.002) * b * b) / (t * cB)
        - (1 + 0.52 * d) - 0.07 * b;
    solution.constraints()[7] = dWt - 3000;
    solution.constraints()[8] = 500000 - dWt;

    return solution;
  }
}
