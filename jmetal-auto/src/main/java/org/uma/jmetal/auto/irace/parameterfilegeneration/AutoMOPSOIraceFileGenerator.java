package org.uma.jmetal.auto.irace.parameterfilegeneration;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoMOPSO;

/**
 * Program to generate the irace configuration file for class {@link AutoMOPSO}
 * Note that the result configuration must be manually modified, replacing the lines of parameters
 * weightMin and weightMax by these:
 *
 * weightMin    "--weightMin "     r   (0.1, 0.5)      | inertiaWeightComputingStrategy %in% c("randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue")
 * weightMax    "--weightMax "     r    (0.5, 1.0)     | inertiaWeightComputingStrategy %in% c("randomSelectedValue", "linearIncreasingValue", "linearDecreasingValue")
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class AutoMOPSOIraceFileGenerator {

  public static void main(String[] args) {

    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator();
    parameterFileGenerator.generateConfigurationFile(new AutoMOPSO());
  }
}
