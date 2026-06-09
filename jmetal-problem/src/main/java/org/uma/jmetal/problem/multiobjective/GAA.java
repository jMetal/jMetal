package org.uma.jmetal.problem.multiobjective;

import java.util.ArrayList;
import java.util.List;
import org.uma.jmetal.problem.doubleproblem.impl.AbstractDoubleProblem;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

/**
 * General Aviation Aircraft (GAA) problem using a Response Surface Model (RSM).
 *
 * <p>Three aircraft designs (2-seat, 4-seat, 6-seat) are optimized simultaneously using 9 design
 * variables each. Six objectives minimize worst-case performance across designs; three objectives
 * maximize best-case range, lift-to-drag ratio, and cruise speed; and one objective minimizes a
 * product-family penalty (PFPF) for design commonality.
 *
 * <p>Source: Simpson, T.W. et al. (1996). Conceptual design of a family of products through the
 * use of the robust concept exploration method. 6th AIAA/USAF/NASA/ISSMO Symposium on
 * Multidisciplinary Analysis and Optimization, vol. 2, pp. 1535-1545.
 *
 * <p>Also used in: Zatarain Salazar, J., Hadka, D., Reed, P., Seada, H., and Deb, K. (2024).
 * Diagnostic benchmarking of many-objective evolutionary algorithms for real-world problems.
 * Engineering Optimization, 1-22. https://doi.org/10.1080/0305215X.2024.2381818
 */
@SuppressWarnings("serial")
public class GAA extends AbstractDoubleProblem {

  public GAA() {
    numberOfObjectives(10);
    numberOfConstraints(1);
    name("GAA");

    double[] lower = {0.24, 7, 0, 5.5, 19, 85, 14, 3, 0.46};
    double[] upper = {0.48, 11, 6, 5.968, 25, 110, 20, 3.75, 1};
    List<Double> lowerBounds = new ArrayList<>();
    List<Double> upperBounds = new ArrayList<>();
    for (int d = 0; d < 3; d++) {
      for (int i = 0; i < 9; i++) {
        lowerBounds.add(lower[i]);
        upperBounds.add(upper[i]);
      }
    }
    variableBounds(lowerBounds, upperBounds);
  }

  @Override
  public DoubleSolution evaluate(DoubleSolution solution) {
    double[] v = new double[27];
    for (int i = 0; i < 27; i++) {
      v[i] = solution.variables().get(i);
    }

    // Scale decision variables to [-1, 1] coded form
    double CSPD2 = (v[0] - 0.36) / 0.12;
    double AR2 = (v[1] - 9) / 2;
    double SWEEP2 = (v[2] - 3) / 3;
    double DPROP2 = (v[3] - 5.734) / 0.234;
    double WINGLD2 = (v[4] - 22) / 3;
    double AF2 = (v[5] - 97.5) / 12.5;
    double SEATW2 = (v[6] - 17) / 3;
    double ELODT2 = (v[7] - 3.375) / 0.375;
    double TAPER2 = (v[8] - 0.73) / 0.27;

    double CSPD4 = (v[9] - 0.36) / 0.12;
    double AR4 = (v[10] - 9) / 2;
    double SWEEP4 = (v[11] - 3) / 3;
    double DPROP4 = (v[12] - 5.734) / 0.234;
    double WINGLD4 = (v[13] - 22) / 3;
    double AF4 = (v[14] - 97.5) / 12.5;
    double SEATW4 = (v[15] - 17) / 3;
    double ELODT4 = (v[16] - 3.375) / 0.375;
    double TAPER4 = (v[17] - 0.73) / 0.27;

    double CSPD6 = (v[18] - 0.36) / 0.12;
    double AR6 = (v[19] - 9) / 2;
    double SWEEP6 = (v[20] - 3) / 3;
    double DPROP6 = (v[21] - 5.734) / 0.234;
    double WINGLD6 = (v[22] - 22) / 3;
    double AF6 = (v[23] - 97.5) / 12.5;
    double SEATW6 = (v[24] - 17) / 3;
    double ELODT6 = (v[25] - 3.375) / 0.375;
    double TAPER6 = (v[26] - 0.73) / 0.27;

    // Product Family Penalty Function (PFPF)
    double CSPDM = (CSPD2 + CSPD4 + CSPD6) / 3.0;
    double ARM = (AR2 + AR4 + AR6) / 3.0;
    double SWEEPM = (SWEEP2 + SWEEP4 + SWEEP6) / 3.0;
    double DPROPM = (DPROP2 + DPROP4 + DPROP6) / 3.0;
    double WINGLDM = (WINGLD2 + WINGLD4 + WINGLD6) / 3.0;
    double AFM = (AF2 + AF4 + AF6) / 3.0;
    double SEATWM = (SEATW2 + SEATW4 + SEATW6) / 3.0;
    double ELODTM = (ELODT2 + ELODT4 + ELODT6) / 3.0;
    double TAPERM = (TAPER2 + TAPER4 + TAPER6) / 3.0;

    double PFPF = Math.sqrt(
        sq(CSPD2 - CSPDM) + sq(CSPD4 - CSPDM) + sq(CSPD6 - CSPDM)
            + sq(AR2 - ARM) + sq(AR4 - ARM) + sq(AR6 - ARM)
            + sq(SWEEP2 - SWEEPM) + sq(SWEEP4 - SWEEPM) + sq(SWEEP6 - SWEEPM)
            + sq(DPROP2 - DPROPM) + sq(DPROP4 - DPROPM) + sq(DPROP6 - DPROPM)
            + sq(WINGLD2 - WINGLDM) + sq(WINGLD4 - WINGLDM) + sq(WINGLD6 - WINGLDM)
            + sq(AF2 - AFM) + sq(AF4 - AFM) + sq(AF6 - AFM)
            + sq(SEATW2 - SEATWM) + sq(SEATW4 - SEATWM) + sq(SEATW6 - SEATWM)
            + sq(ELODT2 - ELODTM) + sq(ELODT4 - ELODTM) + sq(ELODT6 - ELODTM)
            + sq(TAPER2 - TAPERM) + sq(TAPER4 - TAPERM) + sq(TAPER6 - TAPERM));

    double[] rsm2 = computeDesign2(CSPD2, AR2, SWEEP2, DPROP2, WINGLD2, AF2, SEATW2, ELODT2,
        TAPER2);
    double[] rsm4 = computeDesign4(CSPD4, AR4, SWEEP4, DPROP4, WINGLD4, AF4, SEATW4, ELODT4,
        TAPER4);
    double[] rsm6 = computeDesign6(CSPD6, AR6, SWEEP6, DPROP6, WINGLD6, AF6, SEATW6, ELODT6,
        TAPER6);

    double NOISE2 = rsm2[0], WEMP2 = rsm2[1], DOC2 = rsm2[2], ROUGH2 = rsm2[3];
    double WFUEL2 = rsm2[4], PURCH2 = rsm2[5], RANGE2 = rsm2[6];
    double LDMAX2 = rsm2[7], VCMAX2 = rsm2[8];

    double NOISE4 = rsm4[0], WEMP4 = rsm4[1], DOC4 = rsm4[2], ROUGH4 = rsm4[3];
    double WFUEL4 = rsm4[4], PURCH4 = rsm4[5], RANGE4 = rsm4[6];
    double LDMAX4 = rsm4[7], VCMAX4 = rsm4[8];

    double NOISE6 = rsm6[0], WEMP6 = rsm6[1], DOC6 = rsm6[2], ROUGH6 = rsm6[3];
    double WFUEL6 = rsm6[4], PURCH6 = rsm6[5], RANGE6 = rsm6[6];
    double LDMAX6 = rsm6[7], VCMAX6 = rsm6[8];

    // Aggregate: maximize worst-case noise/weight/cost/roughness/fuel/price (minimize max)
    solution.objectives()[0] = Math.max(NOISE2, Math.max(NOISE4, NOISE6));
    solution.objectives()[1] = Math.max(WEMP2, Math.max(WEMP4, WEMP6));
    solution.objectives()[2] = Math.max(DOC2, Math.max(DOC4, DOC6));
    solution.objectives()[3] = Math.max(ROUGH2, Math.max(ROUGH4, ROUGH6));
    solution.objectives()[4] = Math.max(WFUEL2, Math.max(WFUEL4, WFUEL6));
    solution.objectives()[5] = Math.max(PURCH2, Math.max(PURCH4, PURCH6));
    // Maximize range/L-D ratio/cruise speed → negate min value to minimize
    solution.objectives()[6] = -Math.min(RANGE2, Math.min(RANGE4, RANGE6));
    solution.objectives()[7] = -Math.min(LDMAX2, Math.min(LDMAX4, LDMAX6));
    solution.objectives()[8] = -Math.min(VCMAX2, Math.min(VCMAX4, VCMAX6));
    solution.objectives()[9] = PFPF;

    // Constraint: total violation <= 0 (feasible). jMetal convention: >= 0 is feasible.
    double cvTotal = computeCvTotal(
        NOISE2, WEMP2, DOC2, ROUGH2, WFUEL2, RANGE2,
        NOISE4, WEMP4, DOC4, ROUGH4, WFUEL4, RANGE4,
        NOISE6, WEMP6, DOC6, ROUGH6, WFUEL6, RANGE6);
    solution.constraints()[0] = -cvTotal;

    return solution;
  }

