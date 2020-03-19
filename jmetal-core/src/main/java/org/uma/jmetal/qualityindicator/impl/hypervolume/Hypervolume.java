package org.uma.jmetal.qualityindicator.impl.hypervolume;

import org.uma.jmetal.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * This interface represents implementations of the Hypervolume quality indicator
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
public abstract class Hypervolume<S> extends GenericIndicator<S> {

  public Hypervolume() {}

  public Hypervolume(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile);
  }

  public Hypervolume(double[] referencePoint) {
    Front referenceFront = new ArrayFront(referencePoint.length, referencePoint.length);
    for (int i = 0; i < referencePoint.length; i++) {
      Point point = new ArrayPoint(referencePoint.length);
      for (int j = 0; j < referencePoint.length; j++) {
        if (j == i) {
          double v = referencePoint[i] ;
          point.setValue(j, v);
        } else {
          point.setValue(j, 0.0);
        }
      }
      referenceFront.setPoint(i, point);
    }
    this.referenceParetoFront = referenceFront;
  }

  public Hypervolume(Front referenceParetoFront) {
    super(referenceParetoFront);
  }

  public abstract List<S> computeHypervolumeContribution(
      List<S> solutionList, List<S> referenceFrontList);

  public List<S> computeHypervolumeContribution(List<S> solutionList) {
    return this.computeHypervolumeContribution(
        solutionList, (List<S>) FrontUtils.convertFrontToSolutionList(referenceParetoFront));
  }

  public abstract double getOffset();

  public abstract void setOffset(double offset);

  @Override
  public String getName() {
    return "HV";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return false;
  }
}
