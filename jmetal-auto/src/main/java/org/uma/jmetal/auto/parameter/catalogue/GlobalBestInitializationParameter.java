package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.auto.component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.auto.parameter.CategoricalParameter;

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
