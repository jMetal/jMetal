package org.uma.jmetal.algorithm.multiobjective.wasfga;

import java.util.List;

import org.uma.jmetal.measure.Measurable;
import org.uma.jmetal.measure.MeasureManager;
import org.uma.jmetal.measure.impl.BasicMeasure;
import org.uma.jmetal.measure.impl.CountingMeasure;
import org.uma.jmetal.measure.impl.DurationMeasure;
import org.uma.jmetal.measure.impl.SimpleMeasureManager;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;

/**
 * Implementation of the preference based algorithm named WASF-GA on jMetal5.0
 *
 * @author Jorge Rodriguez
 */
@SuppressWarnings("serial")
public class WASFGAMeasures<S extends Solution<?>> extends WASFGA<S> implements Measurable {

    protected CountingMeasure iterations;
    protected DurationMeasure durationMeasure;
    protected SimpleMeasureManager measureManager;
    protected BasicMeasure<List<S>> solutionListMeasure;
//    protected BasicMeasure<Double> hypervolumeValue;
//    protected BasicMeasure<Double> epsilonValue;
//    protected Front referenceFront;
    
	/**
	 * Constructor
	 *
	 * @param problem
	 *            Problem to solve
	 */
	public WASFGAMeasures(Problem<S> problem,
								int populationSize,
								int maxIterations,
								CrossoverOperator<S> crossoverOperator,
								MutationOperator<S> mutationOperator,
								SelectionOperator<List<S>, S> selectionOperator,
								SolutionListEvaluator<S> evaluator,
								List<Double> referencePoint) {

		super(problem,
		      populationSize,
		      maxIterations,
		      crossoverOperator,
		      mutationOperator,
		      selectionOperator,
		      evaluator,
		      referencePoint);
		this.initMeasures();
	}

    @Override
    protected void initProgress() {
        this.iterations.reset();
    }

    @Override
    protected void updateProgress() {
        this.iterations.increment();
//        hypervolumeValue.push(new PISAHypervolume<DoubleSolution>(referenceFront).evaluate(getResult()));
//        epsilonValue.push(new Epsilon<DoubleSolution>(referenceFront).evaluate(getResult()));
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
//        hypervolumeValue = new BasicMeasure<>();
//        epsilonValue = new BasicMeasure<>();

        measureManager = new SimpleMeasureManager();
        measureManager.setPullMeasure("currentExecutionTime", durationMeasure);
        measureManager.setPullMeasure("currentEvaluation", iterations);
//        measureManager.setPullMeasure("hypervolume", hypervolumeValue);
//        measureManager.setPullMeasure("epsilon", epsilonValue);

        measureManager.setPushMeasure("currentPopulation", solutionListMeasure);
        measureManager.setPushMeasure("currentEvaluation", iterations);
//        measureManager.setPushMeasure("hypervolume", hypervolumeValue);
//        measureManager.setPushMeasure("epsilon", epsilonValue);
    }
    
	@Override public String getName() {
		return "WASFGA" ;
	}

	@Override public String getDescription() {
		return "Weighting Achievement Scalarizing Function Genetic Algorithm. Version using Measures" ;
	}

    @Override
    public MeasureManager getMeasureManager() {
        return this.measureManager;
    }
}
