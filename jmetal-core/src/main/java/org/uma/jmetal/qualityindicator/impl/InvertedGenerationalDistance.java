package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This class implements the inverted generational distance metric.
 * Reference: Van Veldhuizen, D.A., Lamont, G.B.: Multiobjective Evolutionary Algorithm Research:
 * A History and Analysis.
 * Technical Report TR-98-03, Dept. Elec. Comput. Eng., Air Force
 * Inst. Technol. (1998)
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public class InvertedGenerationalDistance<S extends Solution<?>> extends GenericIndicator<S> {

  private double pow = 2.0;

  /**
   * Default constructor
   */
  public InvertedGenerationalDistance() {
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(String referenceParetoFrontFile, double p) throws FileNotFoundException {
    super(referenceParetoFrontFile) ;
    pow = p ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFrontFile
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(String referenceParetoFrontFile) throws FileNotFoundException {
    this(referenceParetoFrontFile, 2.0) ;
  }

  /**
   * Constructor
   *
   * @param referenceParetoFront
   * @throws FileNotFoundException
   */
  public InvertedGenerationalDistance(Front referenceParetoFront) {
    super(referenceParetoFront) ;
  }

  /**
   * Evaluate() method
   * @param solutionList
   * @return
   */
  @Override public Double evaluate(List<S> solutionList) {
    return invertedGenerationalDistance(new ArrayFront(solutionList), referenceParetoFront);
  }

  /**
   * Returns the inverted generational distance value for a given front
   *
   * @param front The front
   * @param referenceFront The reference pareto front
   */
  public double invertedGenerationalDistance(Front front, Front referenceFront) {
    double sum = 0.0;
    for (int i = 0 ; i < referenceFront.getNumberOfPoints(); i++) {
      sum += Math.pow(FrontUtils.distanceToClosestPoint(referenceFront.getPoint(i),
          front), pow);
    }

    sum = Math.pow(sum, 1.0 / pow);

    return sum / referenceFront.getNumberOfPoints();
  }

  @Override public String getName() {
    return "IGD" ;
  }

  @Override public String getDescription() {
    return "Inverted generational distance quality indicator" ;
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true ;
  }
}
