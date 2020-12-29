package org.uma.jmetal.qualityindicator;

import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.errorchecking.Check;

import java.util.ArrayList;
import java.util.List;

public class QualityIndicatorUtils {
  /**
   * Creates a list with the available indicators (but setCoverage)
   *
   * @param referenceFront
   * @return
   */
  public static List<QualityIndicator> getAvailableIndicators(double[][] referenceFront) {

    List<QualityIndicator> list = new ArrayList<>();
    list.add(new Epsilon(referenceFront));
    list.add(new PISAHypervolume(referenceFront));
    list.add(new NormalizedHypervolume(referenceFront));
    list.add(new GenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistancePlus(referenceFront));
    list.add(new Spread(referenceFront));
    list.add(new GeneralizedSpread(referenceFront));

    return list;
  }

  /**
   * Given an indicator name, finds the indicator in the list of indicator
   *
   * @param name
   * @param list
   * @return
   */
  public static QualityIndicator getIndicatorFromName(String name, List<QualityIndicator> list) {
    QualityIndicator result = null;

    for (QualityIndicator indicator : list) {
      if (indicator.getName().equals(name)) {
        result = indicator;
        break;
      }
    }

    Check.isNotNull(result);

    return result;
  }
}
