package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.util.JMetalException;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.imp.ArrayFront;
import org.uma.jmetal.util.naming.impl.SimpleDescribedEntity;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Abstract class representing quality indicators that need a reference front to be computed
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public abstract class GenericIndicator<S>
    extends SimpleDescribedEntity
    implements QualityIndicator<List<S>, Double> {

  protected Front referenceParetoFront = null ;
  /**
   * Default constructor
   */
  public GenericIndicator() {
  }

  public GenericIndicator(String referenceParetoFrontFile) throws FileNotFoundException {
    setReferenceParetoFront(referenceParetoFrontFile);
  }

  public GenericIndicator(Front referenceParetoFront) {
    if (referenceParetoFront == null) {
      throw new NullParetoFrontException();
    }

    this.referenceParetoFront = referenceParetoFront ;
  }

  public void setReferenceParetoFront(String referenceParetoFrontFile) throws FileNotFoundException {
    if (referenceParetoFrontFile == null) {
      throw new NullParetoFrontException();
    }

    Front front = new ArrayFront(referenceParetoFrontFile);
    referenceParetoFront = front ;
  }

  public void setReferenceParetoFront(Front referenceFront) throws FileNotFoundException {
    if (referenceFront == null) {
      throw new NullParetoFrontException();
    }

    referenceParetoFront = referenceFront ;
  }

  /**
   * This method returns true if lower indicator values are preferred and false otherwise
   * @return
   */
  public abstract boolean isTheLowerTheIndicatorValueTheBetter() ;
  
  private static class NullParetoFrontException extends JMetalException {
    public NullParetoFrontException() {
      super("The reference pareto front is null");
    }
  }
}
