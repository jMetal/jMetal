/**
 * Copyright 2016 Juan Olmedilla Arregui
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
package org.uma.jmetal.algorithm.singleobjective.simulatedannealing;

import java.util.Comparator;

import org.uma.jmetal.algorithm.impl.AbstractSingleSolutionSequentialAlgorithm;
import org.uma.jmetal.operator.LocalSearchOperator;
import org.uma.jmetal.operator.MutationOperator;
import org.uma.jmetal.operator.impl.localsearch.BasicLocalSearch;
import org.uma.jmetal.operator.impl.localsearch.LocalSearchWithMutationReverse;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.pseudorandom.JMetalRandom;
import org.uma.jmetal.util.solutionattribute.impl.Fitness;

/**
 * Single Objective Simulated Annealing
 * 
 * @author Juan Olmedilla Arregui
 *
 */
public class SimulatedAnnealing<S extends Solution<?>>
        extends AbstractSingleSolutionSequentialAlgorithm<S> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private JMetalRandom randomGenerator = JMetalRandom.getInstance();

    private double t;

    private double alpha;

    private int rounds = 100;

    @SuppressWarnings("serial")
    private class FakeSearchOperator implements LocalSearchOperator<S> {

        private int evaluations = 0;

        private int improvements = 0;

        public void reset() {
            evaluations = 0;
            improvements = 0;
        }

        @Override
        public S execute(S source) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int getEvaluations() {
            return evaluations;
        }

        public void setEvaluations(int amount) {
            evaluations = amount;
        }

        @Override
        public int getNumberOfImprovements() {
            return improvements;
        }

        public void setNumberOfImprovements(int amount) {
            improvements = amount;
        }

        @Override
        public int getNumberOfNonComparableSolutions() {
            // TODO Auto-generated method stub
            return 0;
        }

    }

    private FakeSearchOperator search = new FakeSearchOperator();

    private class AcceptanceProbabilityComparator implements Comparator<S> {
        private Fitness<S> solutionFitness = new Fitness<S>();

        private double t;

        AcceptanceProbabilityComparator(double t) {
            this.t = t;
        }

        @Override
        public int compare(S newSolution, S oldSolution) {
            int result;
            double newFitness = solutionFitness.getAttribute(newSolution);
            double oldFitness = solutionFitness.getAttribute(oldSolution);
            double costDelta = oldFitness - newFitness;
            double a = Math.pow(Math.E, costDelta / t);
            double random = randomGenerator.nextDouble();
            if (newFitness < oldFitness || a > random) {
                result = -1;
            } else {
                result = 1;
            }
            return result;
        }

    }

    /**
     * Base constructor with all parameters. It applies one mutation at a time
     * and if there is no improvement (according to the acceptance probability)
     * it undoes it with the reverse operator. This is meant for problem whose
     * solutions share some information so that collide with each other.
     * 
     */
    public SimulatedAnnealing(MutationOperator<S> mutationOperator,
            MutationOperator<S> reverseOperator, Problem<S> problem, int rounds,
            int targetNumberOfImprovements, int durationInMinutes, double t,
            double alpha) {
        super(targetNumberOfImprovements, mutationOperator, reverseOperator,
                problem, durationInMinutes);
        this.rounds = rounds;
        this.t = t;
        this.alpha = alpha;
    }

    public SimulatedAnnealing(MutationOperator<S> mutationOperator,
            Problem<S> problem, int rounds, int targetNumberOfImprovements,
            int durationInMinutes, double t, double alpha) {
        this(mutationOperator, null, problem, rounds,
                targetNumberOfImprovements, durationInMinutes, t, alpha);
    }

    /**
     * Constructor with standard initial and min termperature and alpha.
     * 
     */
    public SimulatedAnnealing(MutationOperator<S> mutationOperator,
            MutationOperator<S> reverseOperator, Problem<S> problem, int rounds,
            int targetNumberOfImprovements, int durationInMinutes) {
        this(mutationOperator, reverseOperator, problem, rounds,
                targetNumberOfImprovements, durationInMinutes, 1.0, 0.9);
    }

    public SimulatedAnnealing(MutationOperator<S> mutationOperator,
            Problem<S> problem, int rounds, int targetNumberOfImprovements,
            int durationInMinutes) {
        this(mutationOperator, null, problem, rounds,
                targetNumberOfImprovements, durationInMinutes);
    }

    @Override
    public String getDescription() {
        return "Simulated Annealing Hill Climb";
    }

    @Override
    public String getName() {
        return "SA";
    }

    protected LocalSearchOperator<S> getLocalSearch() {
        return search;
    }

    @Override
    public void execute() {
        Fitness<S> solutionFitness = new Fitness<S>();
        S solution = problem.createSolution();
        result = solution;
        problem.evaluate(solution);
        initProgress();
        for (double t_min = 1 - t; t > t_min
                && !isStoppingConditionReached(); t = t * alpha) {
            Comparator<S> comparator = new AcceptanceProbabilityComparator(t);
            LocalSearchOperator<S> localSearch = getLocalSearch(comparator);
            int improvementsForLastChange = 0;
            for (int i = 0; i < rounds && !isStoppingConditionReached(); i++) {
                solution = localSearch.execute(solution);
                if (solutionFitness.getAttribute(solution) < solutionFitness
                        .getAttribute(result)) {
                    result = solution;
                    search.setNumberOfImprovements(
                            localSearch.getNumberOfImprovements() - improvementsForLastChange);
                    improvementsForLastChange = localSearch.getNumberOfImprovements();
                }
                search.setEvaluations(localSearch.getEvaluations());
                updateProgress();
                search.reset();
            }
        }
    }

    private LocalSearchOperator<S> getLocalSearch(Comparator<S> comparator) {
        LocalSearchOperator<S> search;
        if (rollbackOperator != null)
            search = new LocalSearchWithMutationReverse<S>(1, mutationOperator,
                    rollbackOperator, comparator, problem);
        else
            search = new BasicLocalSearch<S>(1, mutationOperator, comparator,
                    problem);
        return search;
    }

}
