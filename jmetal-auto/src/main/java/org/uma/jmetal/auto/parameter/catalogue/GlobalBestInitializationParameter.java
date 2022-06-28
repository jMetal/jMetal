package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class GlobalBestInitializationParameter extends CategoricalParameter {
  public GlobalBestInitializationParameter(String[] args, List<String> globalBestInitializationStrategies) {
    super("globalBestInitialization", args, globalBestInitializationStrategies);
  }

  public GlobalBestInitialization getParameter() {
    GlobalBestInitialization result;

    if ("defaultGlobalBestInitialization".equals(getValue())) {
      result = new DefaultGlobalBestInitialization();
    } else {
      throw new JMetalException("Global best initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "globalBestInitialization";
  }
}
