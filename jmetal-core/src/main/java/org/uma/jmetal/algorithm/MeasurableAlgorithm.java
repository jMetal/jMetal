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
package org.uma.jmetal.algorithm;

import org.uma.jmetal.algorithm.Algorithm;
import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.solution.Solution;

/**
 * @author Juan Olmedilla Arregui
 *
 */
public interface MeasurableAlgorithm<S extends Solution<?>>
        extends Algorithm<S>,Measurable {
    
    static final String EVALUATIONS_MEASURE_NAME = "currentEvaluation";
    
    static final String EXECUTION_TIME_MEASURE_NAME = "currentExecutionTime";
}
