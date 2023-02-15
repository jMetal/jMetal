package org.uma.jmetal.auto.irace.parameterfilegeneration;

import org.uma.jmetal.auto.autoconfigurablealgorithm.AutoNSGAII;

/**
 * Program to generate the irace configuration file for class {@link AutoNSGAII}
 *
 * @author Antonio J. Nebro (ajnebro@uma.es)
 */
public class AutoNSGAIIIraceParameterFileGenerator {

  public static void main(String[] args) {
    IraceParameterFileGenerator parameterFileGenerator = new IraceParameterFileGenerator() ;
    parameterFileGenerator.generateConfigurationFile(new AutoNSGAII()) ;
  }
}
