package org.uma.jmetal.algorithm.multiobjective.agemoeaii.util;

import org.uma.jmetal.algorithm.multiobjective.agemoea.util.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.stream;

/**
 * Environmental Selection used in AGE-MOEA-II (Adaptive GEometry-based Many-Objective Evolutionary Algorithm II)
 *
 * @author Annibale Panichella
 */
public class AGEMOEA2EnvironmentalSelection<S extends Solution<?>> extends AGEMOEAEnvironmentalSelection<S> {
    private double[] ZERO;

    public AGEMOEA2EnvironmentalSelection(int numberOfObjectives) {
        super(numberOfObjectives);
        ZERO = new double[numberOfObjectives];
    }

    /**
     * This method compute the front/shape of the front using the Newton-Raphson method
     *
     * @param normalizedFront normalized non-dominated front
     * @param extremePoints   extreme points of the non-dominated front
     * @return
     */
    @Override
    protected double computeGeometry(List<double[]> normalizedFront, List<Integer> extremePoints) {
        // find solutions closer to the ideal point
        double[] d = new double[normalizedFront.size()];
        for (int i = 0; i < d.length; i++) {
            if (extremePoints.contains(i)) {
                d[i] = Double.POSITIVE_INFINITY;
            } else {
                d[i] = minkowskiDistance(normalizedFront.get(i), ZERO, 2);
            }
        }

        double[] referencePoint = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < d.length; i++) {
            if (d[i] < minDistance) {
                minDistance = d[i];
                referencePoint = normalizedFront.get(i);
            }
        }

        double p = findZero(referencePoint, 0.001);

        if (Double.isNaN(p) || p < 0.10 || Double.isInfinite(p))
            p = 1;

        return p;
    }

    /**
     * Newton Raphson method for Lp curves
     * @param referencePoint Point used to compute the Lp curvature
     * @param precision delta-error (precision) used as stopping-condition
     * @return the value of p (front curvature)
     */
    private double findZero(double[] referencePoint, double precision) {
        double x = 1.0;

        double pastValue = x;
        for (int i = 0; i < 100; i++) {
            // Original function
            double f = originalFunction(referencePoint, x);

            // Derivative function
            double ff = derivativeFunction(referencePoint, x);

            // zero of function
            x = x - f / ff;

            if (Math.abs(x - pastValue) <= precision) {
                break;
            } else {
                pastValue = x;
            }
        }

        return x;
    }

    private double derivativeFunction(double[] referencePoint, double x) {
        double numerator = 0.0;
        double denominator = 0.0;

        for (int i = 0; i < this.numberOfObjectives; i++) {
            if (referencePoint[i] > 0) {
                numerator += Math.pow(referencePoint[i], x) * Math.log(referencePoint[i]);
                denominator += Math.pow(referencePoint[i], x);
            }
        }

        if (denominator == 0)
            return 1.0;

        return numerator / denominator;
    }

    protected double originalFunction(double[] point, double p) {
        double f = 0.0;
        for (int i = 0; i < point.length; i++) {
            f += Math.pow(Math.abs(point[i]), p);
        }
        return Math.log(f);
    }

    @Override
    protected double[][] pairwiseDistances(List<double[]> normalizedFront, double p) {
        // projecting the non-dominated front on the Lp manifold
        for (int i = 0; i < normalizedFront.size(); i++) {
            normalizedFront.set(i, this.projectPoint(normalizedFront.get(i), p));
        }

        // approximating the geodesic distance
        double[][] distances = new double[normalizedFront.size()][normalizedFront.size()];
        for (int i = 0; i < normalizedFront.size() - 1; i++) {
            for (int j = i + 1; j < normalizedFront.size(); j++) {
                double[] midpoint = this.midPoint(normalizedFront.get(i), normalizedFront.get(j));
                midpoint = this.projectPoint(midpoint, P);

                distances[i][j] = this.minkowskiDistance(normalizedFront.get(i), midpoint, 2) +
                        this.minkowskiDistance(midpoint, normalizedFront.get(j), 2);
                distances[j][i] = distances[i][j];
            }
        }
        return distances;
    }

    protected double[] projectPoint(double[] point, double p) {
        double[] projection = point.clone();
        double dist = minkowskiDistance(point, ZERO, p);
        for (int i = 0; i < point.length; i++)
            projection[i] *= 1.0 / dist;

        return projection;
    }

    protected double[] midPoint(double[] A, double[] B) {
        double[] midpoint = new double[A.length];
        for (int i = 0; i < A.length; i++)
            midpoint[i] = A[i] * 0.5 + B[i] * 0.5;

        return midpoint;
    }

}
