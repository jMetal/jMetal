# jMetal Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
(http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

After eight years since the first release of jMetal, we have decided it's time to make a deep redesign of the
software. Some of the ideas we are elaborating are:
* Maven is used as the tool for development, testing, packaging and deployment.
* The encoding takes into account the recommendations provided in “Clean code: A Handbook of Agile Software Craftsmanship" (Robert C. Martin)
* The Fluent Interface (http://martinfowler.com/bliki/FluentInterface.html) is applied to configure and execute
the algorithms.
* We will incorporate progressively unit tests to all the classes.

If you are interested in contributing with your ideas and comments, please take a look the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

Features of the current development version:
* Algorithms: NSGA-II, SMPSO, GDE3, IBEA (corrected version)
* Encodings: Binary, Real, Integer, Permutation, Integer+Double
* Crossover operators: SBXCrossover, SinglePointCrossover, DifferentialEvolutionCrossover, IntegerSBXCrossover
* Mutation operators: PolynomialMutation, SingleRandomMutation, BitFlipMutation, IntegerPolynomialMutation
* Selection operators:  BinaryTournamentSelection, DifferentialEvolutionSelection
* Problems: Fonseca, Kursawe, Schaffer, Srinivas, OneZeroMax, NMMin, NMIN2, ZDT family



