package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.front.Front;
import org.uma.jmetal.util.front.impl.ArrayFront;
import org.uma.jmetal.util.front.util.FrontUtils;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Class providing an implementation of the normalized hypervolume, which is calculated as follows:
 * relative hypervolume = 1 - (HV of the front / HV of the reference front)
 *
 * Before computing this indicator it must be checked that the HV of the reference front is not zero.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
@SuppressWarnings("serial")
public class NormalizedHypervolume<S extends Solution<?>> extends GenericIndicator<S> {
  private double referenceFrontHypervolume;
  private Hypervolume<S> hypervolume;

  public NormalizedHypervolume() {
  }

  public NormalizedHypervolume(String referenceParetoFrontFile)
      throws FileNotFoundException {
    super(referenceParetoFrontFile);
    Front referenceFront = new ArrayFront(referenceParetoFrontFile);
    hypervolume = new PISAHypervolume<>(referenceParetoFrontFile) ;

    referenceFrontHypervolume =
        this.hypervolume.evaluate(
            (List<S>) FrontUtils.convertFrontToSolutionList(referenceFront));
  }

  public NormalizedHypervolume(double[] referencePoint) {
    Front referenceFront = new ArrayFront(referencePoint.length, referencePoint.length);
    hypervolume = new PISAHypervolume<>();
    hypervolume.setReferenceParetoFront(referenceFront);

    referenceFrontHypervolume =
        hypervolume.evaluate((List<S>) FrontUtils.convertFrontToSolutionList(referenceFront));
  }

  public NormalizedHypervolume(Front referenceParetoFront) {
    super(referenceParetoFront);
    hypervolume = new PISAHypervolume<>(referenceParetoFront) ;

    referenceFrontHypervolume =
            this.hypervolume.evaluate(
                    (List<S>) FrontUtils.convertFrontToSolutionList(referenceParetoFront));
  }

  @Override
  public void setReferenceParetoFront(Front referenceFront) {
    super.setReferenceParetoFront(referenceFront);

    hypervolume = new PISAHypervolume<>(referenceFront) ;
    referenceFrontHypervolume =
            hypervolume.evaluate((List<S>) FrontUtils.convertFrontToSolutionList(new ArrayFront(referenceFront)));
  }

  @Override
  public void setReferenceParetoFront(String referenceParetoFrontFile) throws FileNotFoundException {
    super.setReferenceParetoFront(referenceParetoFrontFile);

    hypervolume = new PISAHypervolume<>(referenceParetoFrontFile) ;
    referenceFrontHypervolume =
            hypervolume.evaluate((List<S>) FrontUtils.convertFrontToSolutionList(new ArrayFront(referenceParetoFrontFile)));
  }

  @Override
  public String getName() {
    return "NHV";
  }

  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }

  @Override
  public Double evaluate(List<S> solutionList) {
    double hypervolumeValue = hypervolume.evaluate(solutionList);

    return 1 - (hypervolumeValue / referenceFrontHypervolume);
  }
}
