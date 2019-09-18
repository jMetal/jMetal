Autoconfiguration of evolutionary algorithms: NSGA-II
=====================================================

Before reading this section, readers are referred to the paper "Automatic configuration of NSGA-II with jMetal and
irace", presented in GECCO 2019 (DOI: https://doi.org/10.1145/3319619.3326832)

Motivation
----------
A current trend in multi-objective optimization is to use automatic parameter configuration tools to find accurate settings of metaheuristics to effectively solve a number of problems. The idea is to avoid the traditional approach of carrying out a number of pilot tests, which are typically conducted without following a systematic strategy. In this context, an algorithm configuration is a complete assignment of values to all required parameters of the algorithm.

The autoconfiguration tool we have selected is irace, an R package that implements an
elitist iterated racing algorithm, where algorithm configurations
are sampled from a sampling distribution, uniformly at random at the beginning, but biased towards the best configurations found in later iterations. At each iteration, the generated configurations and
the "elite" ones from previous iterations are raced by evaluating
them on training problem instances. A statistical test is used to
decide which configurations should be eliminated from the race.
When the race terminates, the surviving configurations become
elites for the next iteration.

The issue we studied in the aformentioned paper is how to use jMetal combined with irace to allow the automatic configuration of multiobjective metaheuristics. As this is our first approximation to this matter, we decided to focus on NSGA-II and its use to solve continuous problems.




NSGA-II parameters
------------------
The standard NSGA-II is a generational evolutionary algorithm featured by using a ranking method based on Pareto ranking and the crowding distance density estimator, both in the selection and replacement steps.
When it is used to solve continuous problems, NSGA-II adopts the
simulated binary crossover (SBX) and the polynomial mutation. No
external archive is included in NSGA-II.
However, as we intend to configure NSGA-II in an automatic
way, we need to relax the aforementioned features in order to have
enough flexibility to modify the search capabilities of the algorithm.
This way, we are going to consider that any multi-objective evolutionary
algorithm with the typical parameters (selection, crossover,
and mutation) and using ranking and crowding in the replacement
step can be considered as a variant of NSGA-II. 

The parameters or components of NSGA-II that can be adjusted are included in this table:

+---------------------------------------+-----------------------------------------------------+
| Parameter name                        | Allowed values                                      | 
+=======================================+=====================================================+
| *algorithmResult*                     | *extenalArchive*, *population*                      |
+---------------------------------------+-----------------------------------------------------+
| *populationSize*                      | 100                                                 |
+---------------------------------------+-----------------------------------------------------+ 
| *populationSizeWithArchive*           | 10,20,50,100,200,400                                |
+---------------------------------------+-----------------------------------------------------+
| *offspringPopulationSize*             | 1,5,10,20,50,100,200,400                            | 
+---------------------------------------+-----------------------------------------------------+
| *createInitialSolutions*              | *random*, *latinHypercubeSampling*, *scatterSearch* |
+---------------------------------------+-----------------------------------------------------+
| *variation*                           | *crossoverAndMutationVariation*                     |
+---------------------------------------+-----------------------------------------------------+
| *crossover*                           | *SBX*, *BLX_Alpha*                                  |
+---------------------------------------+-----------------------------------------------------+
| *crossoverProbability*                | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *crossoverRepairStrategy*             | *random*, *round*, *bounds*                         |
+---------------------------------------+-----------------------------------------------------+
| *sbxCrossoverDistributionIndex*       | [5.0, 400.0]                                        | 
+---------------------------------------+-----------------------------------------------------+
| *blxAlphaCrossoverAlphaValue*         | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *mutation*                            | *uniform*, *polynomial*                             |
+---------------------------------------+-----------------------------------------------------+
| *mutationProbability*                 | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *mutationRepairStrategy*              | *random*, *round*, *bounds*                         |
+---------------------------------------+-----------------------------------------------------+
| *polynomialMutationDistributionIndex* | [5.0, 400.0]                                        |
+---------------------------------------+-----------------------------------------------------+
| *uniformMutationPerturbation*         | [0.0, 1.0]                                          |
+---------------------------------------+-----------------------------------------------------+
| *selection*                           | *random*, *tournament*                              |
+---------------------------------------+-----------------------------------------------------+
| *selectionTournamentSize*             | [2, 10]                                             |
+---------------------------------------+-----------------------------------------------------+

