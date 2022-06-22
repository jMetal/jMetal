package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.auto.component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.auto.parameter.CategoricalParameter;

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
