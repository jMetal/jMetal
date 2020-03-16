Algorithm NSGA-II
=================
NSGA-II is the most well-known and used multiobjective metaheuristic. It was presented in 2002 in the paper `[DPA02] <https://doi.org/10.1109/4235.996017>`_. It is a generational genetic algorithm characterized by adopting a non-dominated sorting ranking scheme and a crowding density based density estimator. Information about the algorithm and their parameters is included in section :ref:`autoconfiguration`.

The implementation of NSGA-II in jMetal 6 (contained in class `org.uma.jmetal.algorithm.multiobjective.nsgaii <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/nsgaii>`_ has the following features:

* The class is an observable entity following the Observer Pattern. This allows external observer entities to register into the algorithm and be notified with changes are produced. In particular, at the end of each iteration, NSGA-II notifies a map with data about the current number of evaluations, computing time, and population. 
* The offspring population size is a parameter that can be set. By default, that value is the same of the population size, but by changing it we can promote the diversification (higher values) or the intensification (lower values) of the search. In particular, a steady-state version of NSGA-II is configured with an offspring population equals to 1.
* The stopping condition is defined with a `Termination <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/component/termination/Termination.java>`_ object. This way, different stopping conditions can be defined: maximum number of evaluations, maximum computing time, or when a keyboard key is pressed.
* Different ranking implementations can be used. This allows, for example, to incorporate a preferente articulation mechanism in NSGA-II by using the concept of g-dominance.

Examples of different configurations of NSGA-II are included in the `jmetal-example <https://github.com/jMetal/jMetal/tree/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii>`_ package. The examples include:

* ``NSGAIIDefaultConfiguratoinExample``. NSGA-II with default settings for solving bi-objective benchmark problems.
* ``NSGAIISteadyStateExample``. Steady-stater NSGA-II.
* ``NSGAIIStoppingByTimeExample``. NSGA-II stopping by fixing a maximum computing time.
* ``NSGAIIStoppingByKeyboardExample``. NSGA-II stopping when the user presses any key.
* ``NSGAIIWithRealTimeChartExample``. Example of using an observer object that plots the current population at the end of each iteration.
* ``NSGAIIWithChartExample``. Example of plotting the final population using Smile.
* ``NSGAIIWithPlotliExample``. Example of plotting the final population using Tablesaw.
* ``GNSGAIIExample``. NSGA-II using g-dominance to focus the search on a region of interest indicated by a reference point. The current population and the reference point are plotted while the algorithm is running.
* ``NSGAIIBinaryProblemExample``. NSGA-II configured to solved a binary encoded problem.
* ``ParallelNSGAIIExample``. NSGA-II using a multithreaded evaluator, which evaluates the population in parallel using threads.

The former implementation and variants of NSGA-II in jMetal 5.x are available in the `jmetal5version <https://github.com/jMetal/jMetal/tree/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/nsgaii/jmetal5version>`_ directory of the NSGA-II examples package.  

