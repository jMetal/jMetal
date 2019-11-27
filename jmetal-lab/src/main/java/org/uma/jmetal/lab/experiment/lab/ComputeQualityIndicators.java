package org.uma.jmetal.lab.experiment.lab;

import org.uma.jmetal.qualityindicator.QualityIndicator;

import java.util.List;

public class ComputeQualityIndicators {
  private final ExperimentSettings<?, ?> experimentSettings ;
  private final List<QualityIndicator<?, ?>> qualityIndicators ;

  public ComputeQualityIndicators(ExperimentSettings<?, ?> experimentSettings, List<QualityIndicator<?, ?>> qualityIndicators) {
    this.experimentSettings = experimentSettings;
    this.qualityIndicators = qualityIndicators;
  }

  public void run() {

  }
}
