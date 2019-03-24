package org.uma.jmetal.operator.impl.localsearch;

import java.util.Comparator;
import java.util.NoSuchElementException;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

@SuppressWarnings("serial")
public class LocalBestSearch<S extends Solution<?>>
        implements LocalSearchOperator<S> {

    private Problem<S> problem;
    private Comparator<S> constraintComparator;
    private Comparator<S> comparator;

    private MutationOperator<S> mutationOperator;
    private MutationOperator<S> rollbackOperator;
    private int evaluations;
    private int numberOfImprovements;

    private int numberOfNonComparableSolutions;
    private MutationOperator<S> commitOperator;

    /**
     * Constructor. Creates a new local search object.
     * 
     * @param mutationOperator
     *            mutation operator
     * @param comparator
     *            comparator to determine which solution is the best
     * @param problem
     *            problem to resolve
     * 
     */
    public LocalBestSearch(MutationOperator<S> mutationOperator,
            MutationOperator<S> rollbackOperator,
            MutationOperator<S> commitOperator, Comparator<S> comparator,
            Problem<S> problem) {
        this.problem = problem;
        this.mutationOperator = mutationOperator;
        this.rollbackOperator = rollbackOperator;
        this.commitOperator = commitOperator;
        this.comparator = comparator;
        constraintComparator = new OverallConstraintViolationComparator<S>();

        numberOfImprovements = 0;
    }

    /**
     * Executes the local search.
     * 
     * @param solution
     *            The solution to improve
     * @return An improved solution
     */
    @SuppressWarnings("unchecked")
    public S execute(S solution) {
        int best;
        evaluations = 0;
        numberOfNonComparableSolutions = 0;

        boolean allNextSolutionsExplored = false;
        do {
            try {
                S mutatedSolution = mutationOperator
                        .execute((S) solution.copy());
                if (problem.getNumberOfConstraints() > 0) {

                    ((ConstrainedProblem<S>) problem)
                            .evaluateConstraints(mutatedSolution);
                    best = constraintComparator.compare(mutatedSolution,
                            solution);
                    if (best == 0) {
                        problem.evaluate(mutatedSolution);
                        evaluations++;
                        best = comparator.compare(mutatedSolution, solution);
                    } else if (best == -1) {
                        problem.evaluate(mutatedSolution);
                        evaluations++;
                    }
                } else {
                    problem.evaluate(mutatedSolution);
                    evaluations++;
                    best = comparator.compare(mutatedSolution, solution);
                }

                if (best == -1) {
                    solution = mutatedSolution;
                    // We want to keep the best, but since we are rollbacking
                    // we always keep the count at 1 after the first one
                    numberOfImprovements = 1;
                } else if (best != 1) {
                    numberOfNonComparableSolutions++;
                }
                rollbackOperator.execute(mutatedSolution);
            } catch (NoSuchElementException e) {
                allNextSolutionsExplored = true;
                commitOperator.execute(solution);
            }
        } while (!allNextSolutionsExplored);
        return (S) solution;
    }

    /**
     * Returns the number of evaluations
     */
    public int getEvaluations() {
        return evaluations;
    }

    @Override
    public int getNumberOfImprovements() {
        return numberOfImprovements;
    }

    @Override
    public int getNumberOfNonComparableSolutions() {
        return numberOfNonComparableSolutions;
    }
}
