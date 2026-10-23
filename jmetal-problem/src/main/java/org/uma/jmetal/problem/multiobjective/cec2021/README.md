# CEC 2021 real-world constrained multi-objective problems (RCM01-RCM50)

This package contains the 50 problems of the real-world constrained multi-objective optimization
test suite (RWMOP) of the CEC 2021 competition, ported from the reference MATLAB code of the suite
(`CEC2021_func.m`, by A. Kumar):

> A. Kumar, G. Wu, M. Z. Ali, Q. Luo, R. Mallipeddi, P. N. Suganthan and S. Das, "A Benchmark-Suite
> of real-World constrained multi-objective optimization problems and some baseline results", Swarm
> and Evolutionary Computation 67:100961, 2021 (https://doi.org/10.1016/j.swevo.2021.100961).

The class of each problem and its `name()` are `RCM01` to `RCM50`, and its reference front, when
there is one, is `resources/referenceFronts/RCMnn.csv`. The constraints are inequalities
`g(x) >= 0` in jMetal; the equality constraints of the suite are relaxed to `|h(x)| <= epsilon`.
The problems of the same family share an abstract class: `AbstractRCMSelectiveHarmonicElimination`
(RCM30-35), `AbstractRCMDistributedGenerationSizing` (RCM36-39), `AbstractRCMOptimalPowerFlow`
(RCM40-46) and `AbstractRCMIslandedMicrogridDroopControl` (RCM47-49).

| Problem | Description | Category | Objectives | Variables | Constraints | Reference front | Notes |
|---|---|---|---:|---:|---:|---|---|
| `RCM01` | Pressure vessel design | Mechanical design | 2 | 4 | 2 | yes |  |
| `RCM02` | Vibrating platform design | Mechanical design | 2 | 5 | 5 | yes |  |
| `RCM03` | Two bar truss design | Mechanical design | 2 | 3 | 3 | yes |  |
| `RCM04` | Welded beam design | Mechanical design | 2 | 4 | 4 | yes |  |
| `RCM05` | Disc brake design | Mechanical design | 2 | 4 | 4 | yes |  |
| `RCM06` | Speed reducer design | Mechanical design | 2 | 7 | 11 | yes |  |
| `RCM07` | Gear train design | Mechanical design | 2 | 4 | 1 | yes |  |
| `RCM08` | Car side impact design | Mechanical design | 3 | 7 | 9 | yes |  |
| `RCM09` | Four bar plane truss | Mechanical design | 2 | 4 | 0 | yes | no constraints (see below) |
| `RCM10` | Two bar plane truss | Mechanical design | 2 | 2 | 2 | yes |  |
| `RCM11` | Water resource management | Mechanical design | 5 | 3 | 7 | yes | same problem as `CRE51` |
| `RCM12` | Simply supported I-beam design | Mechanical design | 2 | 4 | 1 | yes |  |
| `RCM13` | Gear box design | Mechanical design | 3 | 7 | 11 | yes |  |
| `RCM14` | Multiple-disk clutch brake design | Mechanical design | 2 | 5 | 8 | yes |  |
| `RCM15` | Spring design | Mechanical design | 2 | 3 | 8 | yes |  |
| `RCM16` | Cantilever beam design | Mechanical design | 2 | 2 | 2 | yes |  |
| `RCM17` | Bulk carrier design | Mechanical design | 3 | 6 | 9 | no | no reference front (see below) |
| `RCM18` | Front rail design | Mechanical design | 2 | 3 | 3 | yes |  |
| `RCM19` | Multi-product batch plant | Mechanical design | 3 | 10 | 10 | yes |  |
| `RCM20` | Hydrostatic thrust bearing design | Mechanical design | 2 | 4 | 7 | yes |  |
| `RCM21` | Crash energy management for high-speed train | Mechanical design | 2 | 6 | 4 | yes |  |
| `RCM22` | Haverly's pooling problem | Chemical engineering | 2 | 9 | 6 | yes |  |
| `RCM23` | Reactor network design | Chemical engineering | 2 | 6 | 5 | yes |  |
| `RCM24` | Heat exchanger network design | Chemical engineering | 3 | 9 | 6 | yes |  |
| `RCM25` | Process synthesis | Process design and synthesis | 2 | 2 | 2 | yes |  |
| `RCM26` | Process synthesis and design | Process design and synthesis | 2 | 3 | 2 | yes |  |
| `RCM27` | Process flow sheeting | Process design and synthesis | 2 | 3 | 3 | yes |  |
| `RCM28` | Two-reactor problem | Process design and synthesis | 2 | 7 | 8 | yes |  |
| `RCM29` | Process synthesis (second problem) | Process design and synthesis | 2 | 7 | 9 | yes |  |
| `RCM30` | Synchronous optimal pulse-width modulation (SOPWM) of 3-level inverters | Power electronics | 2 | 25 | 24 | yes |  |
| `RCM31` | Synchronous optimal pulse-width modulation (SOPWM) of 5-level inverters | Power electronics | 2 | 25 | 24 | yes |  |
| `RCM32` | Synchronous optimal pulse-width modulation (SOPWM) of 7-level inverters | Power electronics | 2 | 25 | 24 | yes |  |
| `RCM33` | Synchronous optimal pulse-width modulation (SOPWM) of 9-level inverters | Power electronics | 2 | 30 | 29 | yes |  |
| `RCM34` | Synchronous optimal pulse-width modulation (SOPWM) of 11-level inverters | Power electronics | 2 | 30 | 29 | yes |  |
| `RCM35` | Synchronous optimal pulse-width modulation (SOPWM) of 13-level inverters | Power electronics | 2 | 30 | 29 | yes |  |
| `RCM36` | Distributed generation sizing: phase balance and active power loss | Power system | 2 | 28 | 24 | yes |  |
| `RCM37` | Distributed generation sizing: phase balance and reactive power loss | Power system | 2 | 28 | 24 | yes |  |
| `RCM38` | Distributed generation sizing: active and reactive power loss | Power system | 2 | 28 | 24 | yes |  |
| `RCM39` | Distributed generation sizing: phase balance, active and reactive power loss | Power system | 3 | 28 | 24 | yes |  |
| `RCM40` | Optimal power flow: active and reactive power loss | Power system | 2 | 34 | 26 | yes |  |
| `RCM41` | Optimal power flow: voltage deviation, active and reactive power loss | Power system | 3 | 34 | 26 | yes |  |
| `RCM42` | Optimal power flow: voltage deviation and active power loss | Power system | 2 | 34 | 26 | yes |  |
| `RCM43` | Optimal power flow: fuel cost and active power loss | Power system | 2 | 34 | 26 | yes |  |
| `RCM44` | Optimal power flow: fuel cost, active and reactive power loss | Power system | 3 | 34 | 26 | yes |  |
| `RCM45` | Optimal power flow: fuel cost, voltage deviation and active power loss | Power system | 3 | 34 | 26 | yes |  |
| `RCM46` | Optimal power flow: fuel cost, voltage deviation, active and reactive power loss | Power system | 4 | 34 | 26 | yes |  |
| `RCM47` | Droop control of an islanded microgrid: active and reactive power loss | Power system | 2 | 18 | 12 | yes |  |
| `RCM48` | Droop control of an islanded microgrid: active power loss and voltage deviation | Power system | 2 | 18 | 12 | yes |  |
| `RCM49` | Droop control of an islanded microgrid: active and reactive power loss and voltage deviation | Power system | 3 | 18 | 12 | yes |  |
| `RCM50` | Power distribution system planning | Power system | 2 | 6 | 1 | yes |  |

Notes:

- **RCM09** has no constraints: the metadata of the suite (`Cal_par.m`) lists one inequality
  constraint, but the reference evaluator never assigns it, and the port follows the evaluator.
- **RCM17** has no reference front: its first objective has no lower bound in the feasible region.
- The reference fronts are estimated, not exact: they are the non-dominated feasible solutions of
  many runs of several algorithms, completed near their extremes by SLSQP and, for the problems
  whose equality constraints leave feasible regions that the algorithms do not reach (RCM22, RCM24,
  RCM28 and RCM30-49), by problem-specific searches. The front of RCM30 is less certain than the
  rest. See the page on constrained benchmarks of the documentation.
- The number of objectives, variables and constraints are those of the default constructor of each
  class.
