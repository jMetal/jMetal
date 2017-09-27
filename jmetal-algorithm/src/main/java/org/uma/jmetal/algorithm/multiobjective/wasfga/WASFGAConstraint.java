package org.uma.jmetal.algorithm.multiobjective.wasfga;

import org.uma.jmetal.algorithm.multiobjective.gwasfga.util.GWASFGARanking;
import org.uma.jmetal.algorithm.multiobjective.mombi.AbstractMOMBI;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.ASFWASFGA;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.AbstractUtilityFunctionsSet;
import org.uma.jmetal.algorithm.multiobjective.mombi.util.Normalizer;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WASFGARanking;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WASFGARankingConstraint;
import org.uma.jmetal.algorithm.multiobjective.wasfga.util.WeightVector;
import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.SolutionListUtils;
import org.uma.jmetal.util.evaluator.SolutionListEvaluator;
import org.uma.jmetal.util.solutionattribute.Ranking;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the preference based algorithm named WASF-GA on jMetal5.0
 *
 * @author Juanjo Durillo
 *
 *         This algorithm is described in the paper: A.B. Ruiz, R. Saborido, M.
 *         Luque "A Preference-based Evolutionary Algorithm for Multiobjective
 *         Optimization: The Weighting Achievement Scalarizing Function Genetic
 *         Algorithm". Journal of Global Optimization. May 2015, Volume 62,
 *         Issue 1, pp 101-129
 *         DOI = {10.1007/s10898-014-0214-y}
 */
public class WASFGAConstraint<S extends Solution<?>> extends WASFGA<S> {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	final AbstractUtilityFunctionsSet<S> achievementScalarizingUtopia;
	final AbstractUtilityFunctionsSet<S> achievementScalarizingNadir;
	/**
	 * Constructor
	 *
	 * @param problem
	 *            Problem to solve
	 */
	public WASFGAConstraint(Problem<S> problem,
                            int populationSize,
                            int maxIterations,
                            CrossoverOperator<S> crossoverOperator,
                            MutationOperator<S> mutationOperator,
                            SelectionOperator<List<S>, S> selectionOperator,
                            SolutionListEvaluator<S> evaluator,
                            List<Double> referencePoint) {
		super(problem, populationSize, maxIterations, crossoverOperator, mutationOperator, selectionOperator, evaluator,
				referencePoint);
		setMaxPopulationSize(populationSize);

		double [][] weights =  WeightVector.initUniformWeights2D(0.005, getMaxPopulationSize());

		int halfVectorSize = weights.length  / 2;
		int evenVectorsSize    = (weights.length%2==0) ? halfVectorSize : (halfVectorSize+1);
		int oddVectorsSize     = halfVectorSize;

		double [][] evenVectors = new double[evenVectorsSize][getProblem().getNumberOfObjectives()];
		double [][] oddVectors = new double[oddVectorsSize][getProblem().getNumberOfObjectives()];

		int index = 0;
		for (int i = 0; i < weights.length; i = i + 2)
			evenVectors[index++] = weights[i];

		index = 0;
		for (int i = 1; i < weights.length; i = i + 2)
			oddVectors[index++] = weights[i];

		this.achievementScalarizingNadir  =  createUtilityFunction(this.getNadirPoint(), evenVectors);
		this.achievementScalarizingUtopia =  createUtilityFunction(this.getReferencePoint(), oddVectors);

	}

	public AbstractUtilityFunctionsSet<S> createUtilityFunction(List<Double> referencePoint, double [][] weights) {
		weights = WeightVector.invertWeights(weights,true);
		ASFWASFGA<S> aux = new ASFWASFGA<>(weights,referencePoint);

		return aux;
	}
	@Override
	protected Ranking<S> computeRanking(List<S> solutionList) {
		Ranking<S> ranking = new WASFGARankingConstraint<>(this.achievementScalarizingUtopia,this.achievementScalarizingNadir);//WASFGARankingConstraint //GWASFGARanking
		ranking.computeRanking(solutionList);
		return ranking;
	}



	@Override public String getName() {
		return "WASFGAConstraint" ;
	}

	@Override public String getDescription() {
		return "Weighting Achievement Scalarizing Function Genetic Algorithm with Constraints" ;
	}
}
