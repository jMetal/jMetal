package org.uma.jmetal.problem.multiobjective.cec2021;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * Base class for the RWMOP "Optimal Setting of Droop Controller for Minimization of Active Power
 * Loss in Islanded Microgrids" problems RCM47-RCM49. They share the same 6-bus islanded microgrid
 * (5 lines, line reactance scaled by the droop frequency variable {@code w}), the same 18 decision
 * variables (real/imaginary voltage at buses 2-6, droop frequency, slack voltage offset, and
 * active/reactive load at buses 4-6) and the same 12 equality constraints (current mismatch at
 * every bus), differing only in which loss/voltage-deviation quantities are combined into
 * objectives.
 *
 * Unlike RCM36-RCM46, the admittance matrix here depends on the decision variable {@code w} (droop
 * frequency), so it is rebuilt on every evaluation from the line data, following the {@code ybus}
 * local function of the reference MATLAB file. Complex arithmetic is implemented with parallel
 * real/imaginary {@code double[]} arrays since Java has no built-in complex number type.
 *
 * Code adapted from:
 * CEC2021_func Real world Multi-objective Constrained Optimization Test Suite
 * Abhishek Kumar (email: abhishek.kumar.eee13@iitbhu.ac.in, Indian Institute of Technology (BHU), Varanasi)
 */
abstract class AbstractRCMIslandedMicrogridDroopControl extends AbstractDoubleProblem {
  private static final double EQUALITY_CONSTRAINT_EPSILON = 1e-4;
  private static final int NUMBER_OF_BUSES = 6;

  // Line data (0-based from/to bus, resistance, reactance); ground admittance and tap ratio are
  // always 0 and 1 respectively in the reference data, so they are not modelled explicitly.
  private static final int[] LINE_FROM = {0, 0, 1, 1, 2};
  private static final int[] LINE_TO = {1, 3, 4, 2, 5};
  private static final double[] LINE_R = {
      0.26660053320106641, 0.18600037200074401, 0.12400024800049600, 0.093000186000372007,
      0.031000062000124000};
  private static final double[] LINE_X = {
      0.074329468658937317, 0.081809163618327255, 0.058435116870233741, 0.43078368156736313,
      0.011687023374046750};

  // Nominal active/reactive load, nonzero only at buses 1 and 3 (0-based).
  private static final double[] P_NOMINAL = {0.16138989835002382, 0, 0.21451873678268588, 0, 0, 0};
  private static final double[] Q_NOMINAL = {0.10680528035555388, 0, 0.15161777012574437, 0, 0, 0};

  protected AbstractRCMIslandedMicrogridDroopControl(int numberOfObjectives, String name) {
    numberOfObjectives(numberOfObjectives);
    numberOfConstraints(12);
    name(name);

    List<Double> lowerLimit = new ArrayList<>(18);
    List<Double> upperLimit = new ArrayList<>(18);
    for (int i = 0; i < 10; i++) {
      lowerLimit.add(-1.0);
      upperLimit.add(1.0);
    }
    lowerLimit.add(0.0);
    upperLimit.add(2.0); // w
    lowerLimit.add(0.0);
    upperLimit.add(2.0); // V(1) offset
    for (int i = 0; i < 6; i++) {
      lowerLimit.add(0.0);
      upperLimit.add(500.0); // Pc, Qc
    }
    variableBounds(lowerLimit, upperLimit);
  }

  /** Holds the result of a power-flow evaluation, shared by all RCM47-RCM49 subclasses. */
  protected static final class PowerFlowResult {
    final double[] psp;
    final double[] qsp;
    final double[] delIr;
    final double[] delIi;
    final double voltageDeviation;

    private PowerFlowResult(double[] psp, double[] qsp, double[] delIr, double[] delIi,
        double voltageDeviation) {
      this.psp = psp;
      this.qsp = qsp;
      this.delIr = delIr;
      this.delIi = delIi;
      this.voltageDeviation = voltageDeviation;
    }
  }

