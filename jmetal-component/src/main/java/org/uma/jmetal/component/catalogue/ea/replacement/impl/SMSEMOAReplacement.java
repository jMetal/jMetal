package org.uma.jmetal.component.catalogue.ea.replacement.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.jetbrains.annotations.NotNull;
import org.uma.jmetal.component.catalogue.ea.replacement.Replacement;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.Hypervolume;
import org.uma.jmetal.util.legacy.qualityindicator.impl.hypervolume.impl.PISAHypervolume;
import org.uma.jmetal.util.ranking.Ranking;

public class SMSEMOAReplacement<S extends Solution<?>>
    implements Replacement<S> {
  private Ranking<S> ranking;
  private Hypervolume<S> hypervolume ;

  public SMSEMOAReplacement(Ranking<S> ranking) {
    this(ranking,  new PISAHypervolume<>()) ;
  }

  public SMSEMOAReplacement(
          Ranking<S> ranking, Hypervolume<S> hypervolume) {
    this.ranking = ranking;
    this.hypervolume = hypervolume;
  }

  public List<S> replace(@NotNull List<S> solutionList, @NotNull List<S> offspringList) {
    @NotNull List<S> jointPopulation = new ArrayList<>();
    jointPopulation.addAll(solutionList);
    jointPopulation.addAll(offspringList);

    ranking.compute(jointPopulation);

    List<S> lastSubFront = ranking.getSubFront(ranking.getNumberOfSubFronts()-1) ;

    lastSubFront = hypervolume.computeHypervolumeContribution(lastSubFront, jointPopulation) ;

      List<S> resultPopulation = new ArrayList<>();
      int bound = ranking.getNumberOfSubFronts() - 1;
      for (int i1 = 0; i1 < bound; i1++) {
          List<S> subFront = ranking.getSubFront(i1);
          for (S s : subFront) {
              resultPopulation.add(s);
          }
      }

      for (int i = 0; i < lastSubFront.size()-1; i++) {
      resultPopulation.add(lastSubFront.get(i)) ;
    }

    return resultPopulation ;
  }
}
