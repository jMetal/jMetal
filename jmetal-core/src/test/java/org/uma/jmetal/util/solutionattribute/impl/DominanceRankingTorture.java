package org.uma.jmetal.util.solutionattribute.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * This is a randomized comparison of different implementations of the {@link Ranking} interface.
 *
 * @author Maxim Buzdalov
 */
public class DominanceRankingTorture {
    private static void run(int n, int d, boolean flat) {
        Ranking<Solution<?>> oldRanking = new DominanceRanking<>();
        Ranking<Solution<?>> newRanking = new ExperimentalFastDominanceRanking<>();

        OverallConstraintViolation<Solution<?>> violation = new OverallConstraintViolation<>();

        Random r = new Random(8235239423L * n + 6123213 * d);
        for (int times = 0; times < 1000000; ++times) {
            if (times > 0 && times % 1000 == 0) {
                System.out.println(times + " instances checked");
            }
            List<Solution<?>> oldInstance = new ArrayList<>(n);
            List<Solution<?>> newInstance = new ArrayList<>(n);
            List<double[]> pointCopies = new ArrayList<>();
            List<Double> constraints = new ArrayList<>();
            for (int i = 0; i < n; ++i) {
                double[] pointContents = new double[d];
                if (flat) {
                    for (int j = 1; j < d; ++j) {
                        pointContents[j] = r.nextDouble();
                        pointContents[0] -= pointContents[j];
                    }
                } else {
                    for (int j = 0; j < d; ++j) {
                        pointContents[j] = r.nextInt(10);
                    }
                }

                Solution<?> solutionForOld = new PointSolution(d);
                Solution<?> solutionForNew = new PointSolution(d);
                System.arraycopy(pointContents, 0, solutionForOld.getObjectives(), 0, d);
                System.arraycopy(pointContents, 0, solutionForNew.getObjectives(), 0, d);
                oldInstance.add(solutionForOld);
                newInstance.add(solutionForNew);
                double constraintViolation = r.nextBoolean() ? 0 : -1;
                violation.setAttribute(solutionForOld, constraintViolation);
                violation.setAttribute(solutionForNew, constraintViolation);

                pointCopies.add(pointContents);
                constraints.add(constraintViolation);
            }

            oldRanking.computeRanking(oldInstance);
            newRanking.computeRanking(newInstance);
            for (int i = 0; i < n; ++i) {
                int oldRank = oldRanking.getAttribute(oldInstance.get(i));
                int newRank = newRanking.getAttribute(newInstance.get(i));
                if (oldRank != newRank) {
                    for (int j = 0; j < n; ++j) {
                        System.err.println("Point " + j + " " + Arrays.toString(pointCopies.get(j))
                                + " constraint " + constraints.get(j)
                                + " => old " + oldRanking.getAttribute(oldInstance.get(j))
                                + " new " + newRanking.getAttribute(newInstance.get(j)));
                    }

                    throw new AssertionError("Ranks not equal: iteration " + times
                            + " location " + i + " old value " + oldRank + " new value " + newRank);
                }
            }
        }
    }

    // Usage: DominanceRankingTorture N D <flat/cube>
    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int d = Integer.parseInt(args[1]);
        boolean flat = args[2].equals("flat");
        run(n, d, flat);
    }
}
