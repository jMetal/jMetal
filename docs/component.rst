.. component:

Component-based algorithms
==========================

:Author: Antonio J. Nebro
:Version: 1.0
:Date: 2022-7-11

The design and architecture of the metaheuristics included in jMetal is probably the feature that has evolved
the most since the start of the project in 2006. In first versions, the implementation of a metaheuristic required
to implement an `Algorithmn` abstract class, which had an `execute()` abstract method that had to be implemented by any
algorithm. As a consequence, developers were free to write the code of an algorithm without any constraint,
resulting in many cases in codes that were difficult to extend and to reuse. To give an example, the
`execute()` code of our implementation of NSGA-II was about 130 lines long, and the development of a
steady-state version of NSGA-II was made by copying all of that code and modifying a few lines from de original
code. This architecture was described in the paper `jMetal: A Java framework for multi-objective
optimization <https://doi.org/10.1016/j.advengsoft.2011.05.014>`_, published in Advanced in Engineering Software
in 2011, and it was used until jMetal version 4.5.

In 2014 I took the decision of redesigning jMetal from scratch, leading to jMetal 5.0 a year later.
The main features of that version were presented in the EvoSoft of the GECCO conference in 2015 (
`Redesigning the jMetal Multi-Objective Optimization Framework <https://doi.org/10.1145/2739482.2768462>_`),
including the inclusion of algorithm templates for metaheuristics such as evolutionary algorithms
and particle swarm optimization techniques. In this way, code reuse was significantly improved but
at the cost of having to take into account the use of templates,
which has proved cumbersome for some researchers, and the consequence is that there are still people
using jMetal 4.5.


