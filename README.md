# jMetal Development Site

**jMetal** is an object-oriented Java-based framework for multi-objective optimization with metaheuristics
(http://jmetal.sourceforge.net).

The current jMetal development version is hosted in this repository; this way, interested users can take a look to
the new incoming features in advance.

After eight years since the first release of jMetal, we have decided it's time to make a deep redesign of the
software. Some of the ideas we are elaborating are:

* Architecture redesign to provide a simpler design while keeping the same functionality.
* Improve code quality:
 * Application of unit testing
 * Better use of Java features (e.g, generics)
 * Design patterns
 * Application of clean code guidelines - “Clean code: A Handbook of Agile Software Craftsmanship" (Robert C. Martin)
* Algorithm templates
* Parallelism support
* Maven is used as the tool for development, testing, packaging and deployment.

If you are interested in contributing with your ideas and comments, please take a look the current discussions in the [Issues section](https://github.com/jMetal/jMetal/issues).

<div id='id-features'/>

##Components
The current development version provides:

* Algorithms: NSGA-II, NSGA-III (to be tested), SMPSO, GDE3, IBEA, MOEA/D (variants: MOEA/D-DE, MOEA/D-DRA, Constrained MOEA/D), SPEA2, PESA2, AbYSS, MOCell, RandomSearch.
* Encodings: Binary, Real, Integer, Permutation, Integer+Double
* Crossover operators: SBXCrossover, SinglePointCrossover, DifferentialEvolutionCrossover, IntegerSBXCrossover, BLXAlphaCrossover, HUXCrossover
* Mutation operators: PolynomialMutation, SingleRandomMutation, BitFlipMutation, IntegerPolynomialMutation, UniformMutation, NonUniformMutation
* Selection operators:  BinaryTournamentSelection, DifferentialEvolutionSelection, RankingAndCrowdingSelection, NaryTournamentSelection
* Problems: Fonseca, Kursawe, Schaffer, Srinivas, OneZeroMax, NMMin, NMIN2, ZDT benchrmark, DTLZ benchmark, WFG benchmark, LZ09 benchmark, multiobjective TSP
* Qualitity indicators: Epsilon, Hypervolume, error ratio, spread, generalized spread, generational distance, inverted generational distance, set coverage.

<div id='id-maven'/>
##jMetal is available as a Maven Project in The Central Repository

The link to the modules is: https://search.maven.org/#search%7Cga%7C1%7Cjmetal

##jMetal user manual
The user manual is hosted in https://github.com/jMetal/jMetalUserManual

####Code coverage (25th January 2015)

|Class % |Method %| Line % |
|--------|--------|--------|
|17,8% (48/ 270) |	16,8% (243/ 1443) |	13,4% (1199/ 8975)

