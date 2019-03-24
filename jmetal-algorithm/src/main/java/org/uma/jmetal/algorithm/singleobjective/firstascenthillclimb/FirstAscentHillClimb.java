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
package org.uma.jmetal.algorithm.singleobjective.firstascenthillclimb;

import java.util.Comparator;

import org.uma.jmetal.algorithm.impl.AbstractHillClimb;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.localsearch.BasicLocalSearch;
import org.uma.jmetal.operator.impl.localsearch.LocalSearchWithMutationReverse;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.FitnessComparator;

/**
 * First Ascent Hill Climb: Single objective basic local search Time bound
 * search.
 * 
 * @author Juan Olmedilla Arregui
 *
 */
public class FirstAscentHillClimb<S extends Solution<?>>
        extends AbstractHillClimb<S> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public FirstAscentHillClimb(int mutations,
            MutationOperator<S> mutationOperator,
            MutationOperator<S> rollbackOperator, Problem<S> problem,
            int durationInMinutes) {
        super(mutations, mutationOperator, rollbackOperator, problem,
                durationInMinutes);
    }

    /**
     * Constructor with no reverse operator. This is meant for all other cases
     * in which the Solutions do not share important information.
     * 
     * @param mutations number of maximum mutations we allow
     * @param mutationOperator the mutation operator
     * @param problem original problem
     * @param durationInMinutes duration in minutes
     */
    public FirstAscentHillClimb(int mutations,
            MutationOperator<S> mutationOperator, Problem<S> problem,
            int durationInMinutes) {
        this(mutations, mutationOperator, null, problem, durationInMinutes);
    }

    @Override
    public String getDescription() {
        return "First Ascent Hill Climb";
    }

    @Override
    public String getName() {
        return "HCF";
    }

    @Override
    protected LocalSearchOperator<S> getNewLocalSearch() {
        LocalSearchOperator<S> search;
        Comparator<S> comparator = new FitnessComparator<S>();
        if (rollbackOperator != null)
            search = new LocalSearchWithMutationReverse<S>(1,
                    mutationOperator, rollbackOperator, comparator,
                    problem);
        else
            search = new BasicLocalSearch<S>(1, mutationOperator,
                    comparator, problem);
        return search;
    }

}
