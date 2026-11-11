package org.uma.jmetal.component.examples.multiobjective.nsgaii;

import java.util.List;
import java.util.Map;
import org.uma.jmetal.component.algorithm.EvolutionaryAlgorithm;
import org.uma.jmetal.component.algorithm.multiobjective.NSGAIIBuilder;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByMutualDominationRate;
import org.uma.jmetal.operator.crossover.impl.SBXCrossover;
import org.uma.jmetal.operator.mutation.impl.PolynomialMutation;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.problem.ProblemFactory;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.observable.Observable;
import org.uma.jmetal.util.observer.Observer;

/**
 * Class to run NSGA-II with a fixed budget and to use the adaptive stopping criterion MGBM
 * ({@link TerminationByMutualDominationRate}) only as a warning: an observer passes it the status of
 * each iteration and tells the user when the search seems to have stagnated, so that the user, who
 * can be watching the evolution of the front, decides whether to stop it (for example, with a
 * {@link org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByUserRequest}). The
 * criterion never stops the run.
 *
 * @author Antonio J. Nebro
 */
public class NSGAIIWithStagnationWarningExample {

  /** Observer that warns once when the population stops improving. */
  static class StagnationWarning implements Observer<Map<String, Object>> {
    private final TerminationByMutualDominationRate stagnationDetector =
        new TerminationByMutualDominationRate(0.05, 0.05, 0.0, 0.01);
    private boolean warned = false;

    @Override
    public void update(Observable<Map<String, Object>> observable, Map<String, Object> data) {
      if (stagnationDetector.isMet(data) && !warned) {
        warned = true;
        JMetalLogger.logger.info(
            "No significant improvement detected after "
                + data.get("EVALUATIONS")
                + " evaluations: the search can be stopped");
      }
    }
  }

  public static void main(String[] args) {
    String problemName = "org.uma.jmetal.problem.multiobjective.zdt.ZDT1";
    Problem<DoubleSolution> problem = ProblemFactory.<DoubleSolution>loadProblem(problemName);

    var crossover = new SBXCrossover(0.9, 20.0);
    var mutation = new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0);

    EvolutionaryAlgorithm<DoubleSolution> nsgaii =
        new NSGAIIBuilder<>(problem, 100, 100, crossover, mutation)
            .setTermination(new TerminationByEvaluations(100000))
            .build();
    nsgaii.observable().register(new StagnationWarning());

    nsgaii.run();

    List<DoubleSolution> population = nsgaii.result();
    JMetalLogger.logger.info(
        "Finished after " + nsgaii.numberOfEvaluations() + " evaluations with "
            + population.size() + " solutions");
  }
}
