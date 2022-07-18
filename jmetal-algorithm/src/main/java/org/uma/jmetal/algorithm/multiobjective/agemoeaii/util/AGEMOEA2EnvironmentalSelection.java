package org.uma.jmetal.algorithm.multiobjective.agemoeaii.util;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.algorithm.multiobjective.agemoea.util.AGEMOEAEnvironmentalSelection;
import org.uma.jmetal.solution.Solution;

import java.util.Arrays;
import java.util.List;

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

        if (referencePoint == null)
            return 1.0;

        double p = findZero(referencePoint, 0.001);

        if (Double.isNaN(p) || p < 0.10 || Double.isInfinite(p))
            p = 1;

        return p;
    }

    /**
     * Newton-Raphson method for Lp curves
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

    /**
     * Derivative function for teh Lp manifold. This method is used by the Newton-Raphson method
     * @param referencePoint reference point used for the estimating the Lp curvature
     * @param x
     * @return value of the derivative for Lp with p=x
     */
    protected double derivativeFunction(double[] referencePoint, double x) {
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

    /**
     * Function used by the Newton-Raphson method
     * @param point reference point used for the estimating the Lp curvature
     * @param x
     * @return value of the derivative for Lp with p=x
     */
    protected double originalFunction(double @NotNull [] point, double x) {
        double f = 0.0;
        for (double v : point) {
            double pow = Math.pow(Math.abs(v), x);
            f += pow;
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

    /**
     * This method project a given point into the Lp manifold
     * @param point point to project
     * @param p curvature of the Lp manifold
     * @return projected point
     */
    protected double[] projectPoint(double @NotNull [] point, double p) {
        double[] projection = point.clone();
        double dist = minkowskiDistance(point, ZERO, p);
        for (int i = 0; i < point.length; i++)
            projection[i] *= 1.0 / dist;

        return projection;
    }

    /**
     * Utility function that returns the middle point on the line connecting the two points A and B
     * @param A first extreme
     * @param B second extreme
     * @return middle point
     */
    protected double[] midPoint(double[] A, double[] B) {
        double @NotNull [] midpoint = new double[10];
        int count = 0;
        for (int i = 0; i < A.length; i++) {
            double v = A[i] * 0.5 + B[i] * 0.5;
            if (midpoint.length == count) midpoint = Arrays.copyOf(midpoint, count * 2);
            midpoint[count++] = v;
        }
        midpoint = Arrays.copyOfRange(midpoint, 0, count);

        return midpoint;
    }

}
