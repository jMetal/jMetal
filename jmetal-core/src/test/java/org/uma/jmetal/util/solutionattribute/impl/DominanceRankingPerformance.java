package org.uma.jmetal.util.solutionattribute.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.point.PointSolution;
import org.uma.jmetal.util.solutionattribute.Ranking;

/**
 * This is a rudimentary benchmarking suite for comparing implementations of {@link Ranking} interface.
 *
 * Normally this should be done using Java Microbenchmark Harness or similar tools. This is just a proof-of-concept.
 *
 * @author Maxim Buzdalov
 */
public class DominanceRankingPerformance {
    private static void measure(Ranking<Solution<?>> ranking, int n, int d, boolean flat) {
        List<Solution<?>> solutions = new ArrayList<>(n);
        Random r = new Random(8235239423L * n + 6123213 * d);
        for (int i = 0; i < n; ++i) {
            Solution<?> solution = new PointSolution(d);
            double[] objectives = solution.getObjectives();
            if (flat) {
                for (int j = 1; j < d; ++j) {
                    objectives[j] = r.nextDouble();
                    objectives[0] -= objectives[j];
                }
            } else {
                for (int j = 0; j < d; ++j) {
                    objectives[j] = r.nextDouble();
                }
            }
            solutions.add(solution);
        }
        int rounds = Math.max(1, 100000000 / n / n);
        for (int iteration = 0; iteration < 20; ++iteration) {
            int sumRanks = 0;
            long t0 = System.nanoTime();
            for (int round = 0; round < rounds; ++round) {
                ranking.computeRanking(solutions);
                for (Solution<?> s : solutions) {
                    sumRanks += ranking.getAttribute(s);
                }
            }
            double time = (System.nanoTime() - t0) * 1e-9 / rounds;
            System.out.println(iteration + ": " + time + ", checksum " + sumRanks);
        }
    }

    // Usage: DominanceRankingPerformance <new/old> N D <flat/cube>
    public static void main(String[] args) {
        Ranking<Solution<?>> r = args[0].equals("new")
                ? new ExperimentalFastDominanceRanking<>()
                : new DominanceRanking<>();
        int n = Integer.parseInt(args[1]);
        int d = Integer.parseInt(args[2]);
        boolean flat = args[3].equals("flat");
        measure(r, n, d, flat);
    }
}
