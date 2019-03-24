package org.uma.jmetal.algorithm.singleobjective.steepestascenthillclimb;

import java.util.Comparator;

import org.uma.jmetal.algorithm.impl.AbstractHillClimb;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.localsearch.LocalBestSearch;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.comparator.FitnessComparator;

public class SteepestAscentHillClimb<S extends Solution<?>>
        extends AbstractHillClimb<S> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private MutationOperator<S> commitOperator;

    public SteepestAscentHillClimb(int mutations,
            MutationOperator<S> mutationOperator,
            MutationOperator<S> rollbackOperator,
            MutationOperator<S> commitOperator, Problem<S> problem,
            int durationInMinutes) {
        super(mutations, mutationOperator, rollbackOperator, problem,
                durationInMinutes);
        this.commitOperator = commitOperator;
    }

    @Override
    protected LocalSearchOperator<S> getNewLocalSearch() {
        LocalSearchOperator<S> search;
        Comparator<S> comparator = new FitnessComparator<S>();
        search = new LocalBestSearch<S>(mutationOperator, rollbackOperator,
                commitOperator, comparator, problem);
        return search;
    }

    @Override
    public String getDescription() {
        return "Steepest Ascent Hill Climb";
    }

    @Override
    public String getName() {
        return "HCS";
    }

    public boolean isStoppingConditionReached() {
        boolean didNotImprove = evaluations.get() > 1
                && getLocalSearch().getNumberOfImprovements() == 0;
        return super.isStoppingConditionReached() || didNotImprove;
    }

}
