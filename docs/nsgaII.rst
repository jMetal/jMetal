Algorithm NSGA-II
=================
NSGA-II is the most well-known and used multiobjective metaheuristic. It was presented in 2002 in the paper `[DPA02] <https://doi.org/10.1109/4235.996017>`_. It is a generational genetic algorithm characterized by adopting a non-dominated sorting ranking scheme and a crowding density based density estimator. Information about the algorithm and their parameters is included in section :ref:`autoconfiguration`.

The implementation of NSGA-II in jMetal 6 (contained in class `org.uma.jmetal.algorithm.multiobjective.nsgaii <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/nsgaii>`_ has the following features:

* The class is an observable entity following the Observer Pattern. This allows external observer entities to register into the algorithm and be notified with changes are produced. In particular, at the end of each iteration, NSGA-II notifies a map with data about the current number of evaluations, 
computing time, and population. 
