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

import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

/**
 * @author Juan Olmedilla Arregui
 *
 * @param <S> type of solution
 */
@SuppressWarnings("serial")
public abstract class AbstractSingleSolutionSequentialAlgorithm<S extends Solution<?>>
        extends AbstractMeasurableTimeLimited<S> {

    public static final String IMPROVEMENTS_MEASURE_NAME = "currentImprovement";

    public static final String SOLUTION_MEASURE_NAME = "currentSolution";

    protected S result;
    protected Problem<S> problem;
    protected MutationOperator<S> mutationOperator;
    protected MutationOperator<S> rollbackOperator;
    protected int targetNumberOfImprovements;
    protected CountingMeasure improvements;
    protected BasicMeasure<S> solutionMeasure;

    /**
     * @param durationInMinutes duration in minutes
     */
    public AbstractSingleSolutionSequentialAlgorithm(
            int targetNumberOfImprovements,
            MutationOperator<S> mutationOperator,
            MutationOperator<S> rollbackOperator, Problem<S> problem,
            int durationInMinutes) {
        super(durationInMinutes);
        this.targetNumberOfImprovements = targetNumberOfImprovements;
        this.mutationOperator = mutationOperator;
        this.rollbackOperator = rollbackOperator;
        this.problem = problem;
    }

    protected void updateResult(S solution) {
        if (getLocalSearch().getNumberOfImprovements() > 0) {
            result = solution;
        }
    }

    @Override
    protected void initMeasures() {
        super.initMeasures();
        solutionMeasure = new BasicMeasure<S>();
        improvements = new CountingMeasure(0);

        measureManager.setPullMeasure(IMPROVEMENTS_MEASURE_NAME, improvements);

        measureManager.setPushMeasure(SOLUTION_MEASURE_NAME, solutionMeasure);
        measureManager.setPushMeasure(IMPROVEMENTS_MEASURE_NAME, improvements);

    }

    @Override
    public void initProgress() {
        super.initProgress();
        improvements.reset(0);
    }

    @Override
    public void updateProgress() {
        super.updateProgress();
        solutionMeasure.push(getResult());
        improvements.increment(getLocalSearch().getNumberOfImprovements());

    }

    @Override
    public S getResult() {
        return result;
    }

    protected boolean isStoppingConditionReached() {
        return (targetNumberOfImprovements >= 0
                && improvements.get() >= targetNumberOfImprovements)
                || super.isStoppingConditionReached();
    }

    @Override
    public MeasureManager getMeasureManager() {
        return measureManager;
    }

}