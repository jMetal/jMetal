package org.uma.jmetal.qualityindicator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.uma.jmetal.qualityindicator.impl.*;
import org.uma.jmetal.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.JMetalLogger;
import org.uma.jmetal.util.NormalizeUtils;
import org.uma.jmetal.util.errorchecking.JMetalException;

public class QualityIndicatorUtils {

  private QualityIndicatorUtils() {
    throw new IllegalArgumentException() ;
  }

  /**
   * Creates a list with the available indicators (but {@link SetCoverage}). The
   * quality indicators are initialized with a reference front.
   *
   * @param referenceFront
   * @return
   */
  public static List<QualityIndicator> getAvailableIndicators(double[][] referenceFront) {
    List<QualityIndicator> list = new ArrayList<>();
    list.add(new Epsilon(referenceFront));
    list.add(new PISAHypervolume(referenceFront));
    list.add(new NormalizedHypervolume(referenceFront));
    list.add(new ErrorRatio(referenceFront));
    list.add(new GenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistance(referenceFront));
    list.add(new InvertedGenerationalDistancePlus(referenceFront));
    list.add(new Spread(referenceFront));
    list.add(new GeneralizedSpread(referenceFront));
    list.add(new SetCoverage(referenceFront));

    return list;
  }

  /**
   * Creates a list with the available indicators (but {@link SetCoverage}) without
   * indicating a reference front (it must be added later).
   *
   * @return A list of quality indicators
   */
  public static List<QualityIndicator> getAvailableIndicators() {
    List<QualityIndicator> list = new ArrayList<>();
    list.add(new Epsilon());
    list.add(new PISAHypervolume());
    list.add(new NormalizedHypervolume());
    list.add(new ErrorRatio());
    list.add(new GenerationalDistance());
    list.add(new InvertedGenerationalDistance());
    list.add(new InvertedGenerationalDistancePlus());
    list.add(new Spread());
    list.add(new GeneralizedSpread());
    list.add(new SetCoverage()) ;

    return list;
  }

  /**
   * Given an indicator name, finds the indicator in the list of available indicators
   *
   * @param indicatorName
   * @return A quality indicator
   */
  public static QualityIndicator getIndicatorFromName(String indicatorName) {
    return getIndicatorFromName(indicatorName, getAvailableIndicators()) ;
  }

  /**
   * Given an indicator name, finds the indicator in the indicator list
   *
   * @param indicatorName
   * @param indicatorList
   * @return A quality indicator
   */
  public static QualityIndicator getIndicatorFromName(String indicatorName,
      List<QualityIndicator> indicatorList) {
    QualityIndicator result = indicatorList.stream()
        .filter(indicator -> indicator.name().equals(indicatorName)).findFirst().orElse(null);

    if (null == result) {
      throw new JMetalException("The indicator " + indicatorName + " does not exist") ;
    }

    return result;
  }

  /**
   * Returns a list of quality indicators from a list of indicator names
   * @param indicatorNames
   * @return
   */
  public static List<QualityIndicator> getIndicatorsFromNames(List<String> indicatorNames) {
    return indicatorNames.stream().map(QualityIndicatorUtils::getIndicatorFromName).collect(Collectors.toList());
  }

  /**
   * Given a front and a reference front, computes and prints the quality indicator values of the
   * front.
   * @param front
   * @param referenceFront
   */
  public static void printQualityIndicators(double[][] front, double[][] referenceFront) {
    double[][] normalizedReferenceFront = NormalizeUtils.normalize(referenceFront);
    double[][] normalizedFront =
        NormalizeUtils.normalize(
            front,
            NormalizeUtils.getMinValuesOfTheColumnsOfAMatrix(referenceFront),
            NormalizeUtils.getMaxValuesOfTheColumnsOfAMatrix(referenceFront));

    List<QualityIndicator> qualityIndicators = getAvailableIndicators(normalizedReferenceFront);
    for (QualityIndicator indicator : qualityIndicators) {
      JMetalLogger.logger.info(
          () -> indicator.name() + ": " + indicator.compute(normalizedFront));
    }
  }
}
