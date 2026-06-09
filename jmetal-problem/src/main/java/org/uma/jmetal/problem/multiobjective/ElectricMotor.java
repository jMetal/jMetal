package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Electric motor product platform design problem.
 *
 * <p>Ten motor designs (for different torque requirements) are optimized simultaneously using 8
 * design variables each. For each motor the mass is minimized and the efficiency is maximized,
 * subject to six constraints per motor (60 total).
 *
 * <p>Variables 0 and 2 of each motor (armature turns NARM and field turns NFIELD) are treated
 * as integers by rounding during evaluation.
 *
 * <p>Source: Simpson, T.W., J.R.A. Maier, and F. Mistree (2001). Product platform design: method
 * and application. Research in Engineering Design, 13:2-22.
 *
 * <p>Also used in: Zatarain Salazar, J., Hadka, D., Reed, P., Seada, H., and Deb, K. (2024).
 * Diagnostic benchmarking of many-objective evolutionary algorithms for real-world problems.
 * Engineering Optimization, 1-22. https://doi.org/10.1080/0305215X.2024.2381818
 */
@SuppressWarnings("serial")
public class ElectricMotor extends AbstractDoubleProblem {

  static final double[] REQUIRED_TORQUE = {0.05, 0.1, 0.125, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.5};
  static final double REQUIRED_POWER = 300.0;
  static final double MAXIMUM_MASS = 2.0;
  static final double MINIMUM_EFFICIENCY = 0.15;
  static final double MAXIMUM_INTENSITY = 5000.0;
  static final double TORQUE_EPSILON = 0.001;
  static final double POWER_EPSILON = 0.1;

  public ElectricMotor() {
    numberOfObjectives(20);
    numberOfConstraints(60);
    name("ElectricMotor");

    double[] lower = {100, 0.01, 1, 0.01, 0.01, 0.0005, 0.001, 0.1};
    double[] upper = {1500, 1.0, 500, 1.0, 0.1, 0.1, 0.1, 6.0};
    List<Double> lowerBounds = new ArrayList<>();
    List<Double> upperBounds = new ArrayList<>();
    for (int m = 0; m < 10; m++) {
      for (int i = 0; i < 8; i++) {
        lowerBounds.add(lower[i]);
        upperBounds.add(upper[i]);
      }
    }
    variableBounds(lowerBounds, upperBounds);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    for (int i = 0; i < 10; i++) {
      double[] input = new double[8];
      for (int j = 0; j < 8; j++) {
        input[j] = solution.variables().get(8 * i + j);
      }
      input[0] = Math.round(input[0]);  // NARM → integer
      input[2] = Math.round(input[2]);  // NFIELD → integer

      double[] output = evaluateMotor(input);

      // Minimize mass; maximize efficiency → negate to minimize
      solution.objectives()[2 * i] = output[1];
      solution.objectives()[2 * i + 1] = -output[2];

      // Constraints (jMetal: >= 0 feasible)
      // |TORQUE - required| <= TORQUE_EPSILON
      solution.constraints()[6 * i] = TORQUE_EPSILON - Math.abs(output[0] - REQUIRED_TORQUE[i]);
      // |POWER - required| <= POWER_EPSILON
      solution.constraints()[6 * i + 1] = POWER_EPSILON - Math.abs(output[4] - REQUIRED_POWER);
      // FEAS >= 1 (motor geometry feasibility: radius > stator thickness)
      solution.constraints()[6 * i + 2] = output[5] - 1.0;
      // SAT/2 <= MAXIMUM_INTENSITY (magnetizing intensity limit)
      solution.constraints()[6 * i + 3] = MAXIMUM_INTENSITY - output[3] / 2.0;
      // MASS <= MAXIMUM_MASS
      solution.constraints()[6 * i + 4] = MAXIMUM_MASS - output[1];
      // EFFIC > MINIMUM_EFFICIENCY
      solution.constraints()[6 * i + 5] = output[2] - MINIMUM_EFFICIENCY;
    }
    return solution;
  }

  /**
   * Evaluates a single motor design.
   *
   * @param input [NARM, AWA, NFIELD, AWF, RADIUS, THICK, LENGTH, CURRNT]
   * @return [TORQUE, MASS, EFFIC, SAT, POWER, FEAS, SPEED]
   */
  static double[] evaluateMotor(double[] input) {
    double NARM = input[0];
    double AWA = input[1];
    double NFIELD = input[2];
    double AWF = input[3];
    double RADIUS = input[4];
    double THICK = input[5];
    double LENGTH = input[6];
    double CURRNT = input[7];

    final double LGAP = 0.0007;
    final double VOLTAG = 115.0;
    final double RESIST = 1.69E-8;
    final double DCOPPR = 8960.0;
    final double DSTEEL = 7850.0;
    final double SATLEV = 220.0;
    final double SATLV2 = 1000.0;
    final double MUO = 4 * 3.14159E-7;

    THICK = THICK / 1000.0;
    RADIUS = RADIUS / 100.0;
    LENGTH = LENGTH / 100.0;
    AWF = AWF / 1000000.0;
    AWA = AWA / 1000000.0;

    double RDIAM = 2.0 * (RADIUS - THICK - LGAP);
    double FEAS = RADIUS / THICK;

    double RA = RESIST * NARM * (2.0 * LENGTH + 2.0 * RDIAM) / AWA;
    double RS = RESIST * 2.0 * NFIELD * (2.0 * LENGTH + 4.0 * (RADIUS - THICK)) / AWF;
    double LOSS = Math.pow(CURRNT, 2.0) * (RA + RS) + 2.0 * CURRNT;
    double POWER = VOLTAG * CURRNT - LOSS;
    double EFFIC = POWER / (VOLTAG * CURRNT);

    double KT = NARM / Math.PI;
    double LC = Math.PI * (2.0 * RADIUS + THICK) / 2.0;
    double SAT = 2.0 * NFIELD * CURRNT / (LC + RDIAM + 2.0 * LGAP);

    double MUR;
    if (SAT <= SATLEV) {
      MUR = -0.22791 * Math.pow(SAT, 2.0) + 52.411 * SAT + 3115.8;
    } else if (SAT >= SATLV2) {
      MUR = 1000.0;
    } else {
      MUR = 11633.5 - 1486.33 * Math.log(SAT);
    }

    double AS = THICK * LENGTH;
    double AR = RDIAM * LENGTH;
    double AA = RDIAM * LENGTH;

    double RRS = LC / (2.0 * MUR * MUO * AS);
    double RRR = RDIAM / (MUR * MUO * AR);
    double RRA = LGAP / (MUO * AA);
    double FFF = NFIELD * CURRNT;

    double RR = RRS + RRR + 2.0 * RRA;
    double PHI = FFF / RR;
    double TORQUE = KT * PHI * CURRNT;
    double SPEED = POWER / TORQUE;

    double MSTATOR = Math.PI * LENGTH * DSTEEL
        * (Math.pow(RADIUS, 2.0) - Math.pow(RADIUS - THICK, 2.0));
    double MROTOR = Math.PI * LENGTH * DSTEEL * Math.pow(RDIAM / 2.0, 2.0);
    double MWIND = ((2.0 * LENGTH + 2.0 * RDIAM) * AWA * NARM
        + (2.0 * LENGTH + 4.0 * (RADIUS - THICK)) * AWF * 2.0 * NFIELD) * DCOPPR;
    double MASS = MSTATOR + MROTOR + MWIND;

    return new double[]{TORQUE, MASS, EFFIC, SAT, POWER, FEAS, SPEED};
  }
}
