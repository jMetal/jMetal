package org.uma.jmetal.algorithm.singleobjective.coralreefsoptimization;

import org.uma.jmetal.operator.CrossoverOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.SelectionOperator;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.AlgorithmBuilder;
import org.uma.jmetal.util.JMetalException;

import java.util.Comparator;
import java.util.List;

/**
 * 
 * @author Inacio Medeiros <inaciogmedeiros@gmail.com>
 *
 */
public class CoralReefsOptimizationBuilder<S extends Solution<?>> implements
		AlgorithmBuilder<CoralReefsOptimization<S>> {

	/**
	 * CoralReefsOptimizationBuilder class
	 */
	private Problem<S> problem;

	private SelectionOperator<List<S>, S> selectionOperator;
	private CrossoverOperator<S> crossoverOperator;
	private MutationOperator<S> mutationOperator;
	private Comparator<S> comparator;

	private int maxEvaluations;
	private int N, M; // Grid sizes
	private double rho; // Percentage of occupied reef
	private double Fbs, Fbr; // Percentage of broadcast spawners and brooders
	private double Fa, Fd; // Percentage of budders and depredated corals
	private double Pd; // Probability of depredation
	private int attemptsToSettle;

	/**
	 * CoralReefsOptimizationBuilder constructor
	 */
	public CoralReefsOptimizationBuilder(Problem<S> problem,
			SelectionOperator<List<S>, S> selectionOperator,
			CrossoverOperator<S> crossoverOperator,
			MutationOperator<S> mutationOperator) {
		this.problem = problem;
		this.selectionOperator = selectionOperator;
		this.crossoverOperator = crossoverOperator;
		this.mutationOperator = mutationOperator;
	}

	public CoralReefsOptimizationBuilder<S> setComparator(
			Comparator<S> comparator) {
		if (comparator == null) {
			throw new JMetalException("Comparator is null!");
		}

		this.comparator = comparator;

		return this;
	}

	public CoralReefsOptimizationBuilder<S> setMaxEvaluations(int maxEvaluations) {
		if (maxEvaluations < 0) {
			throw new JMetalException("maxEvaluations is negative: "
					+ maxEvaluations);
		}
		this.maxEvaluations = maxEvaluations;

		return this;
	}

	public CoralReefsOptimizationBuilder<S> setN(int n) {
		if (n < 0) {
			throw new JMetalException("N is negative: " + n);
		}

		N = n;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setM(int m) {
		if (m < 0) {
			throw new JMetalException("M is negative: " + m);
		}

		M = m;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setRho(double rho) {
		if (rho < 0) {
			throw new JMetalException("Rho is negative: " + rho);
		}

		this.rho = rho;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setFbs(double fbs) {
		if (fbs < 0) {
			throw new JMetalException("Fbs is negative: " + fbs);
		}

		Fbs = fbs;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setFbr(double fbr) {
		if (fbr < 0) {
			throw new JMetalException("Fbr is negative: " + fbr);
		}

		Fbr = fbr;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setFa(double fa) {
		if (fa < 0) {
			throw new JMetalException("Fa is negative: " + fa);
		}

		Fa = fa;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setFd(double fd) {
		if (fd < 0) {
			throw new JMetalException("Fd is negative: " + fd);
		}

		Fd = fd;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setPd(double pd) {
		if (pd < 0) {
			throw new JMetalException("Pd is negative: " + pd);
		}

		Pd = pd;
		return this;
	}

	public CoralReefsOptimizationBuilder<S> setAttemptsToSettle(
			int attemptsToSettle) {
		if (attemptsToSettle < 0) {
			throw new JMetalException("attemptsToSettle is negative: "
					+ attemptsToSettle);
		}

		this.attemptsToSettle = attemptsToSettle;
		return this;
	}

	@Override
	public CoralReefsOptimization<S> build() {
		CoralReefsOptimization<S> algorithm = null;

		algorithm = new CoralReefsOptimization<S>(problem, maxEvaluations,
				comparator, selectionOperator, crossoverOperator,
				mutationOperator, N, M, rho, Fbs, Fa, Pd, attemptsToSettle);

		return algorithm;
	}

	public Problem<S> getProblem() {
		return problem;
	}

	public int getMaxEvaluations() {
		return maxEvaluations;
	}

	public SelectionOperator<List<S>, S> getSelectionOperator() {
		return selectionOperator;
	}

	public CrossoverOperator<S> getCrossoverOperator() {
		return crossoverOperator;
	}

	public MutationOperator<S> getMutationOperator() {
		return mutationOperator;
	}

	public Comparator<S> getComparator() {
		return comparator;
	}

	public int getN() {
		return N;
	}

	public int getM() {
		return M;
	}

	public double getRho() {
		return rho;
	}

	public double getFbs() {
		return Fbs;
	}

	public double getFbr() {
		return Fbr;
	}

	public double getFa() {
		return Fa;
	}

	public double getFd() {
		return Fd;
	}

	public double getPd() {
		return Pd;
	}

	public int getAttemptsToSettle() {
		return attemptsToSettle;
	}

}
