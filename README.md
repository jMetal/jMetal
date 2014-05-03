# jMetal

**jMetal** stands for Metaheuristic Algorithms in Java, and it is an object-oriented Java-based framework for
multi-objective optimization with metaheuristics.

### Features

- Portability
- Multi-objective algoritms: NSGA-II (variants: ssNSGAII, NSGAIIadaptive, NSGAIIrandom), SPEA2, PAES, PESA-II, OMOPSO,
MOCell, AbYSS, MOEA/D, Densea, CellDE, GDE3, FastPGA, IBEA, SMPSO, SMPSOhv, SMS-EMOA, dMOPSO
- Single-objective algoritms: genetic algorithm (variants: generational, steady-state, synchronous cellular, asynchronous
cellular), evolution strategy (variants: elitist or mu+lambda, non-elitist or mu, lambda), PSO, DE, CMA-ES
- Parallel algorithms: pNSGAII, pSMPSO, pMOEAD, pgGA
- Included problems:
  - Problem families: ZDT, DTLZ, WFG, CEC2009, LZ09
  - Classical problems: Kursawe, Fonseca, Schaffer
  - Constrained problems: Srinivas, Tanaka, Osyczka2, Constr_Ex, Golinski, Water
  - Combinatorial problems: multi-objective TSP, multi-objective QAP
- Quality indicators: hypervolume, spread, generational distance, inverted generational distance, additive epsilon, R2,
WFG hypervolume
- Variable representations: binary, real, binary-coded real, integer, permutation, mixed encoding (real+binary, int+real)

### You can use it to ...

The object-oriented architecture of the framework and the included features allow you to: experiment with the provided
classic and state-of-the-art techniques, develop your own algorithms, solve your optimization problems, integrate jMetal
in other tools, etc.

### Our motivation is ...

The motivation driving us is to provide the programs we use in our own works to the multi-objective optimization research
community. We have made an effort in trying to produce an ease-to-use software, and we are continuously modifying and
extending jMetal according to our needs and those suggested by people interested in using it.