  private static double[] computeDesign2(
      double CSPD2, double AR2, double SWEEP2, double DPROP2, double WINGLD2,
      double AF2, double SEATW2, double ELODT2, double TAPER2) {

    double NOISE2 = 74.099998 - 0.0004 * CSPD2 - 0.0156 * AR2 + 0.0003 * SWEEP2
        + 0.9684 * DPROP2 + 0.0316 * WINGLD2 - 0.0053 * AF2 - 0.0015 * SEATW2
        - 0.0002 * ELODT2 + 0.0007 * TAPER2
        - 0.0001 * CSPD2 * DPROP2 - 0.0001 * CSPD2 * WINGLD2 - 0.0001 * CSPD2 * AF2
        + 0.0001 * CSPD2 * ELODT2
        - 0.0012 * AR2 * DPROP2 - 0.0014 * AR2 * WINGLD2 - 0.0002 * AR2 * AF2
        + 0.0003 * AR2 * ELODT2 + 0.0001 * AR2 * TAPER2
        - 0.0003 * DPROP2 * WINGLD2 + 0.0058 * DPROP2 * AF2 - 0.0001 * DPROP2 * SEATW2
        + 0.0002 * DPROP2 * ELODT2 - 0.0001 * DPROP2 * TAPER2
        + 0.0002 * WINGLD2 * AF2 - 0.0003 * WINGLD2 * SEATW2 - 0.0002 * WINGLD2 * ELODT2
        + 0.0001 * WINGLD2 * TAPER2
        - 0.0001 * AF2 * SEATW2 - 0.0001 * SEATW2 * ELODT2 - 0.0001 * SEATW2 * TAPER2
        + 0.0008 * sq(CSPD2) + 0.0016 * sq(AR2) + 0.0011 * sq(SWEEP2) + 0.1105 * sq(DPROP2)
        + 0.0004 * sq(WINGLD2) - 0.0019 * sq(AF2) + 0.0009 * sq(SEATW2)
        + 0.001 * sq(ELODT2) + 0.0007 * sq(TAPER2);

    double WEMP2 = 1917 + 5.979 * CSPD2 + 35.130001 * AR2 - 0.7119 * SWEEP2
        + 11.11 * DPROP2 - 32.290001 * WINGLD2 + 5.739 * AF2 + 48.110001 * SEATW2
        + 0.3376 * ELODT2 + 15.28 * TAPER2
        + 1.244 * CSPD2 * AR2 - 0.1315 * CSPD2 * SWEEP2 + 1.129 * CSPD2 * DPROP2
        - 2.393 * CSPD2 * WINGLD2 + 0.3954 * CSPD2 * AF2 - 0.4978 * CSPD2 * SEATW2
        - 0.3882 * CSPD2 * ELODT2 + 0.5742 * CSPD2 * TAPER2
        - 0.2236 * AR2 * SWEEP2 - 0.0739 * AR2 * DPROP2 - 3.805 * AR2 * WINGLD2
        - 0.0164 * AR2 * AF2 - 0.0923 * AR2 * SEATW2 - 0.9326 * AR2 * ELODT2
        + 3.135 * AR2 * TAPER2
        - 0.0385 * SWEEP2 * DPROP2 + 0.4376 * SWEEP2 * WINGLD2 + 0.0259 * SWEEP2 * AF2
        + 0.4009 * SWEEP2 * SEATW2 + 0.3002 * SWEEP2 * ELODT2 + 0.7036 * SWEEP2 * TAPER2
        - 0.2083 * DPROP2 * WINGLD2 + 1.165 * DPROP2 * AF2 - 0.2119 * DPROP2 * SEATW2
        - 0.1934 * DPROP2 * ELODT2 + 0.1462 * DPROP2 * TAPER2
        - 0.0644 * WINGLD2 * AF2 + 3.194 * WINGLD2 * SEATW2 + 2.672 * WINGLD2 * ELODT2
        - 0.4407 * WINGLD2 * TAPER2
        - 0.057 * AF2 * SEATW2 - 0.0529 * AF2 * ELODT2 + 0.0341 * AF2 * TAPER2
        + 4.88 * SEATW2 * ELODT2 + 1.349 * SEATW2 * TAPER2 + 0.8836 * ELODT2 * TAPER2
        + 0.958 * sq(CSPD2) - 1.812 * sq(AR2) + 1.173 * sq(SWEEP2) + 0.753 * sq(DPROP2)
        + 3.638 * sq(WINGLD2) + 0.133 * sq(AF2) + 5.323 * sq(SEATW2)
        + 1.478 * sq(ELODT2) - 0.192 * sq(TAPER2);

    double DOC2 = 83.17 + 12.53 * CSPD2 - 0.0477 * AR2 - 0.0215 * SWEEP2
        + 3.597 * DPROP2 - 0.7367 * WINGLD2 + 0.7481 * AF2 + 0.733 * SEATW2
        - 0.2029 * ELODT2 + 0.0393 * TAPER2
        + 0.6526 * CSPD2 * AR2 + 0.0481 * CSPD2 * SWEEP2 + 1.208 * CSPD2 * DPROP2
        + 0.6802 * CSPD2 * WINGLD2 + 0.0992 * CSPD2 * AF2 - 0.7074 * CSPD2 * SEATW2
        + 0.2768 * CSPD2 * ELODT2 + 0.0109 * CSPD2 * TAPER2
        + 0.0031 * AR2 * SWEEP2 + 0.2146 * AR2 * DPROP2 - 0.0721 * AR2 * WINGLD2
        - 0.2445 * AR2 * AF2 - 0.0172 * AR2 * SEATW2 + 0.0127 * AR2 * ELODT2
        + 0.0087 * AR2 * TAPER2
        + 0.0169 * SWEEP2 * DPROP2 + 0.0151 * SWEEP2 * WINGLD2 - 0.0063 * SWEEP2 * AF2
        - 0.0001 * SWEEP2 * SEATW2 - 0.0042 * SWEEP2 * ELODT2 - 0.0059 * SWEEP2 * TAPER2
        + 0.0789 * DPROP2 * WINGLD2 + 0.676 * DPROP2 * AF2 - 0.1912 * DPROP2 * SEATW2
        + 0.0519 * DPROP2 * ELODT2 - 0.0265 * DPROP2 * TAPER2
        + 0.0136 * WINGLD2 * AF2 + 0.0804 * WINGLD2 * SEATW2 + 0.0577 * WINGLD2 * ELODT2
        + 0.017 * WINGLD2 * TAPER2
        - 0.0617 * AF2 * SEATW2 + 0.0058 * AF2 * ELODT2 - 0.0178 * AF2 * TAPER2
        + 0.0901 * SEATW2 * ELODT2 + 0.0047 * SEATW2 * TAPER2 - 0.003 * ELODT2 * TAPER2
        - 11.37 * sq(CSPD2) - 0.2836 * sq(AR2) - 0.3149 * sq(SWEEP2) + 5.337 * sq(DPROP2)
        - 0.3711 * sq(WINGLD2) - 0.071 * sq(AF2) - 0.2177 * sq(SEATW2)
        - 0.2354 * sq(ELODT2) - 0.238 * sq(TAPER2);

    double ROUGH2 = 2.197 - 0.0002 * CSPD2 + 0.1541 * AR2 - 0.0012 * SWEEP2
        + 0.0222 * DPROP2 - 0.1611 * WINGLD2 - 0.0012 * AF2 - 0.0628 * SEATW2
        - 0.011 * ELODT2 + 0.0068 * TAPER2
        + 0.0006 * CSPD2 * AR2 + 0.0001 * CSPD2 * SWEEP2 + 0.001 * CSPD2 * WINGLD2
        + 0.0001 * CSPD2 * SEATW2 - 0.0002 * CSPD2 * ELODT2 - 0.0006 * CSPD2 * TAPER2
        - 0.0001 * AR2 * SWEEP2 + 0.0006 * AR2 * DPROP2 - 0.0113 * AR2 * WINGLD2
        - 0.0001 * AR2 * AF2 - 0.0045 * AR2 * SEATW2 - 0.0017 * AR2 * ELODT2
        - 0.0011 * AR2 * TAPER2
        - 0.0002 * SWEEP2 * DPROP2 - 0.0004 * SWEEP2 * WINGLD2 - 0.0006 * SWEEP2 * SEATW2
        - 0.0003 * SWEEP2 * ELODT2 - 0.0001 * SWEEP2 * TAPER2
        - 0.0051 * DPROP2 * WINGLD2 - 0.0038 * DPROP2 * AF2 + 0.0022 * DPROP2 * SEATW2
        - 0.0012 * DPROP2 * ELODT2 - 0.0002 * DPROP2 * TAPER2
        - 0.001 * WINGLD2 * AF2 - 0.0036 * WINGLD2 * SEATW2 - 0.0025 * WINGLD2 * ELODT2
        - 0.0025 * WINGLD2 * TAPER2
        + 0.0008 * AF2 * SEATW2 - 0.0003 * AF2 * ELODT2 - 0.0001 * AF2 * TAPER2
        - 0.001 * SEATW2 * ELODT2 - 0.0007 * SEATW2 * TAPER2
        + 0.0012 * sq(CSPD2) - 0.0273 * sq(AR2) - 0.0048 * sq(SWEEP2) + 0.0033 * sq(DPROP2)
        + 0.0062 * sq(WINGLD2) + 0.0021 * sq(AF2) - 0.0016 * sq(SEATW2)
        - 0.0011 * sq(ELODT2) - 0.0048 * sq(TAPER2);

    double WFUEL2 = 416.399994 - 6.093 * CSPD2 - 31.91 * AR2 + 0.7968 * SWEEP2
        - 19.17 * DPROP2 + 34.189999 * WINGLD2 - 7.57 * AF2 - 49.610001 * SEATW2
        + 0.2331 * ELODT2 - 15.33 * TAPER2
        - 1.201 * CSPD2 * AR2 + 0.1735 * CSPD2 * SWEEP2 - 1.247 * CSPD2 * DPROP2
        + 1.703 * CSPD2 * WINGLD2 - 0.4588 * CSPD2 * AF2 + 0.1585 * CSPD2 * SEATW2
        + 0.6156 * CSPD2 * ELODT2 - 0.528 * CSPD2 * TAPER2
        + 0.2215 * AR2 * SWEEP2 - 0.4976 * AR2 * DPROP2 + 4.058 * AR2 * WINGLD2
        - 0.108 * AR2 * AF2 + 0.2679 * AR2 * SEATW2 + 0.8514 * AR2 * ELODT2
        - 3.182 * AR2 * TAPER2
        + 0.0359 * SWEEP2 * DPROP2 - 0.482 * SWEEP2 * WINGLD2 - 0.0207 * SWEEP2 * AF2
        - 0.3878 * SWEEP2 * SEATW2 - 0.3249 * SWEEP2 * ELODT2 - 0.715 * SWEEP2 * TAPER2
        + 0.3374 * DPROP2 * WINGLD2 - 2.403 * DPROP2 * AF2 + 0.4519 * DPROP2 * SEATW2
        + 0.1352 * DPROP2 * ELODT2 - 0.123 * DPROP2 * TAPER2
        + 0.2498 * WINGLD2 * AF2 - 2.896 * WINGLD2 * SEATW2 - 3.016 * WINGLD2 * ELODT2
        + 0.3662 * WINGLD2 * TAPER2
        - 0.114 * AF2 * SEATW2 + 0.0571 * AF2 * ELODT2 - 0.0222 * AF2 * TAPER2
        - 4.689 * SEATW2 * ELODT2 - 1.339 * SEATW2 * TAPER2 - 0.9311 * ELODT2 * TAPER2
        - 0.7538 * sq(CSPD2) + 1.13 * sq(AR2) - 1.078 * sq(SWEEP2) - 5.989 * sq(DPROP2)
        - 3.043 * sq(WINGLD2) + 0.0627 * sq(AF2) - 4.958 * sq(SEATW2)
        - 1.41 * sq(ELODT2) + 0.3532 * sq(TAPER2);

    double PURCH2 = 43280 + 133.1 * CSPD2 + 780.400024 * AR2 - 1.501 * SWEEP2
        + 494 * DPROP2 + 191.2 * AF2 + 786 * SEATW2 + 102 * ELODT2 + 333.9 * TAPER2
        + 25.95 * CSPD2 * AR2 - 2.442 * CSPD2 * SWEEP2 + 28.280001 * CSPD2 * DPROP2
        - 50.509998 * CSPD2 * WINGLD2 + 11.55 * CSPD2 * AF2 - 6.188 * CSPD2 * SEATW2
        - 4.326 * CSPD2 * ELODT2 + 13.78 * CSPD2 * TAPER2
        - 3.335 * AR2 * SWEEP2 + 0.4272 * AR2 * DPROP2 - 69.110001 * AR2 * WINGLD2
        - 331.8 * WINGLD2 - 0.8834 * AR2 * AF2 + 7.221 * AR2 * SEATW2
        - 20.389999 * AR2 * ELODT2 + 69.580002 * AR2 * TAPER2
        - 1.107 * SWEEP2 * DPROP2 + 7.078 * SWEEP2 * WINGLD2 + 0.0705 * SWEEP2 * AF2
        + 5.359 * SWEEP2 * SEATW2 + 4.936 * SWEEP2 * ELODT2 + 14.77 * SWEEP2 * TAPER2
        - 21.82 * DPROP2 * WINGLD2 + 22.17 * DPROP2 * AF2 + 14.2 * DPROP2 * SEATW2
        - 7.537 * DPROP2 * ELODT2 + 5.813 * DPROP2 * TAPER2
        - 6.771 * WINGLD2 * AF2 + 27.99 * WINGLD2 * SEATW2 + 47.080002 * WINGLD2 * ELODT2
        - 9.888 * WINGLD2 * TAPER2
        + 3.135 * AF2 * SEATW2 - 2.658 * AF2 * ELODT2 + 1.033 * AF2 * TAPER2
        + 82.230003 * SEATW2 * ELODT2 + 27.969999 * SEATW2 * TAPER2
        + 16.110001 * ELODT2 * TAPER2
        + 10.51 * sq(CSPD2) - 49.189999 * sq(AR2) + 26.51 * sq(SWEEP2)
        + 35.310001 * sq(DPROP2) + 33.560001 * sq(WINGLD2) + 10.66 * sq(AF2)
        + 74.309998 * sq(SEATW2) + 8.461 * sq(ELODT2) - 0.7392 * sq(TAPER2);

    double RANGE2 = 1971 - 6.807 * CSPD2 - 76.230003 * AR2 + 2.885 * SWEEP2
        - 363.799988 * DPROP2 - 383.100006 * WINGLD2 - 68.099998 * AF2
        - 67.480003 * SEATW2 + 18.09 * ELODT2 - 165.699997 * TAPER2
        + 0.084 * CSPD2 * AR2 + 0.2793 * CSPD2 * SWEEP2 + 1.674 * CSPD2 * DPROP2
        + 3.342 * CSPD2 * WINGLD2 + 0.1543 * CSPD2 * AF2 - 0.4629 * CSPD2 * SEATW2
        + 0.8574 * CSPD2 * ELODT2 + 2.537 * CSPD2 * TAPER2
        + 0.0098 * AR2 * SWEEP2 - 23.23 * AR2 * DPROP2 - 25.799999 * AR2 * WINGLD2
        - 1.787 * AR2 * AF2 + 9.432 * AR2 * SEATW2 - 1.912 * AR2 * ELODT2
        - 40.200001 * AR2 * TAPER2
        - 1.479 * SWEEP2 * DPROP2 - 1.311 * SWEEP2 * WINGLD2 - 0.2168 * SWEEP2 * AF2
        - 0.7168 * SWEEP2 * SEATW2 - 0.584 * SWEEP2 * ELODT2 - 0.8652 * SWEEP2 * TAPER2
        + 121.099998 * DPROP2 * WINGLD2 - 26.709999 * DPROP2 * AF2
        + 25.280001 * DPROP2 * SEATW2 - 10.74 * DPROP2 * ELODT2 + 37.080002 * DPROP2 * TAPER2
        + 21.290001 * WINGLD2 * AF2 + 19.299999 * WINGLD2 * SEATW2
        - 4.318 * WINGLD2 * ELODT2 - 20.360001 * WINGLD2 * TAPER2
        + 3.611 * AF2 * SEATW2 - 1.678 * AF2 * ELODT2 + 7.541 * AF2 * TAPER2
        - 7.232 * SEATW2 * ELODT2 + 16.860001 * SEATW2 * TAPER2 - 3.006 * ELODT2 * TAPER2
        - 11.61 * sq(CSPD2) + 4.892 * sq(AR2) - 11.61 * sq(SWEEP2) - 84.110001 * sq(DPROP2)
        + 65.389999 * sq(WINGLD2) - 7.108 * sq(AF2) - 15.61 * sq(SEATW2)
        - 13.11 * sq(ELODT2) + 8.892 * sq(TAPER2);

    double LDMAX2 = 17.780001 + 0.4845 * CSPD2 + 1.625 * AR2 + 0.0267 * SWEEP2
        - 0.0153 * DPROP2 - 0.5289 * WINGLD2 - 0.007 * AF2 - 0.4965 * SEATW2
        + 0.2108 * ELODT2 + 0.0302 * TAPER2
        + 0.0598 * CSPD2 * AR2 + 0.0019 * CSPD2 * SWEEP2 - 0.0085 * CSPD2 * DPROP2
        - 0.0146 * CSPD2 * WINGLD2 - 0.0018 * CSPD2 * AF2 - 0.0014 * CSPD2 * SEATW2
        + 0.0104 * CSPD2 * ELODT2 + 0.0044 * CSPD2 * TAPER2
        + 0.0053 * AR2 * SWEEP2 - 0.0024 * AR2 * DPROP2 - 0.0612 * AR2 * WINGLD2
        - 0.001 * AR2 * AF2 - 0.0517 * AR2 * SEATW2 + 0.0128 * AR2 * ELODT2
        + 0.0043 * AR2 * TAPER2
        - 0.0001 * SWEEP2 * DPROP2 - 0.0054 * SWEEP2 * WINGLD2 - 0.0072 * SWEEP2 * SEATW2
        - 0.0039 * SWEEP2 * ELODT2
        + 0.0002 * DPROP2 * WINGLD2 - 0.0012 * DPROP2 * AF2 + 0.0016 * DPROP2 * SEATW2
        - 0.0001 * DPROP2 * ELODT2 + 0.0007 * DPROP2 * TAPER2
        - 0.0001 * WINGLD2 * AF2 - 0.0727 * WINGLD2 * SEATW2 - 0.0256 * WINGLD2 * ELODT2
        - 0.0033 * WINGLD2 * TAPER2
        + 0.0008 * AF2 * SEATW2 - 0.0003 * AF2 * ELODT2 + 0.0003 * AF2 * TAPER2
        - 0.0525 * SEATW2 * ELODT2 - 0.0071 * SEATW2 * TAPER2 - 0.0059 * ELODT2 * TAPER2
        - 0.0897 * sq(CSPD2) - 0.1488 * sq(AR2) - 0.0116 * sq(SWEEP2) - 0.0009 * sq(DPROP2)
        + 0.0157 * sq(WINGLD2) - 0.001 * sq(AF2) - 0.0577 * sq(SEATW2)
        - 0.0305 * sq(ELODT2) - 0.0009 * sq(TAPER2);

    double VCMAX2 = 200.4 - 0.3799 * CSPD2 + 0.8236 * AR2 + 0.2168 * SWEEP2
        + 1.74 * DPROP2 + 5.589 * WINGLD2 - 0.1683 * AF2 - 3.522 * SEATW2
        + 1.559 * ELODT2 + 0.2442 * TAPER2
        - 0.0215 * CSPD2 * AR2 + 0.0054 * CSPD2 * SWEEP2 - 0.0465 * CSPD2 * DPROP2
        - 0.0144 * CSPD2 * WINGLD2 - 0.0069 * CSPD2 * AF2 + 0.0578 * CSPD2 * SEATW2
        + 0.0557 * CSPD2 * ELODT2 + 0.0221 * CSPD2 * TAPER2
        + 0.0175 * AR2 * SWEEP2 - 0.0942 * AR2 * DPROP2 + 0.1402 * AR2 * WINGLD2
        - 0.0079 * AR2 * AF2 + 0.0147 * AR2 * SEATW2 - 0.071 * AR2 * ELODT2
        - 0.0104 * AR2 * TAPER2
        - 0.0067 * SWEEP2 * DPROP2 - 0.0306 * SWEEP2 * WINGLD2 - 0.0078 * SWEEP2 * AF2
        - 0.0534 * SWEEP2 * SEATW2 - 0.0224 * SWEEP2 * ELODT2 + 0.0026 * SWEEP2 * TAPER2
        - 0.2771 * DPROP2 * WINGLD2 - 0.3476 * DPROP2 * AF2 + 0.2093 * DPROP2 * SEATW2
        - 0.0864 * DPROP2 * ELODT2 - 0.0051 * DPROP2 * TAPER2
        - 0.0948 * WINGLD2 * AF2 - 0.7081 * WINGLD2 * SEATW2 - 0.1219 * WINGLD2 * ELODT2
        - 0.016 * WINGLD2 * TAPER2
        + 0.0555 * AF2 * SEATW2 - 0.0342 * AF2 * ELODT2 - 0.0049 * AF2 * TAPER2
        - 0.3658 * SEATW2 * ELODT2 - 0.046 * SEATW2 * TAPER2 - 0.0504 * ELODT2 * TAPER2
        - 0.1452 * sq(CSPD2) - 0.2937 * sq(AR2) + 0.0303 * sq(SWEEP2)
        + 0.1378 * sq(DPROP2) - 0.6522 * sq(WINGLD2) + 0.1378 * sq(AF2)
        - 0.6102 * sq(SEATW2) - 0.3722 * sq(ELODT2) + 0.0303 * sq(TAPER2);

    return new double[]{NOISE2, WEMP2, DOC2, ROUGH2, WFUEL2, PURCH2, RANGE2, LDMAX2, VCMAX2};
  }

