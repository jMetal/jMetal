.. _encodings:

Solution encodings
==================

:Author: Antonio J. Nebro
:Version: Draft
:Date: 2020-1-28

One of the first decisions that have to be taken when using metaheuristics is to define how to encode or represent the tentative solutions of the problem to solve. Representation strongly depends on the problem and determines the operations (e.g., recombination with other solutions, local search procedures, etc.) that can be applied. Thus, selecting a specific representation has a great impact on the behavior of metaheuristics and, hence, in the quality of the obtained results.

The base class of all the solution encodings in jMetal is the `Solution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/Solution.java>`_ interface:

.. code-block:: java

    package org.uma.jmetal.solution;
    
    public interface Solution<T> extends Serializable {
      void setObjective(int index, double value) ;
      double getObjective(int index) ;
      double[] getObjectives() ;

      T getVariable(int index) ;
      List<T> getVariables() ;
      void setVariable(int index, T variable) ;

      double[] getConstraints() ;
      double getConstraint(int index) ;
      void setConstraint(int index, double value) ;

      int getNumberOfVariables() ;
      int getNumberOfObjectives() ;
      int getNumberOfConstraints() ;

      Solution<T> copy() ;

      void setAttribute(Object id, Object value) ;
      Object getAttribute(Object id) ;
      boolean hasAttribute(Object id) ;
  
      Map<Object, Object> getAttributes();
    }

Any solution contains a list of decision variables, an array of objective values, and array of constraint values (i.e., the constraint violation degree per each of the problem side constraints), and a map of attributes (e.g., to assign properties such as ranking, strength, etc., which are usually algorithm dependant). The variable values are assigned when a solution is created (typically, when invoking the `createSolution()` method of a problem), whereas the objective and constraint values are set when evaluating a solution. 

jMetal provides currently the following interfaces representing encodings (all of them extending `Solution`):

* `BinarySolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/binarysolution/BinarySolution.java>`_
* `IntegerSolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/integersolution/IntegerSolution.java>`_
* `DoubleSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/doublesolution/DoubleSolution.java>`_ 
* `PermutationSolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/permutationsolution>`_
* `SequenceSolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/sequencesolution>`_
* `CompositeSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/compositesolution/CompositeSolution.java>`_

Binary encoding
---------------

Implementations:

* `DefaultBinarySolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/binarysolution/impl/DefaultBinarySolution.java>`_: The solution chromosome is a list of binary strings. The number of solution variables is the number of strings.

Integer encoding
----------------

Implementations:

* `DefaultIntegerSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/integersolution/impl/DefaultIntegerSolution.java>`_: The solution chromosome is a list of integer values bounded by a lower and upper bound. Each integer value is stored in a solution variable.


Double encoding
---------------

Implementations:

* `DefaultDoubleSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/doublesolution/impl/DefaultDoubleSolution.java>`_: The solution chromosome is a list of double values bounded by a lower and upper bound. Each double value is stored in a solution variable.


Permutation encoding
--------------------

Implementations:

* `IntegerPermutationSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/permutationsolution/impl/IntegerPermutationSolution.java>`_: The encoding is a list of N integer numbers with are arrange as a permutation of size N. Each value of the permutation is stored in a solution variable.

Sequence encoding
-----------------

Implementations:

* `CharSequenceSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/sequencesolution/impl/CharSequenceSolution.java>`_: The encoding a list of N char values.

Composite or mixed encoding
---------------------------

Implementation:

* `CompositeSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/compositesolution/CompositeSolution.java>`_: A solution is composed of list of solutions, thus allowing to mix different types of encodings in a single solution. Each solution variable contains a solution.
