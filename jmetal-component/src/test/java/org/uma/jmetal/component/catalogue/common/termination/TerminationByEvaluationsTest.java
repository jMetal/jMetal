package org.uma.jmetal.component.catalogue.common.termination;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.uma.jmetal.component.catalogue.common.termination.impl.TerminationByEvaluations;
import org.uma.jmetal.util.errorchecking.exception.NullParameterException;

class TerminationByEvaluationsTest {

  @Test
  void TheConstructorInitializesCorrectlyTheMaximumNumberOfEvaluations() {
    int maximumNumberOfEvaluations = 10000;
    TerminationByEvaluations termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    assertThat(termination.getMaximumNumberOfEvaluations()).isEqualTo(maximumNumberOfEvaluations);
  }

  @Test
  void isMetRaisesAnExceptionIfTheEvaluationsFieldIsNotPresent() {
    int maximumNumberOfEvaluations = 10000;
    TerminationByEvaluations termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    Map<String, Object> algorithmStatusData = new HashMap<>();
    assertThatThrownBy(() -> termination.isMet(algorithmStatusData)).isInstanceOf(
        NullParameterException.class);
  }

  @Test
  void isMetRaisesReturnsTrueIfTheConditionIsMet() {
    int maximumNumberOfEvaluations = 10000;
    TerminationByEvaluations termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("EVALUATIONS", 10001);

    assertThat(termination.isMet(algorithmStatusData)).isTrue();
  }

  @Test
  void isMetRaisesReturnsFalseIfTheConditionIsNotMet() {
    int maximumNumberOfEvaluations = 10000;
    TerminationByEvaluations termination = new TerminationByEvaluations(maximumNumberOfEvaluations);

    Map<String, Object> algorithmStatusData = new HashMap<>();
    algorithmStatusData.put("EVALUATIONS", 9000);

    assertThat(termination.isMet(algorithmStatusData)).isFalse();
  }
}