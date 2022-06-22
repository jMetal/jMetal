package org.uma.jmetal.experimental.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.experimental.auto.parameter.CategoricalParameter;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.experimental.componentbasedalgorithm.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;

public class GlobalBestUpdateParameter extends CategoricalParameter {
  public GlobalBestUpdateParameter(String args[], List<String> updateStrategies) {
    super("globalBestUpdate", args, updateStrategies);
  }

  public GlobalBestUpdate getParameter() {
    GlobalBestUpdate result;
    switch (getValue()) {
      case "defaultGlobalBestUpdate":
        result = new DefaultGlobalBestUpdate();
        break;

      default:
        throw new RuntimeException("Global Best Update component unknown: " + getValue());
    }
    return result;
  }
}