  protected PowerFlowResult computePowerFlow(DoubleSolution solution) {
    double[] vr = new double[NUMBER_OF_BUSES];
    double[] vi = new double[NUMBER_OF_BUSES];
    for (int k = 0; k < 5; k++) {
      vr[1 + k] = solution.variables().get(k);
      vi[1 + k] = solution.variables().get(5 + k);
    }
    double w = solution.variables().get(10);
    vr[0] = solution.variables().get(11) + 1e-5;
    vi[0] = 0.0;

    double[] pc = new double[NUMBER_OF_BUSES];
    double[] qc = new double[NUMBER_OF_BUSES];
    pc[3] = solution.variables().get(12);
    pc[4] = solution.variables().get(13);
    pc[5] = solution.variables().get(14);
    qc[3] = solution.variables().get(15);
    qc[4] = solution.variables().get(16);
    qc[5] = solution.variables().get(17);

    double[][] yr = new double[NUMBER_OF_BUSES][NUMBER_OF_BUSES];
    double[][] yi = new double[NUMBER_OF_BUSES][NUMBER_OF_BUSES];
    for (int k = 0; k < LINE_FROM.length; k++) {
      double r = LINE_R[k];
      double x = LINE_X[k] * w;
      double denom = r * r + x * x;
      double branchYr = r / denom;
      double branchYi = -x / denom;
      int fb = LINE_FROM[k];
      int tb = LINE_TO[k];
      yr[fb][tb] -= branchYr;
      yi[fb][tb] -= branchYi;
      yr[tb][fb] = yr[fb][tb];
      yi[tb][fb] = yi[fb][tb];
      yr[fb][fb] += branchYr;
      yi[fb][fb] += branchYi;
      yr[tb][tb] += branchYr;
      yi[tb][tb] += branchYi;
    }

    double[] ir = new double[NUMBER_OF_BUSES];
    double[] ii = new double[NUMBER_OF_BUSES];
    for (int i = 0; i < NUMBER_OF_BUSES; i++) {
      double sr = 0;
      double si = 0;
      for (int k = 0; k < NUMBER_OF_BUSES; k++) {
        sr += yr[i][k] * vr[k] - yi[i][k] * vi[k];
        si += yr[i][k] * vi[k] + yi[i][k] * vr[k];
      }
      ir[i] = sr;
      ii[i] = si;
    }

    double[] psp = new double[NUMBER_OF_BUSES];
    double[] qsp = new double[NUMBER_OF_BUSES];
    double[] voltageMagnitude = new double[NUMBER_OF_BUSES];
    for (int i = 0; i < NUMBER_OF_BUSES; i++) {
      voltageMagnitude[i] = Math.hypot(vr[i], vi[i]);
      // (|V|/1)^0 == 1 identically for this network's droop exponent data, so it is omitted.
      psp[i] = pc[i] * (1 - w) - P_NOMINAL[i];
      qsp[i] = qc[i] * (1 - voltageMagnitude[i]) - Q_NOMINAL[i];
    }

    double[] delIr = new double[NUMBER_OF_BUSES];
    double[] delIi = new double[NUMBER_OF_BUSES];
    double voltageDeviation = 0;
    for (int i = 0; i < NUMBER_OF_BUSES; i++) {
      double magSquared = vr[i] * vr[i] + vi[i] * vi[i];
      // spI = conj((Psp + j*Qsp) / V)
      double spIr = (psp[i] * vr[i] + qsp[i] * vi[i]) / magSquared;
      double spIi = (psp[i] * vi[i] - qsp[i] * vr[i]) / magSquared;
      delIr[i] = ir[i] - spIr;
      delIi[i] = ii[i] - spIi;
      voltageDeviation += Math.pow(1 - voltageMagnitude[i], 2);
    }

    return new PowerFlowResult(psp, qsp, delIr, delIi, voltageDeviation);
  }

  protected void setEqualityConstraints(DoubleSolution solution, PowerFlowResult result) {
    for (int k = 0; k < NUMBER_OF_BUSES; k++) {
      solution.constraints()[k] = EQUALITY_CONSTRAINT_EPSILON - Math.abs(result.delIr[k]);
      solution.constraints()[NUMBER_OF_BUSES + k] =
          EQUALITY_CONSTRAINT_EPSILON - Math.abs(result.delIi[k]);
    }
  }

  protected static double sum(double[] values) {
    double total = 0;
    for (double value : values) {
      total += value;
    }
    return total;
  }
}
