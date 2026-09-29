package org.uma.jmetal.auto.irace.parameterfilegeneration;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoRVEA;

/**
 * Program to generate the irace configuration file for class {@link AutoRVEA}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class AutoRVEAIraceParameterFileGenerator {
  public static void main(String[] args) {
    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator();
    parameterFileGenerator.generateConfigurationFile(new AutoRVEA());
  }
}
