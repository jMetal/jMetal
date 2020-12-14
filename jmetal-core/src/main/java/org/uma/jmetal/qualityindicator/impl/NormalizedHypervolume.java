package org.uma.jmetal.qualityindicator.impl;

import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.VectorUtils;

/**
 * Class providing an implementation of the normalized hypervolume, which is calculated as follows:
 * relative hypervolume = 1 - (HV of the front / HV of the reference front)
 * <p>
 * Before computing this indicator it must be checked that the HV of the reference front is not zero.
 *
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 */
public class NormalizedHypervolume extends QualityIndicator {
  private double referenceFrontHypervolume;
  private Hypervolume hypervolume;

  public NormalizedHypervolume() {
  }

  public NormalizedHypervolume(String referenceFrontFile) {
    super(referenceFrontFile);
    double[][] referenceFront = VectorUtils.readVectors(referenceFrontFile);
    hypervolume = new PISAHypervolume(referenceFrontFile);

    referenceFrontHypervolume = hypervolume.compute(referenceFront);
  }

  public NormalizedHypervolume(double[] referencePoint) {
    // TODO: add a unit test
    double[][] referenceFront = {referencePoint};
    hypervolume = new PISAHypervolume();
    hypervolume.setReferenceFront(referenceFront);

    referenceFrontHypervolume = hypervolume.compute(referenceFront);
  }

  public NormalizedHypervolume(double[][] referenceFront) {
    super(referenceFront);
    hypervolume = new PISAHypervolume(referenceFront);

    referenceFrontHypervolume = hypervolume.compute(referenceFront);
  }

  @Override
  public void setReferenceFront(double[][] referenceFront) {
    super.setReferenceFront(referenceFront);

    hypervolume = new PISAHypervolume(referenceFront);
    referenceFrontHypervolume = hypervolume.compute(referenceFront);
  }

  @Override
  public void setReferenceFront(String referenceFrontFile) {
    super.setReferenceFront(referenceFrontFile);

    hypervolume = new PISAHypervolume(referenceFrontFile);
    referenceFrontHypervolume =
            hypervolume.compute(VectorUtils.readVectors(referenceFrontFile));
  }

  @Override
  public String getName() {
    return "NHV";
  }

  @Override
  public String getDescription() {
    return "Normalized hypervolume";
  }


  @Override
  public boolean isTheLowerTheIndicatorValueTheBetter() {
    return true;
  }

  @Override
  public double compute(double[][] front) {
    double hypervolumeValue = hypervolume.compute(front);

    return 1 - (hypervolumeValue / referenceFrontHypervolume);
  }
}
