package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;

public class GlobalBestInitializationParameter extends CategoricalParameter {
  public GlobalBestInitializationParameter(String[] args, List<String> globalBestInitializationStrategies) {
    super("globalBestInitialization", args, globalBestInitializationStrategies);
  }

  public GlobalBestInitialization getParameter() {
    GlobalBestInitialization result;

    switch (getValue()) {
      case "defaultGlobalBestInitialization":
        result = new DefaultGlobalBestInitialization() ;
        break;
      default:
        throw new RuntimeException("Global best initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "globalBestInitialization";
  }
}