Our *autoNSGAII* can optionally adopt an external archive to store the non-dominated solutions found during the search process. The archive size of bounded and the crowding distance estimator is used to remove solutions with the archive is full. Then, in case of using no archive, the result of the algorithm is the population, which is configured with the *populationSize* parameter; otherwise, the output is the external archive, whose maximum size is *populationSize* parameter value, but then the population size can be tuned by taking values from the set (10, 20, 50, 100, 200, 400). 

In the classical NSGA-II, the offspring population size is equal to the population size, but we can set its value from 1 (which leads to a steady-state selection scheme) to 400.

The initial population is typically filled with randomly created solutions, but we also allows to use a latin hypercube sampling scheme and a strategy similar to the one used in the scatter search algorithm.

The *autoNSGAII* has a *variation* component than can take a single value named *crossoverAndMutationVariation*. It is intended to represent the typical crossover and mutation operators of a genetic algorithm (additional values, e.g., *DifferentialiEvolutionVariation* are expected to be added in the future). The *crossver* operators included are *SBX* (simulated binary crossover) and *BLX_Alpha*, which ara featured by a given probability and a *crossoverRepairStrategy*, which defines what to do when the crossver produces a variable value out of the allowed bounds (please, refer to Section 3.2 and Figure 3 in the paper). The *SBX* and *BLX_Alpha* require, if selected, a distribution index (a value in the range [5.0, 400]) and an alpha value (in the range [0.0, 1.0]), respectively. Similarly, there are two possible mutation operators to choose from, *polynomial* and *uniform*, requiring both a mutation probability and a repairing strategy; the polymial mutation has, as the SBX crossover, a distribution index parameter (in the range [5.0, 400]) and the *uniform* mutation needs a perturbation value (in the range [0.0, 1.0]).

Finally, the *selection* operator be *random* or *tournament*; this last one can take a value between 2 (i.e., binary tournament) and 10.


Evolutionary algorithm template
-------------------------------
The following code snippet include the most relevant parts of the ``ÈvolutionaryAlgorithm`` class, which is the algorithm template we have defined for developing autoconfigurable metaheuristics. It is not an abstract but a regular class containing the basic components of an evolutionary algorithm, including the selection, variation and replacement steps. 


.. code-block:: java

  public class EvolutionaryAlgorithm<S extends Solution<?>>{
    ...
    public EvolutionaryAlgorithm(
      String name,
      Evaluation<S> evaluation,
      InitialSolutionsCreation<S> initialPopulationCreation,
      Termination termination,
      MatingPoolSelection<S> selection,
      Variation<S> variation,
      Replacement<S> replacement,
      Archive<S> externalArchive) {
      ...
   }

    public void run() {
      population = createInitialPopulation.create();
      population = evaluation.evaluate(population);
      initProgress();
      while (!termination.isMet(attributes)) {
        List<S> matingPopulation = selection.select(population);
        List<S> offspringPopulation = variation.variate(population, matingPopulation);
        offspringPopulation = evaluation.evaluate(offspringPopulation);
        updateArchive(offspringPopulation);

        population = replacement.replace(population, offspringPopulation);
        updateProgress();
      }
    }

    private void updateArchive(List<S> population) {
      if (externalArchive != null) {
        for (S solution : population) {
          externalArchive.add(solution);
        }
      }
    }

    ...

    @Override
    public List<S> getResult() {
      if (externalArchive != null) {
        return externalArchive.getSolutionList();
      } else {
        return population;
      }
    }
  }




.. , as we focus here mainly on implementation issues. The motivation for including auto configuration (or auto tuning) of multi-objecive evolutionary algorithms in jMetal, the proposed architecture, the aspects that can be tuned in NSGA-II, and the results of an experiment showing a use case are described in that paper.







Configuration file:





API
---


.. code-block:: java

   public interface Algorithm<Result> extends Runnable, Serializable, DescribedEntity {
      void run() ;
      Result getResult() ;
   }




+------------+------------+-----------+
| Header 1   | Header 2   | Header 3  |
+============+============+===========+
| body row 1 | column 2   | column 3  |
+------------+------------+-----------+
| body row 2 | Cells may span columns.|
+------------+------------+-----------+
| body row 3 | Cells may  | - Cells   |
+------------+ span rows. | - contain |
| body row 4 |            | - blocks. |
+------------+------------+-----------+
