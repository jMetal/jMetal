package org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume;

import java.io.FileNotFoundException;
import java.util.List;
import org.uma.jmetal.util.legacy.front.Front;
import org.uma.jmetal.util.legacy.front.impl.ArrayFront;
import org.uma.jmetal.util.legacy.front.util.FrontUtils;
import org.uma.jmetal.util.legacy.qualityindicator.impl.GenericIndicator;
import org.uma.jmetal.util.point.Point;
import org.uma.jmetal.util.point.impl.ArrayPoint;

/**
 * This interface represents implementations of the Hypervolume quality indicator
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @author Juan J. Durillo
 */
@SuppressWarnings("serial")
@Deprecated
public abstract class Hypervolume<S> extends GenericIndicator<S> {

  public Hypervolume() {}

  public Hypervolume(String referenceParetoFrontFile) throws FileNotFoundException {
    super(referenceParetoFrontFile);
  }

  public Hypervolume(double[] referencePoint) {
    Front referenceFront = new ArrayFront(referencePoint.length, referencePoint.length);
    for (var i = 0; i < referencePoint.length; i++) {
      Point point = new ArrayPoint(referencePoint.length);
      for (var j = 0; j < referencePoint.length; j++) {
        if (j == i) {
          var v = referencePoint[i] ;
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
