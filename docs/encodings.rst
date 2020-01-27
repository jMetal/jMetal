.. _encodings:

Solution encodings
==================

:Author: Antonio J. Nebro
:Version: Draft
:Date: 2020-1-23


jMetal 6.0 provides currently the following interfaces representing encodings:

* `BinarySolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/binarysolution>`_
* `IntegerSolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/integersolution>`_
* `DoubleSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/doublesolution/DoubleSolution.java>`_ 
* `PermutationSolution <https://github.com/jMetal/jMetal/tree/master/jmetal-core/src/main/java/org/uma/jmetal/solution/permutationsolution>`_
* `CompositeSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/compositesolution/CompositeSolution.java>`_

Binary encoding
---------------

Implementations:

* `DefaultBinarySolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/binarysolution/impl/DefaultBinarySolution.java>`_: The solution chromosome is a list of binary strings. The number of solution variables is the number of strings.

Integer encoding
---------------

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

Composite or mixed encoding
---------------------------

Implementation:

* `CompositeSolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/compositesolution/CompositeSolution.java>`_: A solution is compose of list of solutions, thus allowing to mix different types of encodings in a single solution. Each solution variable contains a solution.