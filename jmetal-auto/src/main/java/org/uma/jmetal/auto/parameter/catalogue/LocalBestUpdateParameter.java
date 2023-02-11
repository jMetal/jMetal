package org.uma.jmetal.auto.parameter.catalogue;

import java.util.List;
import org.uma.jmetal.auto.parameter.CategoricalParameter;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.LocalBestUpdate;
import org.uma.jmetal.component.catalogue.pso.localbestupdate.impl.DefaultLocalBestUpdate;
import org.uma.jmetal.util.comparator.dominanceComparator.DominanceComparator;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class LocalBestUpdateParameter extends CategoricalParameter {
  public LocalBestUpdateParameter(List<String> localBestUpdateStrategies) {
    super("localBestUpdate", localBestUpdateStrategies);
  }

  public LocalBestUpdate getParameter(DominanceComparator comparator) {
    LocalBestUpdate result;

    if ("defaultLocalBestUpdate".equals(value())) {
      result = new DefaultLocalBestUpdate(comparator);
    } else {
      throw new JMetalException("Local best update component unknown: " + value());
    }

    return result;
  }
}
