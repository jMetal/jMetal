/**
 * Copyright 2017 Juan Olmedilla Arregui
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
package org.uma.jmetal.algorithm.impl;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * @author Juan Olmedilla Arregui
 *
 * @param <S> type of solution
 */
@SuppressWarnings("serial")
public abstract class AbstractHillClimb<S extends Solution<?>>
        extends AbstractSingleSolutionSequentialAlgorithm<S> {

    private LocalSearchOperator<S> search;

    /**
     * Constructor for a backtrack version of the algorithm. It applies one
     * mutation at a time and if there is no improvement it undoes it with the
     * reverse operator. This is meant for problem whose solutions share some
     * information so that collide with each other.
     * 
     * @param mutations
     *            number of desired successful mutations
     * @param mutationOperator the mutation operator
     * @param rollbackOperator
     *            operator to undo the mutation
     * @param problem original problem
     * @param durationInMinutes
     *            negative if inifinite
     */
    public AbstractHillClimb(int mutations,
            MutationOperator<S> mutationOperator,
            MutationOperator<S> rollbackOperator, Problem<S> problem,
            int durationInMinutes) {
        super(mutations, mutationOperator, rollbackOperator, problem,
                durationInMinutes);
    }

    @Override
    protected void execute() {
        result = problem.createSolution();
        problem.evaluate(result);
        initProgress();
        while (!isStoppingConditionReached()) {
            S solution = getLocalSearch().execute(result);
            updateResult(solution);
            updateProgress();
        }
    }

    protected synchronized LocalSearchOperator<S> getLocalSearch() {
        if (search == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return search;
    }

    private synchronized void resetLocalSearch() {
        search = getNewLocalSearch();
        notifyAll();
    }

    public void initProgress() {
        super.initProgress();
        resetLocalSearch();
    }

    public void updateProgress() {
        super.updateProgress();
        resetLocalSearch();
    }

    protected abstract LocalSearchOperator<S> getNewLocalSearch();
}