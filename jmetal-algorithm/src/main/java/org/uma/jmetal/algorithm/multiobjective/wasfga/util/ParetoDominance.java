package org.uma.jmetal.algorithm.multiobjective.wasfga.util;

/**
 * @author Rubén Saborido Infantes
 * This class offers different methods to calculate the Pareto dominance
 */
public class ParetoDominance {
		
	/**
	 * Check the Pareto-dominance between two points
	 * @param v1 Point1
	 * @param v2 Point2
	 * @return -1 if Point1 dominates Point2, 1 if Point2 dominates Point1, and 0 if no one dominate the other	 
	 */
    public static int checkParetoDominance(double[] v1, double[] v2) {
        int dominate1; // indicates if some objective of solution1 dominates the same objective in solution2
        int dominate2; // is the complementary of dominate1.

        dominate1 = 0;
        dominate2 = 0;

        int flag; //stores the result of the comparison	

        double value1, value2;
        for (int i = 0; i < v1.length; i++) {
            value1 = v1[i];
            value2 = v2[i];
            if (value1 < value2) {
                flag = -1;
            } else if (value1 > value2) {
                flag = 1;
            } else {
                flag = 0;
            }

            if (flag == -1) {
                dominate1 = 1;
            }

            if (flag == 1) {
                dominate2 = 1;
            }
        }

        if (dominate1 == dominate2) {
            return 0; //No one dominate the other
        }
        if (dominate1 == 1) {
            return -1; // solution1 dominate
        }
        return 1;    // solution2 dominate		
    }
}
