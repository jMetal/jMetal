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

We took the decision of redesigning jMetal from scratch in 2014, leading to jMetal 5.0 a year later.
The main features of that version were presented in the EvoSoft of the GECCO conference in 2015 (
`Redesigning the jMetal Multi-Objective Optimization Framework <https://doi.org/10.1145/2739482.2768462>_`),
including the inclusion of algorithm templates for metaheuristics such as evolutionary algorithms
and particle swarm optimization techniques. In this way, code reuse was significantly improved but
at the cost of having to take into account the use of templates,
which has proved cumbersome for some researchers, and the consequence is that there are still people
using jMetal 4.5.

Now, in the incoming major release of jMetal (version 6.0), we propose a new architecture for the
design and implementation of metaheuristics. The main reason has to do with the fact that we are
currently interested in using jMetal for the auto-configuration and auto-design of algorithms,
and the current architecture has limitations for this, as discussed in the paper
`Automatic Configuration of NSGA-II with jMetal and irace - GECCO Companion 2019 <https://doi.org/10.1145/3319619.3326832>`_.
The idea is to use a component-based approach, were algorithmic templates are again used but
the steps of the algorithms are objects instead of methods so, instead of using inheritance,
implementing a given algorithm requires to add the proper components to the template.

Contrarily to the approach adopted in jMetal 5.0, in jMetal 6.0 the new architecture is not going
to replace the former one. There are some reasons for this, starting with the aforementioned comment about
jMetal users who are comfortable with the current implementation and are probably not interested
in learning another way of developing or using the algorithms. Another reason is that algorithms
having tightly related components can be difficult to be implemented with the component-
based architecture; this way, while NSGA-II adapts very well to the architecture, to implement MOEA/D
we had to design some complext components.

...


