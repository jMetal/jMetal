.. _dynamic:

Dynamic optimization
====================

:Author: Antonio J. Nebro
:Date: 2026-11-19

A dynamic multi-objective problem changes during the search: its Pareto front, its Pareto set or both
depend on a time instant ``t``. jMetal includes dynamic problems, a loop that runs a metaheuristic on
them and reacts to their changes, a dynamic NSGA-II and InDM2, an interactive algorithm in which a
decision maker guides the search with reference points while the problem changes. They are in the
classic style (``jmetal-algorithm``) and need no streaming framework: the changes come from the
algorithm itself or from plain Java threads.

Dynamic problems
----------------

A dynamic problem implements ``DynamicProblem<S, Integer>``: ``update(counter)`` sets the time instant
from a counter (the generation of the algorithm, or a clock), and ``hasChanged()`` and
``clearChanged()`` tell the algorithm whether the problem has changed since it last reacted. The
problems are marked as changed only when the time instant changes.

* ``fda``: FDA1-FDA5 of Farina, Deb and Amato (IEEE TEVC, 2004), with ``t = floor(tau / tau_T) /
  n_T`` (``tau_T = 5`` and ``n_T = 10`` by default), and ``FDA2Mod``, the redesign of FDA2 by Mehnen,
  Wagner and Rudolph (2006).
* ``df``: DF1-DF14 of the CEC 2018 Competition on Dynamic Multiobjective Optimisation (Jiang et al.,
  2017), with ``t = floor(tau / tau_t) / n_t`` (``tau_t = 10`` and ``n_t = 10`` by default). The
  Javadoc of ``DF`` lists where the official MATLAB code and pymoo differ from the technical report,
  which these classes follow.

The dynamic algorithm
---------------------

``DynamicAlgorithm`` (package ``org.uma.jmetal.algorithm.dynamic``) runs a ``StepwiseAlgorithm``, a
metaheuristic that can be run one iteration at a time, on a dynamic problem:

.. code-block:: text

  initialize the population
  while the stopping condition is not met and no stop has been requested:
    run one iteration
    if a round of iterations has finished: publish the front to the observers if it is
        significantly different from the last one published
    update the time of the problem
    react to other external events (e.g. new reference points in InDM2)
    if the problem has changed: restart the search

* **Restarts.** The search is restarted only when the problem changes: a ``RestartStrategy`` removes
  part of the population and creates new solutions (``RemoveNRandomSolutions``,
  ``RemoveNSolutionsAccordingToTheHypervolumeContribution``, ``CreateNRandomSolutions``...), and the
  whole population is evaluated again, because the objectives of the solutions that remain are no
  longer valid. The stepwise algorithms also reset the state that depends on the old objectives
  (archives of leaders, memories, ideal and nadir points).
* **Time of the problem.** With ``timeDrivenByIterations(true)`` the algorithm passes its iteration
  counter to the problem after each iteration, which is the protocol of the FDA and CEC 2018
  benchmarks. Otherwise the time only changes with ``updateProblem(counter)``, which any thread can
  call (a clock, a data stream); the updates are queued and applied between iterations, so the problem
  never changes in the middle of an evaluation. ``PeriodicProblemUpdater`` is a clock that does it
  periodically.
* **Rounds.** A round is a fixed number of iterations, which a restart does not reset. At the end of
  each round the front is sent to the observers if there is no previous one, or if its distance to the
  last one sent (``frontChangeThreshold(indicator, threshold)``, IGD by default) is greater than the
  threshold; with a threshold of 0 every round is sent. The front is sent before the changes of that
  iteration are applied, so it is always the front of the problem with which it was computed. With
  ``notifyProblemChanges(true)`` the observers also receive the front just before each restart.
* **Stopping.** The run ends when a ``DynamicStoppingCondition`` is met (``afterIterations``,
  ``afterEvaluations``, ``afterRounds``, ``afterProblemChanges``, ``afterComputingTime``, combined with
  ``or``; ``never()`` by default), or when ``requestStop()`` is called from any thread (a button of a
  user interface, an endpoint of a web application). The algorithm ends the current iteration, and
  ``result()`` gives the current front.
* **Counting evaluations.** ``afterEvaluations`` counts all the evaluations, including the
  re-evaluations of the population after each change, so frequent changes leave less budget for the
  search. ``afterIterations`` counts generations, as the CEC 2018 protocol, whose budget of
  ``100 (30 tau_t + 50)`` evaluations is ``30 tau_t + 50`` generations of 100 solutions, and
  ``afterProblemChanges(30)`` reproduces its 30 changes per run.

