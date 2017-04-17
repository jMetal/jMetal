//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package org.uma.jmetal.algorithm.multiobjective.dmopso;

import java.util.List;

import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.qualityindicator.impl.Epsilon;
import org.uma.jmetal.qualityindicator.impl.hypervolume.PISAHypervolume;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;

public class DMOPSOMeasures extends DMOPSO implements Measurable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected CountingMeasure iterations;
    protected DurationMeasure durationMeasure;
    protected SimpleMeasureManager measureManager;
    protected BasicMeasure<List<DoubleSolution>> solutionListMeasure;
    protected BasicMeasure<Double> hypervolumeValue;
    protected BasicMeasure<Double> epsilonValue;
    protected Front referenceFront;

    public DMOPSOMeasures(DoubleProblem problem, int swarmSize, int maxIterations, double r1Min, double r1Max,
            double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin,
            double weightMax, double changeVelocity1, double changeVelocity2, DMOPSO.FunctionType functionType,
            String dataDirectory, int maxAge, String name) {
        super(problem, swarmSize, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin,
                weightMax, changeVelocity1, changeVelocity2, functionType, dataDirectory, maxAge, name);
        this.referenceFront = new ArrayFront();
        initMeasures();
    }

    public DMOPSOMeasures(DoubleProblem problem, int swarmSize, int maxIterations, double r1Min, double r1Max,
            double r2Min, double r2Max, double c1Min, double c1Max, double c2Min, double c2Max, double weightMin,
            double weightMax, double changeVelocity1, double changeVelocity2, DMOPSO.FunctionType functionType,
            String dataDirectory, int maxAge) {
        super(problem, swarmSize, maxIterations, r1Min, r1Max, r2Min, r2Max, c1Min, c1Max, c2Min, c2Max, weightMin,
                weightMax, changeVelocity1, changeVelocity2, functionType, dataDirectory, maxAge);
        this.referenceFront = new ArrayFront();
        initMeasures();
    }

    @Override
    protected void initProgress() {
        this.iterations.reset();
    }

    @Override
    protected void updateProgress() {
        this.iterations.increment();
        hypervolumeValue.push(new PISAHypervolume<DoubleSolution>(referenceFront).evaluate(getResult()));
        epsilonValue.push(new Epsilon<DoubleSolution>(referenceFront).evaluate(getResult()));
        solutionListMeasure.push(getResult());
    }

    @Override
    protected boolean isStoppingConditionReached() {
        return this.iterations.get() >= maxIterations;
    }

    @Override
    public void run() {
        durationMeasure.reset();
        durationMeasure.start();
        super.run();
        durationMeasure.stop();
    }

    /* Measures code */
    private void initMeasures() {
        durationMeasure = new DurationMeasure();
        iterations = new CountingMeasure(0);
        solutionListMeasure = new BasicMeasure<>();
        hypervolumeValue = new BasicMeasure<>();
        epsilonValue = new BasicMeasure<>();

        measureManager = new SimpleMeasureManager();
        measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
        measureManager.setPullMeasure("currentEvaluation", iterations);
        measureManager.setPullMeasure("hypervolume", hypervolumeValue);
        measureManager.setPullMeasure("epsilon", epsilonValue);

        measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
        measureManager.setPushMeasure("currentEvaluation", iterations);
        measureManager.setPushMeasure("hypervolume", hypervolumeValue);
        measureManager.setPushMeasure("epsilon", epsilonValue);
    }

    @Override
    public String getDescription() {
        return "MOPSO with decomposition. Version using measures";
    }

    @Override
    public MeasureManager getMeasureManager() {
        return this.measureManager;
    }

    public void setReferenceFront(Front referenceFront) {
        this.referenceFront = referenceFront;
    }
}