  private static double[] computeDesign4(
      double CSPD4, double AR4, double SWEEP4, double DPROP4, double WINGLD4,
      double AF4, double SEATW4, double ELODT4, double TAPER4) {

    double NOISE4 = 74.099998 - 0.0005 * CSPD4 - 0.0158 * AR4 + 0.0003 * SWEEP4
        + 0.9682 * DPROP4 + 0.0316 * WINGLD4 - 0.0053 * AF4 - 0.0014 * SEATW4
        - 0.0003 * ELODT4 + 0.0008 * TAPER4
        - 0.0001 * CSPD4 * DPROP4 - 0.0001 * CSPD4 * WINGLD4 - 0.0001 * CSPD4 * AF4
        + 0.0001 * CSPD4 * ELODT4
        - 0.0012 * AR4 * DPROP4 - 0.0014 * AR4 * WINGLD4 - 0.0002 * AR4 * AF4
        + 0.0002 * AR4 * ELODT4 + 0.0002 * AR4 * TAPER4
        - 0.0003 * DPROP4 * WINGLD4 + 0.0057 * DPROP4 * AF4 - 0.0001 * DPROP4 * SEATW4
        + 0.0001 * DPROP4 * ELODT4
        + 0.0002 * WINGLD4 * AF4 - 0.0002 * WINGLD4 * SEATW4 - 0.0001 * WINGLD4 * ELODT4
        + 0.0001 * WINGLD4 * TAPER4
        - 0.0001 * AF4 * SEATW4 - 0.0001 * SEATW4 * ELODT4
        + 0.0008 * sq(CSPD4) + 0.0016 * sq(AR4) + 0.0011 * sq(SWEEP4)
        + 0.1104 * sq(DPROP4) + 0.0003 * sq(WINGLD4) - 0.0019 * sq(AF4)
        + 0.001 * sq(SEATW4) + 0.001 * sq(ELODT4) + 0.0007 * sq(TAPER4);

    double WEMP4 = 1947 + 6.338 * CSPD4 + 33.869999 * AR4 - 0.448 * SWEEP4
        + 11 * DPROP4 - 30.85 * WINGLD4 + 5.723 * AF4 + 53.220001 * SEATW4
        + 1.896 * ELODT4 + 15.26 * TAPER4
        + 1.963 * CSPD4 * AR4 - 0.1599 * CSPD4 * SWEEP4 + 1.073 * CSPD4 * DPROP4
        - 1.699 * CSPD4 * WINGLD4 + 0.462 * CSPD4 * AF4 - 0.9528 * CSPD4 * SEATW4
        - 0.9851 * CSPD4 * ELODT4 + 0.5956 * CSPD4 * TAPER4
        + 0.0065 * AR4 * SWEEP4 + 0.0874 * AR4 * DPROP4 - 3.447 * AR4 * WINGLD4
        + 0.1024 * AR4 * AF4 - 0.1814 * AR4 * SEATW4 + 0.7878 * AR4 * ELODT4
        + 1.592 * AR4 * TAPER4
        + 0.0288 * SWEEP4 * DPROP4 + 0.3498 * SWEEP4 * WINGLD4 + 0.034 * SWEEP4 * AF4
        + 0.3134 * SWEEP4 * SEATW4 + 0.2289 * SWEEP4 * ELODT4 + 0.5603 * SWEEP4 * TAPER4
        - 0.1862 * DPROP4 * WINGLD4 + 1.061 * DPROP4 * AF4 - 0.0774 * DPROP4 * SEATW4
        - 0.2335 * DPROP4 * ELODT4 + 0.1385 * DPROP4 * TAPER4
        - 0.0914 * WINGLD4 * AF4 + 1.932 * WINGLD4 * SEATW4 + 1.853 * WINGLD4 * ELODT4
        - 0.8019 * WINGLD4 * TAPER4
        - 0.0754 * AF4 * SEATW4 - 0.1306 * AF4 * ELODT4 + 0.1086 * AF4 * TAPER4
        + 4.81 * SEATW4 * ELODT4 + 1.309 * SEATW4 * TAPER4 + 1.265 * ELODT4 * TAPER4
        + 0.4046 * sq(CSPD4) + 1.065 * sq(AR4) + 0.7346 * sq(SWEEP4)
        + 0.4896 * sq(DPROP4) + 5.815 * sq(WINGLD4) - 0.1304 * sq(AF4)
        + 3.595 * sq(SEATW4) + 0.6296 * sq(ELODT4) + 1.26 * sq(TAPER4);

    double DOC4 = 83.150002 + 12.02 * CSPD4 - 0.072 * AR4 - 0.0126 * SWEEP4
        + 3.428 * DPROP4 - 0.704 * WINGLD4 + 0.7248 * AF4 + 0.7224 * SEATW4
        - 0.1421 * ELODT4 + 0.0407 * TAPER4
        + 0.613 * CSPD4 * AR4 + 0.046 * CSPD4 * SWEEP4 + 1.155 * CSPD4 * DPROP4
        + 0.7144 * CSPD4 * WINGLD4 + 0.0944 * CSPD4 * AF4 - 0.8399 * CSPD4 * SEATW4
        + 0.2251 * CSPD4 * ELODT4 + 0.0082 * CSPD4 * TAPER4
        + 0.0082 * AR4 * SWEEP4 + 0.1826 * AR4 * DPROP4 - 0.0606 * AR4 * WINGLD4
        - 0.2352 * AR4 * AF4 - 0.0486 * AR4 * SEATW4 + 0.0162 * AR4 * ELODT4
        + 0.0211 * AR4 * TAPER4
        + 0.0199 * SWEEP4 * DPROP4 + 0.0088 * SWEEP4 * WINGLD4 - 0.0152 * SWEEP4 * AF4
        + 0.0082 * SWEEP4 * SEATW4 - 0.0123 * SWEEP4 * ELODT4 - 0.0142 * SWEEP4 * TAPER4
        + 0.1207 * DPROP4 * WINGLD4 + 0.6576 * DPROP4 * AF4 - 0.2728 * DPROP4 * SEATW4
        + 0.0435 * DPROP4 * ELODT4 - 0.0326 * DPROP4 * TAPER4
        + 0.0196 * WINGLD4 * AF4 + 0.0952 * WINGLD4 * SEATW4 + 0.0351 * WINGLD4 * ELODT4
        + 0.0091 * WINGLD4 * TAPER4
        - 0.0628 * AF4 * SEATW4 - 0.0201 * AF4 * ELODT4 - 0.0244 * AF4 * TAPER4
        + 0.0651 * SEATW4 * ELODT4 + 0.0048 * SEATW4 * TAPER4 - 0.0042 * ELODT4 * TAPER4
        - 10.95 * sq(CSPD4) - 0.2401 * sq(AR4) - 0.2203 * sq(SWEEP4)
        + 5.223 * sq(DPROP4) - 0.2669 * sq(WINGLD4) - 0.0884 * sq(AF4)
        - 0.2169 * sq(SEATW4) - 0.306 * sq(ELODT4) - 0.2231 * sq(TAPER4);

    double ROUGH4 = 2.191 - 0.0001 * CSPD4 + 0.1584 * AR4 - 0.0017 * SWEEP4
        + 0.0238 * DPROP4 - 0.1632 * WINGLD4 - 0.0008 * AF4 - 0.0666 * SEATW4
        - 0.0142 * ELODT4 + 0.0069 * TAPER4
        - 0.0002 * CSPD4 * AR4 + 0.0001 * CSPD4 * SWEEP4 + 0.0001 * CSPD4 * WINGLD4
        - 0.0002 * CSPD4 * AF4 + 0.0008 * CSPD4 * SEATW4 + 0.0005 * CSPD4 * ELODT4
        - 0.0007 * CSPD4 * TAPER4
        - 0.0003 * AR4 * SWEEP4 + 0.0006 * AR4 * DPROP4 - 0.012 * AR4 * WINGLD4
        - 0.0003 * AR4 * AF4 - 0.0051 * AR4 * SEATW4 - 0.0037 * AR4 * ELODT4
        + 0.0007 * AR4 * TAPER4
        - 0.0002 * SWEEP4 * DPROP4 - 0.0003 * SWEEP4 * WINGLD4 - 0.0001 * SWEEP4 * AF4
        - 0.0004 * SWEEP4 * SEATW4 - 0.0002 * SWEEP4 * ELODT4 - 0.0001 * SWEEP4 * TAPER4
        - 0.005 * DPROP4 * WINGLD4 - 0.0037 * DPROP4 * AF4 + 0.0022 * DPROP4 * SEATW4
        - 0.0008 * DPROP4 * ELODT4 - 0.0003 * DPROP4 * TAPER4
        - 0.0008 * WINGLD4 * AF4 - 0.0018 * WINGLD4 * SEATW4 - 0.0012 * WINGLD4 * ELODT4
        - 0.0021 * WINGLD4 * TAPER4
        + 0.0009 * AF4 * SEATW4 - 0.0001 * AF4 * ELODT4 - 0.0002 * AF4 * TAPER4
        - 0.0011 * SEATW4 * ELODT4 - 0.0012 * SEATW4 * TAPER4 - 0.0008 * ELODT4 * TAPER4
        - 0.0001 * sq(CSPD4) - 0.0302 * sq(AR4) - 0.0015 * sq(SWEEP4)
        + 0.0041 * sq(DPROP4) + 0.005 * sq(WINGLD4) + 0.0029 * sq(AF4)
        + 0.0009 * sq(SEATW4) - 0.0007 * sq(ELODT4) - 0.0058 * sq(TAPER4);

    double WFUEL4 = 385.5 - 6.707 * CSPD4 - 30.57 * AR4 + 0.5048 * SWEEP4
        - 18.91 * DPROP4 + 33.009998 * WINGLD4 - 7.543 * AF4 - 55.169998 * SEATW4
        - 1.447 * ELODT4 - 15.32 * TAPER4
        - 1.938 * CSPD4 * AR4 + 0.18 * CSPD4 * SWEEP4 - 1.201 * CSPD4 * DPROP4
        + 1.434 * CSPD4 * WINGLD4 - 0.5591 * CSPD4 * AF4 + 0.272 * CSPD4 * SEATW4
        + 1.172 * CSPD4 * ELODT4 - 0.5566 * CSPD4 * TAPER4
        - 0.0178 * AR4 * SWEEP4 - 0.6466 * AR4 * DPROP4 + 3.714 * AR4 * WINGLD4
        - 0.1913 * AR4 * AF4 + 0.3522 * AR4 * SEATW4 - 0.828 * AR4 * ELODT4
        - 1.633 * AR4 * TAPER4
        - 0.0415 * SWEEP4 * DPROP4 - 0.3772 * SWEEP4 * WINGLD4 - 0.0287 * SWEEP4 * AF4
        - 0.3237 * SWEEP4 * SEATW4 - 0.2653 * SWEEP4 * ELODT4 - 0.5519 * SWEEP4 * TAPER4
        + 0.5867 * DPROP4 * WINGLD4 - 2.332 * DPROP4 * AF4 + 0.0168 * DPROP4 * SEATW4
        + 0.2074 * DPROP4 * ELODT4 - 0.1206 * DPROP4 * TAPER4
        + 0.3465 * WINGLD4 * AF4 - 1.509 * WINGLD4 * SEATW4 - 2.134 * WINGLD4 * ELODT4
        + 0.7418 * WINGLD4 * TAPER4
        - 0.0803 * AF4 * SEATW4 + 0.1825 * AF4 * ELODT4 - 0.1052 * AF4 * TAPER4
        - 4.86 * SEATW4 * ELODT4 - 1.355 * SEATW4 * TAPER4 - 1.289 * ELODT4 * TAPER4
        + 0.1451 * sq(CSPD4) - 1.698 * sq(AR4) - 0.6064 * sq(SWEEP4)
        - 6.025 * sq(DPROP4) - 4.978 * sq(WINGLD4) + 0.2611 * sq(AF4)
        - 3.229 * sq(SEATW4) - 0.4999 * sq(ELODT4) - 1.068 * sq(TAPER4);

    double PURCH4 = 43730 + 142.5 * CSPD4 + 756.5 * AR4 + 2.004 * SWEEP4
        + 504.799988 * DPROP4 - 314.799988 * WINGLD4 + 194.100006 * AF4
        + 890.5 * SEATW4 + 114.099998 * ELODT4 + 334.5 * TAPER4
        + 43.029999 * CSPD4 * AR4 - 2.865 * CSPD4 * SWEEP4 + 27.42 * CSPD4 * DPROP4
        - 35.060001 * CSPD4 * WINGLD4 + 13.28 * CSPD4 * AF4 - 17.02 * CSPD4 * SEATW4
        - 18.83 * CSPD4 * ELODT4 + 14.26 * CSPD4 * TAPER4
        + 0.8277 * AR4 * SWEEP4 + 3.724 * AR4 * DPROP4 - 60.380001 * AR4 * WINGLD4
        + 3.039 * AR4 * AF4 + 7.395 * AR4 * SEATW4 + 18.809999 * AR4 * ELODT4
        + 35.360001 * AR4 * TAPER4
        - 0.0758 * SWEEP4 * DPROP4 + 6.451 * SWEEP4 * WINGLD4 + 0.5043 * SWEEP4 * AF4
        + 4.745 * SWEEP4 * SEATW4 + 3.97 * SWEEP4 * ELODT4 + 11.97 * SWEEP4 * TAPER4
        - 20.6 * DPROP4 * WINGLD4 + 20.549999 * DPROP4 * AF4 + 18.09 * DPROP4 * SEATW4
        - 7.554 * DPROP4 * ELODT4 + 5.429 * DPROP4 * TAPER4
        - 8.093 * WINGLD4 * AF4 + 2.433 * WINGLD4 * SEATW4 + 29.82 * WINGLD4 * ELODT4
        - 17.860001 * WINGLD4 * TAPER4
        + 3.676 * AF4 * SEATW4 - 3.442 * AF4 * ELODT4 + 2.995 * AF4 * TAPER4
        + 86.110001 * SEATW4 * ELODT4 + 29.129999 * SEATW4 * TAPER4
        + 26.49 * ELODT4 * TAPER4
        - 1.456 * sq(CSPD4) + 16.049999 * sq(AR4) - 0.7055 * sq(SWEEP4)
        + 45.990002 * sq(DPROP4) + 87.14 * sq(WINGLD4) + 0.8945 * sq(AF4)
        + 51.650002 * sq(SEATW4) - 2.256 * sq(ELODT4) + 25.59 * sq(TAPER4);

    double RANGE4 = 1941 - 6.768 * CSPD4 - 68.910004 * AR4 + 2.315 * SWEEP4
        - 346.799988 * DPROP4 - 365 * WINGLD4 - 65.25 * AF4 - 77.599998 * SEATW4
        + 13.19 * ELODT4 - 155.300003 * TAPER4
        + 0.1113 * CSPD4 * AR4 + 0.2207 * CSPD4 * SWEEP4 + 1.229 * CSPD4 * DPROP4
        + 3.721 * CSPD4 * WINGLD4 + 0.0176 * CSPD4 * AF4 - 0.0527 * CSPD4 * SEATW4
        + 0.6777 * CSPD4 * ELODT4 + 2.674 * CSPD4 * TAPER4
        + 0.0762 * AR4 * SWEEP4 - 24.07 * AR4 * DPROP4 - 31.309999 * AR4 * WINGLD4
        - 1.979 * AR4 * AF4 + 11.8 * AR4 * SEATW4 - 0.623 * AR4 * ELODT4
        - 38.310001 * AR4 * TAPER4
        - 1.15 * SWEEP4 * DPROP4 - 0.9785 * SWEEP4 * WINGLD4 - 0.2051 * SWEEP4 * AF4
        - 0.5879 * SWEEP4 * SEATW4 - 0.4512 * SWEEP4 * ELODT4 - 0.7754 * SWEEP4 * TAPER4
        + 115.400002 * DPROP4 * WINGLD4 - 26.84 * DPROP4 * AF4 + 27.91 * DPROP4 * SEATW4
        - 7.897 * DPROP4 * ELODT4 + 34.349998 * DPROP4 * TAPER4
        + 20.610001 * WINGLD4 * AF4 + 28.92 * WINGLD4 * SEATW4 - 3.006 * WINGLD4 * ELODT4
        - 27.870001 * WINGLD4 * TAPER4
        + 4.107 * AF4 * SEATW4 - 1.17 * AF4 * ELODT4 + 7.186 * AF4 * TAPER4
        - 7.389 * SEATW4 * ELODT4 + 18.02 * SEATW4 * TAPER4 - 2.451 * ELODT4 * TAPER4
        - 12.83 * sq(CSPD4) + 4.667 * sq(AR4) - 12.33 * sq(SWEEP4)
        - 85.330002 * sq(DPROP4) + 57.169998 * sq(WINGLD4) - 8.833 * sq(AF4)
        - 16.33 * sq(SEATW4) - 13.33 * sq(ELODT4) + 8.167 * sq(TAPER4);

    double LDMAX4 = 17.43 + 0.4811 * CSPD4 + 1.584 * AR4 + 0.0212 * SWEEP4
        - 0.0128 * DPROP4 - 0.5456 * WINGLD4 - 0.0054 * AF4 - 0.4984 * SEATW4
        + 0.1607 * ELODT4 + 0.0288 * TAPER4
        + 0.0635 * CSPD4 * AR4 + 0.0013 * CSPD4 * SWEEP4 - 0.0078 * CSPD4 * DPROP4
        - 0.0123 * CSPD4 * WINGLD4 - 0.0011 * CSPD4 * AF4 - 0.0065 * CSPD4 * SEATW4
        + 0.0049 * CSPD4 * ELODT4 + 0.0038 * CSPD4 * TAPER4
        + 0.0038 * AR4 * SWEEP4 - 0.0014 * AR4 * DPROP4 - 0.0601 * AR4 * WINGLD4
        - 0.0003 * AR4 * AF4 - 0.0503 * AR4 * SEATW4 + 0.017 * AR4 * ELODT4
        - 0.0029 * AR4 * TAPER4
        + 0.0002 * SWEEP4 * DPROP4 - 0.0032 * SWEEP4 * WINGLD4 + 0.0001 * SWEEP4 * AF4
        - 0.0043 * SWEEP4 * SEATW4 - 0.0021 * SWEEP4 * ELODT4 - 0.0001 * SWEEP4 * TAPER4
        - 0.0007 * DPROP4 * WINGLD4 - 0.0015 * DPROP4 * AF4 + 0.0009 * DPROP4 * SEATW4
        - 0.0002 * DPROP4 * ELODT4 + 0.0008 * DPROP4 * TAPER4
        - 0.0008 * WINGLD4 * AF4 - 0.0682 * WINGLD4 * SEATW4 - 0.0243 * WINGLD4 * ELODT4
        - 0.0047 * WINGLD4 * TAPER4
        - 0.0004 * AF4 * ELODT4 + 0.0007 * AF4 * TAPER4
        - 0.0406 * SEATW4 * ELODT4 - 0.0052 * SEATW4 * TAPER4 - 0.0018 * ELODT4 * TAPER4
        - 0.0923 * sq(CSPD4) - 0.1312 * sq(AR4) - 0.0127 * sq(SWEEP4)
        - 0.0041 * sq(DPROP4) + 0.0304 * sq(WINGLD4) - 0.0044 * sq(AF4)
        - 0.0545 * sq(SEATW4) - 0.0239 * sq(ELODT4) + 0.0066 * sq(TAPER4);

    double VCMAX4 = 197.800003 - 0.3562 * CSPD4 + 0.7729 * AR4 + 0.1807 * SWEEP4
        + 1.886 * DPROP4 + 5.332 * WINGLD4 - 0.1262 * AF4 - 3.585 * SEATW4
        + 1.18 * ELODT4 + 0.2323 * TAPER4
        + 0.0123 * CSPD4 * AR4 + 0.0073 * CSPD4 * SWEEP4 - 0.0462 * CSPD4 * DPROP4
        + 0.0075 * CSPD4 * WINGLD4 - 0.003 * CSPD4 * AF4 + 0.0375 * CSPD4 * SEATW4
        + 0.0225 * CSPD4 * ELODT4 + 0.017 * CSPD4 * TAPER4
        + 0.0071 * AR4 * SWEEP4 - 0.0914 * AR4 * DPROP4 + 0.1716 * AR4 * WINGLD4
        - 0.0115 * AR4 * AF4 + 0.0278 * AR4 * SEATW4 - 0.0074 * AR4 * ELODT4
        - 0.0583 * AR4 * TAPER4
        - 0.0124 * SWEEP4 * DPROP4 - 0.0116 * SWEEP4 * WINGLD4 - 0.0041 * SWEEP4 * AF4
        - 0.0337 * SWEEP4 * SEATW4 - 0.0132 * SWEEP4 * ELODT4 + 0.0028 * SWEEP4 * TAPER4
        - 0.2578 * DPROP4 * WINGLD4 - 0.3341 * DPROP4 * AF4 + 0.2057 * DPROP4 * SEATW4
        - 0.0696 * DPROP4 * ELODT4 - 0.0125 * DPROP4 * TAPER4
        - 0.1043 * WINGLD4 * AF4 - 0.6842 * WINGLD4 * SEATW4 - 0.1276 * WINGLD4 * ELODT4
        - 0.0217 * WINGLD4 * TAPER4
        + 0.0663 * AF4 * SEATW4 - 0.0185 * AF4 * ELODT4 + 0.0001 * AF4 * TAPER4
        - 0.2758 * SEATW4 * ELODT4 - 0.039 * SEATW4 * TAPER4 - 0.0162 * ELODT4 * TAPER4
        - 0.1479 * sq(CSPD4) - 0.1989 * sq(AR4) - 0.2574 * sq(SWEEP4)
        + 0.3991 * sq(DPROP4) - 0.5109 * sq(WINGLD4) + 0.0706 * sq(AF4)
        - 0.4174 * sq(SEATW4) - 0.2574 * sq(ELODT4) - 0.0384 * sq(TAPER4);

    return new double[]{NOISE4, WEMP4, DOC4, ROUGH4, WFUEL4, PURCH4, RANGE4, LDMAX4, VCMAX4};
  }

