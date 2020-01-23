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

* `DefaultBinarySolution <https://github.com/jMetal/jMetal/blob/master/jmetal-core/src/main/java/org/uma/jmetal/solution/binarysolution/impl/DefaultBinarySolution.java>`_: The solution chromosome is a list of binary strings. 


