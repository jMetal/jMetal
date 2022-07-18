package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.LocalBestInitialization;
import org.uma.jmetal.component.catalogue.pso.localbestinitialization.impl.DefaultLocalBestInitialization;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class LocalBestInitializationParameter extends CategoricalParameter {
  public LocalBestInitializationParameter(String[] args, List<String> localBestInitializationStrategies) {
    super("localBestInitialization", args, localBestInitializationStrategies);
  }

  public @NotNull LocalBestInitialization getParameter() {
    LocalBestInitialization result;

    if ("defaultLocalBestInitialization".equals(getValue())) {
      result = new DefaultLocalBestInitialization();
    } else {
      throw new JMetalException("Local best initialization component unknown: " + getValue());
    }

    return result;
  }

  @Override
  public @NotNull String getName() {
    return "localBestInitialization";
  }
}
