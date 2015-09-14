package org.uma.jmetal.algorithm.multiobjective.vepso;

import org.uma.jmetal.problem.DoubleProblem;
import org.uma.jmetal.solution.DoubleSolution;
import org.uma.jmetal.util.archive.Archive;


/**
 * This class implements the VEPSO algorithm described in:
 * MULTIOBJECTIVE OPTIMIZATION USING PARALLEL VECTOR EVALUATED PARTICLE SWARM OPTIMIZATION
 * K.E. Parsopoulos, D.K. Tasoulis, M.N. Vrahatis
 * Proceedings of international conference on artificial intelligence
 and applications (IASTED), Innsbruck, Austria; 2004.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class Vepso<S extends DoubleSolution> {
  private DoubleProblem problem;
  private Archive<S> archive ;

  public Vepso(DoubleProblem problem, Archive<S> archive) {
    this.problem = problem ;
    this.archive = archive ;
  }


}
