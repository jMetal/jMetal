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

- **SBXCrossover**: Simulated Binary Crossover with polynomial distribution. Good for real-valued optimization.
- **BLXAlphaCrossover**: Blend Crossover with alpha parameter controlling exploration.
- **BLXAlphaBetaCrossover**: Extended BLX with separate alpha and beta parameters for more control.
- **DifferentialEvolutionCrossover**: Used in Differential Evolution algorithms.
- **ArithmeticCrossover**: Weighted arithmetic mean of parent solutions.
- **LaplaceCrossover**: Based on Laplace distribution for real-coded variables.
- **WholeArithmeticCrossover**: Uses all parents in arithmetic recombination.

For Integer Solutions
~~~~~~~~~~~~~~~~~~~~

- **IntegerSBXCrossover**: Integer version of SBX for integer variables.
- **IntegerPolynomialCrossover**: Polynomial crossover for integer variables.

For Binary Solutions
~~~~~~~~~~~~~~~~~~~

- **HUXCrossover**: Half Uniform Crossover, preserves half of the differing bits.
- **SinglePointCrossover**: One-point crossover for binary strings.
- **TwoPointCrossover**: Two-point crossover for binary strings.
- **NPointCrossover**: General N-point crossover.
- **UniformCrossover**: Each bit is randomly selected from either parent.

For Permutation Solutions
~~~~~~~~~~~~~~~~~~~~~~~~

- **PMXCrossover**: Partially Mapped Crossover, good for ordering problems.
- **CycleCrossover**: Preserves absolute positions from parents.
- **PositionBasedCrossover**: Preserves relative ordering.
- **OXDCrossover**: Order-based crossover for permutation problems.
- **EdgeRecombinationCrossover**: Preserves adjacency information.

Mutation Operators
-----------------
Mutation operators introduce random changes to solutions to maintain diversity.

For Double Solutions
~~~~~~~~~~~~~~~~~~~

- **PolynomialMutation**: Polynomial mutation with distribution index.
- **NonUniformMutation**: Mutation strength decreases over time.
- **UniformMutation**: Uniform random mutation within bounds.
- **LevyFlightMutation**: Uses Lévy flights for larger jumps in search space.
- **PowerLawMutation**: Mutation based on power law distribution.
- **SimpleRandomMutation**: Simple uniform random mutation.

For Integer Solutions
~~~~~~~~~~~~~~~~~~~~

- **IntegerPolynomialMutation**: Integer version of polynomial mutation.
- **SimpleRandomMutation**: Random perturbation of integer values.

For Binary Solutions
~~~~~~~~~~~~~~~~~~~

- **BitFlipMutation**: Flips each bit with a given probability.

For Permutation Solutions
~~~~~~~~~~~~~~~~~~~~~~~~

- **SwapMutation**: Randomly swaps two elements.
- **InsertMutation**: Moves an element to a new position.
- **ScrambleMutation**: Randomly reorders a subsequence.
- **InversionMutation**: Inverts the order of a subsequence.
- **DisplacementMutation**: Moves a subsequence to a new position.
- **SimpleInversionMutation**: Simple inversion of two elements.

Selection Operators
------------------
Selection operators choose solutions from a population for reproduction.

- **BinaryTournamentSelection**: Selects the better of two random solutions.
- **NaryTournamentSelection**: Selects the best from N random solutions.
- **RankingAndCrowdingSelection**: NSGA-II selection with crowding distance.
- **RandomSelection**: Selects solutions uniformly at random.
- **BestSolutionSelection**: Selects the best solution in the population.
- **RouletteWheelSelection**: Fitness-proportional selection.
- **StochasticUniversalSampling**: Improved version of roulette wheel selection.

Local Search Operators
---------------------

- **BasicLocalSearch**: Applies local search to improve solutions.

Using Operators in jMetal
------------------------

Here's an example of how to create and use a crossover operator:

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

And here's how to create and use a mutation operator:

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

1. **Solution Encoding**: Match the operator to your solution representation (binary, real, permutation, etc.).
2. **Problem Characteristics**: Some operators work better for certain problem types.
3. **Diversity vs. Intensification**: Some operators promote exploration while others favor exploitation.
4. **Computational Cost**: More complex operators may be more expensive but provide better results.

