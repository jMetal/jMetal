/**
 * Copyright 2016 Juan Olmedilla Arregui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uma.jmetal.util.comparator;

import java.io.Serializable;
import java.util.Comparator;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.JMetalException;


/**
 * @author Juan Olmedilla Arregui
 *
 */
public class WeightedSumComparator<S extends Solution<?>>
        implements Comparator<S>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public enum Ordering {
        ASCENDING, DESCENDING
    };

    private Ordering order;

    private double[] weights;

    /**
     * Basic constructor giving weights to each objective
     * <p>
     * Ordering of final compounded objective is ascending
     * 
     * @param weights
     *            of each objective in same order of appearance
     */
    public WeightedSumComparator(double... weights) {
        this.weights = weights;
        order = Ordering.ASCENDING;
    }

    /**
     * Alternate constructor with specified ordering
     * <p>
     * Ordering of final compounded objective is ascending
     * 
     * @param weights
     *            of each objective in same order of appearance
     */
    public WeightedSumComparator(Ordering order, double... weights) {
        this.weights = weights;
        this.order = order;
    }

    /**
     * Compares two solutions according to a weighted sum of all objectives
     *
     * @param solution1 The first solution
     * @param solution2 The second solution
     * @return -1, or 0, or 1 if solution1 is less than, equal, or greater than solution2,
     * respectively, according to the established order
     */
    @Override
    public int compare(S solution1, S solution2) {
        int result;
        if (solution1 == null) {
            if (solution2 == null) {
                result = 0;
            } else {
                result = 1;
            }
        } else if (solution2 == null) {
            result = -1;
        } else if (solution1.getNumberOfObjectives() != weights.length) {
            throw new JMetalException("The solution1 has "
                    + solution1.getNumberOfObjectives() + " objectives "
                    + "and the number of weights is " + weights.length);
        } else if (solution2.getNumberOfObjectives() != weights.length) {
            throw new JMetalException("The solution2 has "
                    + solution2.getNumberOfObjectives() + " objectives "
                    + "and the number of weights is " + weights.length);
        } else {
            Double objective1 = 0.0;
            Double objective2 = 0.0;
            int index = 0;
            for (double weight : weights) {
                objective1 += solution1.getObjective(index)*weight;
                objective2 += solution2.getObjective(index)*weight;
                index++;
            }
            if (order == Ordering.ASCENDING) {
                result = Double.compare(objective1, objective2);
            } else {
                result = Double.compare(objective2, objective1);
            }
        }
        return result;
    }


}