  private static double[] computeDesign6(
      double CSPD6, double AR6, double SWEEP6, double DPROP6, double WINGLD6,
      double AF6, double SEATW6, double ELODT6, double TAPER6) {

    double NOISE6 = 74.099998 - 0.0004 * CSPD6 - 0.0156 * AR6 + 0.0003 * SWEEP6
        + 0.9682 * DPROP6 + 0.0314 * WINGLD6 - 0.0053 * AF6 - 0.0015 * SEATW6
        - 0.0004 * ELODT6 + 0.0007 * TAPER6
        - 0.0001 * CSPD6 * DPROP6 - 0.0001 * CSPD6 * WINGLD6 - 0.0001 * CSPD6 * AF6
        - 0.0013 * AR6 * DPROP6 - 0.0014 * AR6 * WINGLD6 - 0.0002 * AR6 * AF6
        - 0.0001 * AR6 * SEATW6 + 0.0002 * AR6 * ELODT6 + 0.0002 * AR6 * TAPER6
        - 0.0003 * DPROP6 * WINGLD6 + 0.0057 * DPROP6 * AF6 - 0.0002 * DPROP6 * SEATW6
        + 0.0001 * DPROP6 * ELODT6 - 0.0001 * DPROP6 * TAPER6
        + 0.0002 * WINGLD6 * AF6 - 0.0002 * WINGLD6 * SEATW6 - 0.0002 * WINGLD6 * ELODT6
        + 0.0001 * WINGLD6 * TAPER6
        - 0.0001 * AF6 * SEATW6 - 0.0001 * SEATW6 * ELODT6
        + 0.0006 * sq(CSPD6) + 0.0017 * sq(AR6) + 0.0011 * sq(SWEEP6)
        + 0.1103 * sq(DPROP6) + 0.0004 * sq(WINGLD6) - 0.0021 * sq(AF6)
        + 0.001 * sq(SEATW6) + 0.001 * sq(ELODT6) + 0.0008 * sq(TAPER6);

    double WEMP6 = 1972 + 5.386 * CSPD6 + 33.290001 * AR6 - 0.0222 * SWEEP6
        + 10.82 * DPROP6 - 28.889999 * WINGLD6 + 5.588 * AF6 + 61.32 * SEATW6
        + 4.65 * ELODT6 + 16.620001 * TAPER6
        + 1.32 * CSPD6 * AR6 - 0.2549 * CSPD6 * SWEEP6 + 0.9089 * CSPD6 * DPROP6
        - 1.403 * CSPD6 * WINGLD6 + 0.3601 * CSPD6 * AF6 - 0.0118 * CSPD6 * SEATW6
        + 0.0123 * CSPD6 * ELODT6 + 0.4761 * CSPD6 * TAPER6
        + 0.0455 * AR6 * SWEEP6 - 0.0596 * AR6 * DPROP6 - 3.818 * AR6 * WINGLD6
        - 0.0408 * AR6 * AF6 + 0.5044 * AR6 * SEATW6 + 0.2867 * AR6 * ELODT6
        + 2.497 * AR6 * TAPER6
        + 0.0011 * SWEEP6 * DPROP6 + 0.151 * SWEEP6 * WINGLD6 + 0.0524 * SWEEP6 * AF6
        + 0.1164 * SWEEP6 * SEATW6 + 0.0491 * SWEEP6 * ELODT6 + 0.391 * SWEEP6 * TAPER6
        + 0.1823 * DPROP6 * WINGLD6 + 1.254 * DPROP6 * AF6 + 0.0491 * DPROP6 * SEATW6
        + 0.1011 * DPROP6 * ELODT6 + 0.0538 * DPROP6 * TAPER6
        + 0.098 * WINGLD6 * AF6 + 1.807 * WINGLD6 * SEATW6 + 1.119 * WINGLD6 * ELODT6
        - 1.437 * WINGLD6 * TAPER6
        + 0.0664 * AF6 * SEATW6 + 0.0566 * AF6 * ELODT6 + 0.0508 * AF6 * TAPER6
        + 2.914 * SEATW6 * ELODT6 + 0.0969 * SEATW6 * TAPER6 - 0.1483 * ELODT6 * TAPER6
        + 1.446 * sq(CSPD6) - 2.524 * sq(AR6) + 1.646 * sq(SWEEP6) + 1.381 * sq(DPROP6)
        + 2.651 * sq(WINGLD6) + 0.7561 * sq(AF6) + 3.116 * sq(SEATW6)
        + 1.211 * sq(ELODT6) - 0.4489 * sq(TAPER6);

    double DOC6 = 83.260002 + 11.86 * CSPD6 - 0.0805 * AR6 - 0.0218 * SWEEP6
        + 3.345 * DPROP6 - 0.6443 * WINGLD6 + 0.7039 * AF6 + 0.8256 * SEATW6
        - 0.0905 * ELODT6 + 0.0305 * TAPER6
        + 0.6 * CSPD6 * AR6 + 0.0284 * CSPD6 * SWEEP6 + 1.109 * CSPD6 * DPROP6
        + 0.6928 * CSPD6 * WINGLD6 + 0.0795 * CSPD6 * AF6 - 0.9926 * CSPD6 * SEATW6
        + 0.1119 * CSPD6 * ELODT6 - 0.0183 * CSPD6 * TAPER6
        + 0.0018 * AR6 * SWEEP6 + 0.1731 * AR6 * DPROP6 - 0.0631 * AR6 * WINGLD6
        - 0.246 * AR6 * AF6 - 0.0581 * AR6 * SEATW6 - 0.0008 * AR6 * ELODT6
        - 0.0048 * AR6 * TAPER6
        + 0.0024 * SWEEP6 * DPROP6 + 0.0011 * SWEEP6 * WINGLD6 - 0.0043 * SWEEP6 * AF6
        + 0.0048 * SWEEP6 * SEATW6 - 0.0019 * SWEEP6 * ELODT6 + 0.0025 * SWEEP6 * TAPER6
        + 0.1331 * DPROP6 * WINGLD6 + 0.6487 * DPROP6 * AF6 - 0.353 * DPROP6 * SEATW6
        - 0.0025 * DPROP6 * ELODT6 - 0.0586 * DPROP6 * TAPER6
        + 0.005 * WINGLD6 * AF6 + 0.1121 * WINGLD6 * SEATW6 + 0.0345 * WINGLD6 * ELODT6
        + 0.0103 * WINGLD6 * TAPER6
        - 0.0674 * AF6 * SEATW6 + 0.0003 * AF6 * ELODT6 - 0.0133 * AF6 * TAPER6
        + 0.0316 * SEATW6 * ELODT6 - 0.0007 * SEATW6 * TAPER6 + 0.0063 * ELODT6 * TAPER6
        - 10.77 * sq(CSPD6) - 0.288 * sq(AR6) - 0.288 * sq(SWEEP6)
        + 5.182 * sq(DPROP6) - 0.2313 * sq(WINGLD6) - 0.0895 * sq(AF6)
        - 0.2136 * sq(SEATW6) - 0.2562 * sq(ELODT6) - 0.2686 * sq(TAPER6);

    double ROUGH6 = 2.161 + 0.0007 * CSPD6 + 0.156 * AR6 - 0.0022 * SWEEP6
        + 0.0239 * DPROP6 - 0.1649 * WINGLD6 - 0.0007 * AF6 - 0.0675 * SEATW6
        - 0.0135 * ELODT6 + 0.0056 * TAPER6
        + 0.0006 * CSPD6 * AR6 + 0.0001 * CSPD6 * SWEEP6 + 0.0001 * CSPD6 * DPROP6
        - 0.0003 * CSPD6 * ELODT6 - 0.0003 * CSPD6 * TAPER6
        - 0.0003 * AR6 * SWEEP6 + 0.0008 * AR6 * DPROP6 - 0.0119 * AR6 * WINGLD6
        - 0.0054 * AR6 * SEATW6 - 0.0026 * AR6 * ELODT6 - 0.0003 * AR6 * TAPER6
        - 0.0002 * SWEEP6 * DPROP6 - 0.0002 * SWEEP6 * WINGLD6 - 0.0001 * SWEEP6 * AF6
        - 0.0002 * SWEEP6 * SEATW6 - 0.0002 * SWEEP6 * ELODT6 + 0.0001 * SWEEP6 * TAPER6
        - 0.0051 * DPROP6 * WINGLD6 - 0.0038 * DPROP6 * AF6 + 0.0024 * DPROP6 * SEATW6
        - 0.0009 * DPROP6 * ELODT6 - 0.0002 * DPROP6 * TAPER6
        - 0.001 * WINGLD6 * AF6 - 0.0018 * WINGLD6 * SEATW6 - 0.0009 * WINGLD6 * ELODT6
        - 0.0017 * WINGLD6 * TAPER6
        + 0.0009 * AF6 * SEATW6 - 0.0002 * AF6 * ELODT6 - 0.0001 * AF6 * TAPER6
        - 0.0005 * SEATW6 * ELODT6 - 0.0001 * SEATW6 * TAPER6 + 0.0003 * ELODT6 * TAPER6
        - 0.0006 * sq(CSPD6) - 0.0255 * sq(AR6) - 0.0043 * sq(SWEEP6)
        + 0.0039 * sq(DPROP6) + 0.0092 * sq(WINGLD6) + 0.0014 * sq(AF6)
        + 0.0003 * sq(SEATW6) - 0.0007 * sq(ELODT6) - 0.0052 * sq(TAPER6);

    double WFUEL6 = 359.700012 - 5.78 * CSPD6 - 29.99 * AR6 + 0.0682 * SWEEP6
        - 18.709999 * DPROP6 + 31 * WINGLD6 - 7.332 * AF6 - 63.599998 * SEATW6
        - 4.44 * ELODT6 - 16.709999 * TAPER6
        - 1.317 * CSPD6 * AR6 + 0.2715 * CSPD6 * SWEEP6 - 1.069 * CSPD6 * DPROP6
        + 1.185 * CSPD6 * WINGLD6 - 0.3931 * CSPD6 * AF6 - 0.8091 * CSPD6 * SEATW6
        + 0.0543 * CSPD6 * ELODT6 - 0.448 * CSPD6 * TAPER6
        - 0.0418 * AR6 * SWEEP6 - 0.5179 * AR6 * DPROP6 + 4.113 * AR6 * WINGLD6
        - 0.0407 * AR6 * AF6 - 0.3222 * AR6 * SEATW6 - 0.3059 * AR6 * ELODT6
        - 2.526 * AR6 * TAPER6
        - 0.0077 * SWEEP6 * DPROP6 - 0.1761 * SWEEP6 * WINGLD6 - 0.0543 * SWEEP6 * AF6
        - 0.1319 * SWEEP6 * SEATW6 - 0.0633 * SWEEP6 * ELODT6 - 0.3999 * SWEEP6 * TAPER6
        + 0.2791 * DPROP6 * WINGLD6 - 2.456 * DPROP6 * AF6 - 0.1367 * DPROP6 * SEATW6
        - 0.155 * DPROP6 * ELODT6 - 0.0405 * DPROP6 * TAPER6
        + 0.0736 * WINGLD6 * AF6 - 1.298 * WINGLD6 * SEATW6 - 1.258 * WINGLD6 * ELODT6
        + 1.389 * WINGLD6 * TAPER6
        - 0.1952 * AF6 * SEATW6 - 0.0011 * AF6 * ELODT6 - 0.0391 * AF6 * TAPER6
        - 2.965 * SEATW6 * ELODT6 - 0.1262 * SEATW6 * TAPER6 + 0.1433 * ELODT6 * TAPER6
        - 0.8956 * sq(CSPD6) + 1.729 * sq(AR6) - 1.613 * sq(SWEEP6)
        - 6.309 * sq(DPROP6) - 1.949 * sq(WINGLD6) - 0.6581 * sq(AF6)
        - 2.723 * sq(SEATW6) - 1.181 * sq(ELODT6) + 0.5244 * sq(TAPER6);

    double PURCH6 = 44220 + 124.400002 * CSPD6 + 749.200012 * AR6 + 9.32 * SWEEP6
        + 506.899994 * DPROP6 - 290.600006 * WINGLD6 + 192.800003 * AF6 + 1034 * SEATW6
        + 144.5 * ELODT6 + 364.799988 * TAPER6
        + 27.370001 * CSPD6 * AR6 - 5.918 * CSPD6 * SWEEP6 + 24.51 * CSPD6 * DPROP6
        - 29.690001 * CSPD6 * WINGLD6 + 10.97 * CSPD6 * AF6 + 2.652 * CSPD6 * SEATW6
        + 2.262 * CSPD6 * ELODT6 + 11.44 * CSPD6 * TAPER6
        + 2.046 * AR6 * SWEEP6 + 0.2715 * AR6 * DPROP6 - 67.760002 * AR6 * WINGLD6
        - 0.3336 * AR6 * AF6 + 25.07 * AR6 * SEATW6 + 7.377 * AR6 * ELODT6
        + 56.369999 * AR6 * TAPER6
        - 0.1879 * SWEEP6 * DPROP6 + 1.594 * SWEEP6 * WINGLD6 + 1.086 * SWEEP6 * AF6
        + 1.375 * SWEEP6 * SEATW6 + 0.4313 * SWEEP6 * ELODT6 + 8.3 * SWEEP6 * TAPER6
        - 11.7 * DPROP6 * WINGLD6 + 24.440001 * DPROP6 * AF6 + 23.85 * DPROP6 * SEATW6
        + 1.195 * DPROP6 * ELODT6 + 3.797 * DPROP6 * TAPER6
        - 3.027 * WINGLD6 * AF6 + 4.452 * WINGLD6 * SEATW6 + 18.059999 * WINGLD6 * ELODT6
        - 30.709999 * WINGLD6 * TAPER6
        + 7.103 * AF6 * SEATW6 + 1.01 * AF6 * ELODT6 + 1.463 * AF6 * TAPER6
        + 53.560001 * SEATW6 * ELODT6 + 4.063 * SEATW6 * TAPER6 - 3.483 * ELODT6 * TAPER6
        + 15.24 * sq(CSPD6) - 71.910004 * sq(AR6) + 33.990002 * sq(SWEEP6)
        + 60.540001 * sq(DPROP6) + 15.24 * sq(WINGLD6) + 28.49 * sq(AF6)
        + 50.490002 * sq(SEATW6) + 18.190001 * sq(ELODT6) - 9.011 * sq(TAPER6);

    double RANGE6 = 1932 - 6.389 * CSPD6 - 64.400002 * AR6 + 1.778 * SWEEP6
        - 342.799988 * DPROP6 - 356.399994 * WINGLD6 - 64.639999 * AF6
        - 91.739998 * SEATW6 + 5.658 * ELODT6 - 150.5 * TAPER6
        - 0.2363 * CSPD6 * AR6 + 0.0566 * CSPD6 * SWEEP6 + 1.346 * CSPD6 * DPROP6
        + 3.529 * CSPD6 * WINGLD6 + 0.0215 * CSPD6 * AF6 - 0.291 * CSPD6 * SEATW6
        + 0.0293 * CSPD6 * ELODT6 + 2.416 * CSPD6 * TAPER6
        + 0.0449 * AR6 * SWEEP6 - 24.780001 * AR6 * DPROP6 - 35.150002 * AR6 * WINGLD6
        - 2.131 * AR6 * AF6 + 13.65 * AR6 * SEATW6 - 0.0059 * AR6 * ELODT6
        - 35.599998 * AR6 * TAPER6
        - 0.9512 * SWEEP6 * DPROP6 - 0.6113 * SWEEP6 * WINGLD6 - 0.166 * SWEEP6 * AF6
        - 0.3457 * SWEEP6 * SEATW6 - 0.1191 * SWEEP6 * ELODT6 - 0.5762 * SWEEP6 * TAPER6
        + 114.300003 * DPROP6 * WINGLD6 - 26.83 * DPROP6 * AF6
        + 33.290001 * DPROP6 * SEATW6 - 4.76 * DPROP6 * ELODT6 + 33.189999 * DPROP6 * TAPER6
        + 20.6 * WINGLD6 * AF6 + 38.630001 * WINGLD6 * SEATW6 + 0.4238 * WINGLD6 * ELODT6
        - 31.940001 * WINGLD6 * TAPER6
        + 5.057 * AF6 * SEATW6 - 0.8809 * AF6 * ELODT6 + 6.873 * AF6 * TAPER6
        - 3.928 * SEATW6 * ELODT6 + 20.360001 * SEATW6 * TAPER6 + 0.0918 * ELODT6 * TAPER6
        - 11.73 * sq(CSPD6) + 4.268 * sq(AR6) - 10.73 * sq(SWEEP6)
        - 83.730003 * sq(DPROP6) + 39.27 * sq(WINGLD6) - 7.232 * sq(AF6)
        - 14.23 * sq(SEATW6) - 11.73 * sq(ELODT6) + 9.268 * sq(TAPER6);

    double LDMAX6 = 17.34 + 0.4817 * CSPD6 + 1.573 * AR6 + 0.0179 * SWEEP6
        - 0.0117 * DPROP6 - 0.5804 * WINGLD6 - 0.0053 * AF6 - 0.5638 * SEATW6
        + 0.0986 * ELODT6 + 0.0278 * TAPER6
        + 0.0603 * CSPD6 * AR6 + 0.0004 * CSPD6 * SWEEP6 - 0.0074 * CSPD6 * DPROP6
        - 0.0132 * CSPD6 * WINGLD6 - 0.0014 * CSPD6 * AF6 - 0.0059 * CSPD6 * SEATW6
        + 0.0053 * CSPD6 * ELODT6 + 0.0024 * CSPD6 * TAPER6
        + 0.0032 * AR6 * SWEEP6 - 0.0021 * AR6 * DPROP6 - 0.0632 * AR6 * WINGLD6
        - 0.0009 * AR6 * AF6 - 0.0553 * AR6 * SEATW6 + 0.0072 * AR6 * ELODT6
        + 0.0011 * AR6 * TAPER6
        + 0.0002 * SWEEP6 * DPROP6 - 0.0028 * SWEEP6 * WINGLD6 - 0.0035 * SWEEP6 * SEATW6
        - 0.0016 * SWEEP6 * ELODT6 - 0.0006 * SWEEP6 * TAPER6
        + 0.0003 * DPROP6 * WINGLD6 - 0.0006 * DPROP6 * AF6 + 0.0014 * DPROP6 * SEATW6
        + 0.001 * DPROP6 * ELODT6 + 0.0004 * DPROP6 * TAPER6
        - 0.0001 * WINGLD6 * AF6 - 0.0563 * WINGLD6 * SEATW6 - 0.0173 * WINGLD6 * ELODT6
        - 0.0049 * WINGLD6 * TAPER6
        + 0.0006 * AF6 * SEATW6 + 0.0004 * AF6 * ELODT6 + 0.0001 * AF6 * TAPER6
        - 0.0254 * SEATW6 * ELODT6 - 0.0069 * SEATW6 * TAPER6 - 0.0037 * ELODT6 * TAPER6
        - 0.0838 * sq(CSPD6) - 0.1499 * sq(AR6) - 0.0058 * sq(SWEEP6)
        + 0.0016 * sq(DPROP6) + 0.0214 * sq(WINGLD6) + 0.0013 * sq(AF6)
        - 0.0337 * sq(SEATW6) - 0.0133 * sq(ELODT6) + 0.0028 * sq(TAPER6);

    double VCMAX6 = 197.100006 - 0.3331 * CSPD6 + 0.7564 * AR6 + 0.153 * SWEEP6
        + 1.918 * DPROP6 + 5.044 * WINGLD6 - 0.1139 * AF6 - 4.07 * SEATW6
        + 0.7276 * ELODT6 + 0.2242 * TAPER6
        - 0.0231 * CSPD6 * AR6 - 0.009 * CSPD6 * SWEEP6 - 0.0365 * CSPD6 * DPROP6
        - 0.0011 * CSPD6 * WINGLD6 - 0.0064 * CSPD6 * AF6 + 0.0355 * CSPD6 * SEATW6
        + 0.028 * CSPD6 * ELODT6 + 0.013 * CSPD6 * TAPER6
        + 0.013 * AR6 * SWEEP6 - 0.0947 * AR6 * DPROP6 + 0.1791 * AR6 * WINGLD6
        - 0.0178 * AR6 * AF6 + 0.0598 * AR6 * SEATW6 - 0.0216 * AR6 * ELODT6
        - 0.0274 * AR6 * TAPER6
        - 0.006 * SWEEP6 * DPROP6 - 0.0243 * SWEEP6 * WINGLD6 - 0.001 * SWEEP6 * AF6
        - 0.0233 * SWEEP6 * SEATW6 - 0.0108 * SWEEP6 * ELODT6 - 0.0011 * SWEEP6 * TAPER6
        - 0.2368 * DPROP6 * WINGLD6 - 0.334 * DPROP6 * AF6 + 0.2329 * DPROP6 * SEATW6
        - 0.0419 * DPROP6 * ELODT6 - 0.0148 * DPROP6 * TAPER6
        - 0.0851 * WINGLD6 * AF6 - 0.6179 * WINGLD6 * SEATW6 - 0.0865 * WINGLD6 * ELODT6
        - 0.0158 * WINGLD6 * TAPER6
        + 0.0691 * AF6 * SEATW6 - 0.0088 * AF6 * ELODT6 - 0.0056 * AF6 * TAPER6
        - 0.1699 * SEATW6 * ELODT6 - 0.0462 * SEATW6 * TAPER6 - 0.028 * ELODT6 * TAPER6
        - 0.2315 * sq(CSPD6) - 0.384 * sq(AR6) - 0.013 * sq(SWEEP6)
        + 0.315 * sq(DPROP6) - 0.612 * sq(WINGLD6) + 0.206 * sq(AF6)
        - 0.2825 * sq(SEATW6) - 0.1225 * sq(ELODT6) - 0.013 * sq(TAPER6);

    return new double[]{NOISE6, WEMP6, DOC6, ROUGH6, WFUEL6, PURCH6, RANGE6, LDMAX6, VCMAX6};
  }

