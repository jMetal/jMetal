package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import component.catalogue.pso.globalbestupdate.GlobalBestUpdate;
import component.catalogue.pso.globalbestupdate.impl.DefaultGlobalBestUpdate;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class GlobalBestUpdateParameter extends CategoricalParameter {
  public GlobalBestUpdateParameter(String[] args, List<String> updateStrategies) {
    super("globalBestUpdate", args, updateStrategies);
  }

  public GlobalBestUpdate getParameter() {
    GlobalBestUpdate result;
    if ("defaultGlobalBestUpdate".equals(getValue())) {
      result = new DefaultGlobalBestUpdate();
    } else {
      throw new JMetalException("Global Best Update component unknown: " + getValue());
    }
    return result;
  }
}
