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

import org.uma.jmetal.algorithm.MeasurableAlgorithm;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.solution.Solution;

/**
 * @author Juan Olmedilla Arregui
 *
 * @param <S> type of solution
 */
@SuppressWarnings("serial")
public abstract class AbstractMeasurableTimeLimited<S extends Solution<?>>
        implements MeasurableAlgorithm<S> {
    
    protected CountingMeasure evaluations;
    protected DurationMeasure durationMeasure;
    protected SimpleMeasureManager measureManager;
    protected long totalAllowedDuration;

    protected AbstractMeasurableTimeLimited(int durationInMinutes) {
        this.totalAllowedDuration = durationInMinutes > 0
                ? durationInMinutes * 60 * 1000 : -1;
        initMeasures();
    }
    
    public void initProgress() {
        evaluations.reset(1);
    }

    public void updateProgress() {
        evaluations.increment(getLocalSearch().getEvaluations());
    }

    @Override
    public void run() {
        durationMeasure.reset();
        durationMeasure.start();
        execute();
        durationMeasure.stop();
    }

    protected boolean isStoppingConditionReached() {
        return totalAllowedDuration >= 0
                && durationMeasure.get() >= totalAllowedDuration;
    }

    /* Measures code */
    protected void initMeasures() {
        durationMeasure = new DurationMeasure();
        evaluations = new CountingMeasure(0);

        measureManager = new SimpleMeasureManager();
        measureManager.setPullMeasure(EXECUTION_TIME_MEASURE_NAME, durationMeasure);
        measureManager.setPullMeasure(EVALUATIONS_MEASURE_NAME, evaluations);

        measureManager.setPushMeasure(EVALUATIONS_MEASURE_NAME, evaluations);
    }
    
    protected abstract LocalSearchOperator<S> getLocalSearch();

    protected abstract void execute();

}