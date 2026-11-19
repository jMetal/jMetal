package org.uma.jmetal.algorithm.dynamic;

/**
 * The state of a run of a {@link DynamicAlgorithm}, which its stopping condition receives.
 *
 * @param iterations the iterations (generations) done
 * @param evaluations all the evaluations done, including the re-evaluations of the population after
 *     a change of the problem or of the reference points
 * @param rounds the rounds completed (a round is a fixed number of iterations, at the end of which
 *     the front can be published)
 * @param problemChanges the changes of the problem to which the algorithm has reacted
 * @param computingTime the milliseconds since the run started
 */
public record DynamicStatus(
    long iterations, long evaluations, int rounds, int problemChanges, long computingTime) {}
