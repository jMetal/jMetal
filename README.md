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

* Algorithms: NSGA-II, SPEA2, NSGA-III (to be tested), SMPSO, GDE3, IBEA (corrected version), MOEA/D (variants: MOEA/D-DE, MOEA/D-DRA, Constrained MOEA/D).
* Encodings: Binary, Real, Integer, Permutation, Integer+Double
* Crossover operators: SBXCrossover, SinglePointCrossover, DifferentialEvolutionCrossover, IntegerSBXCrossover, BLXAlphaCrossover, HUXCrossover
* Mutation operators: PolynomialMutation, SingleRandomMutation, BitFlipMutation, IntegerPolynomialMutation, UniformMutation, NonUniformMutation
* Selection operators:  BinaryTournamentSelection, DifferentialEvolutionSelection, RankingAndCrowdingSelection, NaryTournamentSelection
* Problems: Fonseca, Kursawe, Schaffer, Srinivas, OneZeroMax, NMMin, NMIN2, ZDT benchrmark, DTLZ benchmark, WFG benchmark, LZ09 benchmark 
* Qualitity indicators: Epsilon, Hypervolume, error ratio, spread, generalized spread, generational distance, inverted generational distance, set coverage.

##jMetal is available as a Maven Project in The Central Repository

The link to the modules is: https://search.maven.org/#search%7Cga%7C1%7Cjmetal

###Code coverage (25th January 2015)

|Class % |Method %| Line % |
|--------|--------|--------|
|17,8% (48/ 270) |	16,8% (243/ 1443) |	13,4% (1199/ 8975)
