.. _operators:

Operators
=========

:Author: Antonio J. Nebro
:Date: 2025-07-07

Operators are the building blocks of evolutionary algorithms in jMetal. Each operator is a function
typically applied to one or more solutions, producing one or more new solutions. The source and result
are typically a solution or a list of solutions, depending on the operator.

Operator Types
-------------

jMetal provides four main types of operators:

1. **Crossover**: Recombines parent solutions to produce offspring solutions.
2. **Mutation**: Modifies a single solution to introduce diversity.
3. **Selection**: Chooses solutions from a population based on some criteria.
4. **Local Search**: Applies local search techniques to improve solutions.

Crossover Operators
------------------
Crossover operators combine genetic information from parent solutions to create new offspring solutions.

For Double Solutions
~~~~~~~~~~~~~~~~~~~

- **SBXCrossover**: Simulated Binary Crossover with polynomial distribution.
- **BLXAlphaCrossover**: Blend Crossover with alpha parameter.
- **BLXAlphaBetaCrossover**: Extended BLX with separate alpha and beta parameters.
- **DifferentialEvolutionCrossover**: Used in Differential Evolution algorithms.
- **ArithmeticCrossover**: Weighted arithmetic mean of parent solutions.
- **LaplaceCrossover**: Based on Laplace distribution.
- **WholeArithmeticCrossover**: Uses all parents in arithmetic recombination.
- **FuzzyRecombinationCrossover**: Implements fuzzy recombination.
- **UnimodalNormalDistributionCrossover**: Uses unimodal normal distribution.

For Integer Solutions
~~~~~~~~~~~~~~~~~~~~

- **IntegerSBXCrossover**: Integer version of SBX.
- **IntegerPolynomialCrossover**: Polynomial crossover.

For Binary Solutions
~~~~~~~~~~~~~~~~~~~

- **HUXCrossover**: Half Uniform Crossover.
- **SinglePointCrossover**: One-point crossover.
- **TwoPointCrossover**: Two-point crossover.
- **NPointCrossover**: General N-point crossover.
- **UniformCrossover**: Random bit selection from parents.

For Permutation Solutions
~~~~~~~~~~~~~~~~~~~~~~~~

- **PMXCrossover**: Partially Mapped Crossover.
- **CycleCrossover**: Preserves absolute positions.
- **PositionBasedCrossover**: Preserves relative ordering.
- **OXDCrossover**: Order-based crossover.
- **EdgeRecombinationCrossover**: Preserves adjacency information.

Mutation Operators
-----------------
Mutation operators introduce random changes to solutions to maintain diversity.

For Double Solutions
~~~~~~~~~~~~~~~~~~~

- **PolynomialMutation**: Polynomial mutation.
- **NonUniformMutation**: Strength decreases over time.
- **UniformMutation**: Random mutation within bounds.
- **LevyFlightMutation**: Uses Lévy flights.
- **PowerLawMutation**: Based on power law distribution.
- **SimpleRandomMutation**: Uniform random mutation.

For Integer Solutions
~~~~~~~~~~~~~~~~~~~~

- **IntegerPolynomialMutation**: Integer version of polynomial mutation.
- **SimpleRandomMutation**: Random perturbation of values.

For Binary Solutions
~~~~~~~~~~~~~~~~~~~

- **BitFlipMutation**: Flips each bit with given probability.

For Permutation Solutions
~~~~~~~~~~~~~~~~~~~~~~~~

- **SwapMutation**: Swaps two elements.
- **InsertMutation**: Moves element to new position.
- **ScrambleMutation**: Reorders subsequence.
- **InversionMutation**: Inverts subsequence order.
- **DisplacementMutation**: Moves subsequence.
- **SimpleInversionMutation**: Inverts two elements.

Selection Operators
------------------

- **BinaryTournamentSelection**: Better of two random solutions.
- **NaryTournamentSelection**: Best of N random solutions.
- **RankingAndCrowdingSelection**: NSGA-II selection.
- **RandomSelection**: Uniform random selection.
- **BestSolutionSelection**: Best in population.
- **RouletteWheelSelection**: Fitness-proportional.
- **StochasticUniversalSampling**: Improved roulette wheel.

Local Search Operators
---------------------

- **BasicLocalSearch**: Improves solutions locally.

Using Operators in jMetal
------------------------

Example of creating and using a crossover operator:

.. code-block:: java

   // Create a SBX crossover operator with probability 0.9 and distribution index 20.0
   double crossoverProbability = 0.9;
   double distributionIndex = 20.0;
   CrossoverOperator<DoubleSolution> crossover = new SBXCrossover(crossoverProbability, distributionIndex);
   
   // Apply crossover to two parent solutions
   List<DoubleSolution> parents = new ArrayList<>();
   parents.add(parent1);
   parents.add(parent2);
   List<DoubleSolution> offspring = crossover.execute(parents);

Example of creating and using a mutation operator:

.. code-block:: java

   // Create a polynomial mutation operator with probability 0.1 and distribution index 20.0
   double mutationProbability = 0.1;
   double distributionIndex = 20.0;
   MutationOperator<DoubleSolution> mutation = new PolynomialMutation(mutationProbability, distributionIndex);
   
   // Apply mutation to a solution
   DoubleSolution mutatedSolution = mutation.execute(solution);

Choosing the Right Operator
--------------------------

The choice of operators depends on several factors:

1. **Solution Encoding**: Match the operator to your solution representation.
2. **Problem Characteristics**: Some operators work better for certain problems.
3. **Diversity vs. Intensification**: Balance exploration and exploitation.
4. **Computational Cost**: Consider the complexity of the operator.