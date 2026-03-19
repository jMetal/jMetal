package org.uma.jmetal.auto.irace.parameterfilegeneration;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoAGEMOEA;

/**
 * Program to generate the irace configuration file for class {@link AutoAGEMOEA}.
 */
public class AutoAGEMOEAIraceParameterFileGenerator {
  public static void main(String[] args) {
    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator();
    parameterFileGenerator.generateConfigurationFile(new AutoAGEMOEA());
  }
}
