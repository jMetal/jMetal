package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.GlobalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.globalbestinitialization.impl.DefaultGlobalBestInitialization;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class GlobalBestInitializationParameter extends CategoricalParameter {
  public GlobalBestInitializationParameter(List<String> globalBestInitializationStrategies) {
    super("globalBestInitialization", globalBestInitializationStrategies);
  }

  public GlobalBestInitialization getParameter() {
    GlobalBestInitialization result;

    if ("defaultGlobalBestInitialization".equals(value())) {
      result = new DefaultGlobalBestInitialization();
    } else {
      throw new JMetalException("Global best initialization component unknown: " + value());
    }

    return result;
  }

  @Override
  public String name() {
    return "globalBestInitialization";
  }
}
