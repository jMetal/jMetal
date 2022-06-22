package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.auto.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.auto.parameter.CategoricalParameter;

public class LocalBestInitializationParameter extends CategoricalParameter {
  public LocalBestInitializationParameter(String[] args, List<String> localBestInitializationStrategies) {
    super("localBestInitialization", args, localBestInitializationStrategies);
  }

  public LocalBestInitialization getParameter() {
    LocalBestInitialization result;

    switch (getValue()) {
      case "defaultLocalBestInitialization":
        result = new DefaultLocalBestInitialization() ;
        break;
      default:
        throw new RuntimeException("Local best initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public String getName() {
    return "localBestInitialization";
  }
}
