.. _problems:

Problems
==================

:Author: Antonio J. Nebro
:Version: Draft
:Date: 2020-2-25

All the problems in jMetal implement the `Problem` <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/problem/Problem.java>`_ interface:


.. code-block:: java

    package org.uma.jmetal.problem;

    /**
     * Interface representing a multi-objective optimization problem
     *
     * @author Antonio J. Nebro <antonio@lcc.uma.es>
     *
     * @param <S> Encoding
     */
    public interface Problem<S> extends Serializable {
      /* Getters */
      int getNumberOfVariables() ;
      int getNumberOfObjectives() ;
      int getNumberOfConstraints() ;
      String getName() ;

      /* Methods */
      void evaluate(S solution) ;
      S createSolution() ;
    }


Every problem is characterized by the number of decision variables, the number of objective functions and the number of constraints, so getter methods for returning those values have to be defined. The genetic type `S` allows to determine the encoding of the solutions of the problem. This way, a problem must include a method for evaluating any solution of class `S` as well as providing a `createSolution()` method for creating a new solution.

If a problem has side constraints, it is assumed that the overall constraint degree of a given solution is computed inside the `evaluate()` method.

jMetal 6.0 provides currently the following interfaces representing types of problems (all of them extending `Problem`):

* `BinaryProblem <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/problem/binaryproblem/BinaryProblem.java>`_
* `IntegerProblem <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/problem/integerproblem/IntegerProblem.java>`_
* `DoubleProblem <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/problem/doubleproblem/DoubleProblem.java>`_ 
* `PermutationProblem <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/problem/permutationproblem/PermutationProblem.java>`_
* `SequenceProblem <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/problem/sequenceproblem/SequenceProblem.java>`_