  private static double computeCvTotal(
      double noise2, double wemp2, double doc2, double rough2, double wfuel2, double range2,
      double noise4, double wemp4, double doc4, double rough4, double wfuel4, double range4,
      double noise6, double wemp6, double doc6, double rough6, double wfuel6, double range6) {

    double n2cv = Math.max(0, (noise2 - 75) / 75);
    double w2cv = Math.max(0, (wemp2 - 2200) / 2200);
    double d2cv = Math.max(0, (doc2 - 80) / 80);
    double r2cv = Math.max(0, (rough2 - 2) / 2);
    double f2cv = Math.max(0, (wfuel2 - 450) / 450);
    double g2cv = Math.max(0, -(range2 - 2000) / 2000);

    double n4cv = Math.max(0, (noise4 - 75) / 75);
    double w4cv = Math.max(0, (wemp4 - 2200) / 2200);
    double d4cv = Math.max(0, (doc4 - 80) / 80);
    double r4cv = Math.max(0, (rough4 - 2) / 2);
    double f4cv = Math.max(0, (wfuel4 - 475) / 475);
    double g4cv = Math.max(0, -(range4 - 2000) / 2000);

    double n6cv = Math.max(0, (noise6 - 75) / 75);
    double w6cv = Math.max(0, (wemp6 - 2200) / 2200);
    double d6cv = Math.max(0, (doc6 - 80) / 80);
    double r6cv = Math.max(0, (rough6 - 2) / 2);
    double f6cv = Math.max(0, (wfuel6 - 500) / 500);
    double g6cv = Math.max(0, -(range6 - 2000) / 2000);

    return n2cv + w2cv + d2cv + r2cv + f2cv + g2cv
        + n4cv + w4cv + d4cv + r4cv + f4cv + g4cv
        + n6cv + w6cv + d6cv + r6cv + f6cv + g6cv;
  }

  private static double sq(double x) {
    return x * x;
  }
}