The observers receive a map with the keys ``EVENT`` (``ROUND_END``, ``PROBLEM_CHANGE`` or, in InDM2,
``REFERENCE_POINTS_CHANGE``), ``POPULATION`` (copies of the solutions of the front), ``EVALUATIONS``,
``ITERATIONS``, ``ROUNDS``, ``PROBLEM_CHANGES``, ``COMPUTING_TIME`` and, in InDM2,
``REFERENCE_POINTS``. ``RunTimeForDynamicProblemsChartObserver`` draws the fronts and the reference
points.

Dynamic NSGA-II
---------------

``DynamicNSGAII`` runs NSGA-II with the time of the problem driven by the generations, and ends after
30 changes of the problem by default (``DynamicNSGAIIRunner``):

.. code-block:: java

  var problem = new FDA2(31, 2, 50, 10);  // the time changes every 50 generations
  var algorithm =
      new DynamicNSGAII<DoubleSolution>(
          problem, 100, 100, new SBXCrossover(0.9, 20.0),
          new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
          new BinaryTournamentSelection<>(), new SequentialSolutionListEvaluator<>(),
          new DefaultRestartStrategy<>(
              new RemoveNRandomSolutions<>(50), new CreateNRandomSolutions<>()),
          50);  // generations per round
  algorithm.run();

InDM2
-----

InDM2 (A. J. Nebro, A. B. Ruiz, C. Barba-González, J. García-Nieto, M. Luque, J. F. Aldana-Montes,
"InDM2: Interactive Dynamic Multi-Objective Decision Making Using Evolutionary Algorithms", Swarm and
Evolutionary Computation 40:184-195, 2018) runs a reference point-based algorithm on a dynamic problem
and lets the decision maker change the reference points during the run, after looking at the
approximations of the region of interest. It was implemented in jMetalSP on Apache Spark; this version
uses Java threads only.

* **Algorithms.** ``StepwiseWASFGA`` (one reference point), ``StepwiseRNSGAII`` (one or more) and
  ``StepwiseSMPSORP`` (one per archive of leaders).
* **Reference points.** ``updateReferencePoints(points)`` can be called from any thread; the
  algorithm takes the points after the current iteration, restarts the search with its strategy for
  reference point changes (only once if the problem changes at the same time) and notifies the
  observers. ``KeyboardReferencePointReader`` reads them from the console ("0.2, 0.5" for one point
  with two objectives, "q" to stop).
* **Defaults** (``InDM2Builder``, Table 1 of the paper): when the problem changes, half of the
  population is removed according to its contribution to the hypervolume and replaced by random
  solutions; when the reference points change, the whole population is replaced; rounds of 250
  iterations; the run ends only with ``requestStop()``.

.. code-block:: java

  var problem = new FDA2();
  var wasfga =
      new StepwiseWASFGA<DoubleSolution>(
          problem, 50, new SBXCrossover(0.9, 20.0),
          new PolynomialMutation(1.0 / problem.numberOfVariables(), 20.0),
          new BinaryTournamentSelection<>(new RankingAndCrowdingDistanceComparator<>()),
          new SequentialSolutionListEvaluator<>(), 0.01, List.of(0.0, 0.0));

  InDM2<DoubleSolution> indm2 =
      new InDM2Builder<>(wasfga, problem, 50)
          .setIterationsPerRound(500)  // 25,000 evaluations
          .setFrontChangeThreshold(new InvertedGenerationalDistance(), 0.005)
          .build();

  var chart = new RunTimeForDynamicProblemsChartObserver<DoubleSolution>(indm2.name(), 80);
  chart.setReferencePointList(List.of(List.of(0.0, 0.0)));
  indm2.observable().register(chart);

  try (var clock = new PeriodicProblemUpdater(indm2, 1000).start()) {  // a counter every second
    new KeyboardReferencePointReader(indm2).start();
    indm2.run();  // ends when "q" is typed
  }

The examples ``InDM2WithWASFGARunner``, ``InDM2WithRNSGAIIRunner`` and ``InDM2WithSMPSORPRunner``
(package ``org.uma.jmetal.algorithm.examples.multiobjective.indm2``) run it on FDA2, FDA3 and DF1.

Compared with the version of jMetalSP, this one can be stopped, applies the changes between
iterations instead of while the population is being evaluated, restarts the population once (not
twice) when the reference points change, and does not restart it at the end of every round, which
Algorithm 1 of the paper does not do either. The experiments of the paper used the FDA2 of jMetalSP,
which is ``FDA2Mod``; its Eq. (5) describes the original FDA2, with 0.75 instead of 0.7 in ``H(t)``.
