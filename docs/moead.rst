Algorithm MOEA/D
================
MOEA/D (Multiobjective Evolutionary Algorithm Based on Decomposition) is the most representative decomposition based multiobjective evolutionary algorithm. It was presented in 2007 in the paper `[ZL07] <https://doi.org/10.1109/TEVC.2007.892759>`_, being MOEA/D-DE `[LZ09] <https://doi.org/10.1109/TEVC.2008.925798>`_ probably the most well-known used version. Implementations of both versions are included in classes `org.uma.jmetal.algorithm.multiobjective.nsgaii.MOEAD <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/moead/MOEAD.java>`_ and `org.uma.jmetal.algorithm.multiobjective.nsgaii.MOEAD <https://github.com/jMetal/jMetal/tree/master/jmetal-algorithm/src/main/java/org/uma/jmetal/algorithm/multiobjective/moead/MOEADDE.java>`_, respectively. 


Both classes have these features:

* The implementation follows a component based scheme, where all the elements of a generic evolutionary algorithm (selection, variation, replacement, evaluation, termination) are abstract and they can be injected when configuring the algorithm.
* The class is an observable entity following the Observer Pattern. This allows external observer entities to register into the algorithm and be notified with changes are produced. In particular, at the end of each iteration, NSGA-II notifies a map with data about the current number of evaluations, computing time, and population. 
* The stopping condition is defined with a `Termination <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/component/termination/Termination.java>`_ object. This way, different stopping conditions can be defined: maximum number of evaluations, maximum computing time, or when a keyboard key is pressed.

Examples of different configurations of MOEA/D are included in the `jmetal-example <https://github.com/jMetal/jMetal/tree/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/moead>`_ package. Currently, all of them are applied to solve continuous problems:

* ``MOEADDefaultConfigurationExample``. MOEA/D with commonly used settings. 
* ``MOEADDefaultConfiguration3DProblemExample``. Example solving a three-objective problem.
* ``MOEADStoppingByTimeExample``. MOEA/D stopping by fixing a maximum computing time.
* ``MOEADStoppingByKeyboardExample``. MOEA/D stopping when the user presses any key.
* ``MOEADWithRealTimeChartExample``. Example of using an observer object that plots the current population at the end of each iteration.
* ``MOEADWithChartExample``. Example of plotting the final population using Smile.
* ``MOEADWithPlotliExample``. Example of plotting the final population using Tablesaw.
* ``MOEAD3DProblemWithChartExample``. Example solving a three objective problem, plotting the current front (functions f1 and f2) in a 2D projection in real time, and the full front at the end of the computation.
* ``MOEADDEDefaultConfigurationExample``. Example of using the `MOEADDE` class.

The former implementation and variants of MOEA/D in jMetal 5.x are available in the `jmetal5version <https://github.com/jMetal/jMetal/tree/master/jmetal-example/src/main/java/org/uma/jmetal/example/multiobjective/moead/jmetal5version>`_ directory of the MOEA/D examples package.  

