package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class GlobalBestUpdateParameter extends CategoricalParameter {
  public GlobalBestUpdateParameter(List<String> updateStrategies) {
    super("globalBestUpdate", updateStrategies);
  }

  public GlobalBestUpdate getParameter() {
    GlobalBestUpdate result;
    if ("defaultGlobalBestUpdate".equals(value())) {
      result = new DefaultGlobalBestUpdate();
    } else {
      throw new JMetalException("Global Best Update component unknown: " + value());
    }
    return result;
  }
}
