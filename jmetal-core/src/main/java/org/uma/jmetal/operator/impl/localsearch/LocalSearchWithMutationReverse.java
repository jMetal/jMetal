package org.uma.jmetal.operator.impl.localsearch;

import java.util.Comparator;

import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.impl.OverallConstraintViolationComparator;

/**
 * @author Juan Olmedilla Arregui
 *
 */
@SuppressWarnings("serial")
public class LocalSearchWithMutationReverse<S extends Solution<?>>
        implements LocalSearchOperator<S> {
    private Problem<S> problem;
    private int improvementRounds;
    private Comparator<S> constraintComparator;
    private Comparator<S> comparator;

    private MutationOperator<S> mutationOperator;
    private MutationOperator<S> rollbackOperator;
    private int evaluations;
    private int numberOfImprovements;

    private int numberOfNonComparableSolutions;

    /**
     * Constructor. Creates a new local search object.
     * 
     * @param improvementRounds
     *            number of iterations
     * @param mutationOperator
     *            mutation operator
     * @param comparator
     *            comparator to determine which solution is the best
     * @param problem
     *            problem to resolve
     * 
     */
    public LocalSearchWithMutationReverse(int improvementRounds,
            MutationOperator<S> mutationOperator, 
            MutationOperator<S> rollbackOperator,
            Comparator<S> comparator,
            Problem<S> problem) {
        this.problem = problem;
        this.mutationOperator = mutationOperator;
        this.rollbackOperator = rollbackOperator;
        this.improvementRounds = improvementRounds;
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
        int i = 0;
        int best;
        evaluations = 0;
        numberOfNonComparableSolutions = 0;

        int rounds = improvementRounds;

        while (i < rounds) {
            S mutatedSolution = mutationOperator.execute((S) solution.copy());
            if (problem.getNumberOfConstraints() > 0) {

                ((ConstrainedProblem<S>) problem)
                        .evaluateConstraints(mutatedSolution);
                best = constraintComparator.compare(mutatedSolution, solution);
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
                numberOfImprovements++;
            } else if (best != 1) {
                    numberOfNonComparableSolutions++;
            }
            i++;
        }
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
