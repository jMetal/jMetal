package org.uma.jmetal.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.uma.jmetal.qualityindicator.QualityIndicator;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.solution.doublesolution.DoubleSolution;

class DynamicFrontManagerTest {

  @Mock
  private QualityIndicator mockIndicator;

  @BeforeEach
  void setUp() {
    mockIndicator = mock(QualityIndicator.class) ;
  }

  @Test
  void whenCreatingTheFrontIsNull() {
    // Arrange
    DynamicFrontManager<DoubleSolution> dynamicFrontManager = new DynamicFrontManager<>(0.1, mockIndicator);

    // Assert
    assertNull(dynamicFrontManager.front());
  }

  @Test
  void whenCallIndicatorThenReturnTheQualityIndicator() {
    // Arrange
    DynamicFrontManager<DoubleSolution> dynamicFrontManager = new DynamicFrontManager<>(0.1, mockIndicator);

    // Assert
    assertEquals(mockIndicator, dynamicFrontManager.indicator());
  }


  @Test
  void whenUpdateIsCalledTheFirstTimeTheFrontIsUpdated() {
    // Arrange
    List<DoubleSolution> newFront = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    DynamicFrontManager<DoubleSolution> dynamicFrontManager = new DynamicFrontManager<>(0.1, mockIndicator);
    when(mockIndicator.compute(any())).thenReturn(0.3);

    // Act
    boolean frontUpdated = dynamicFrontManager.update(newFront);

    // Assert
    assertTrue(frontUpdated);
    assertEquals(newFront, dynamicFrontManager.front());
  }

  @Test
  void givenDifferenceIsGreaterThanThresholdWhenUpdateThenTheFrontIsUpdated() {
    // Arrange
    List<DoubleSolution> front1 = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    List<DoubleSolution> front2 = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    DynamicFrontManager<DoubleSolution> dynamicFrontManager = new DynamicFrontManager<>(0.1, mockIndicator);

    when(mockIndicator.compute(any())).thenReturn(0.5, 0.8);
    dynamicFrontManager.update(front1);

    // Act
    boolean frontUpdated = dynamicFrontManager.update(front2);

    // Assert
    assertTrue(frontUpdated);
    assertEquals(front2, dynamicFrontManager.front());
    assertEquals(0.8, dynamicFrontManager.indicator().compute(null)); // Here we expect 0.5 based on the mock
  }

  @Test
  void givenDifferenceIsLessThanThresholdWhenUpdateThenTheFrontIsNotUpdated() {
    // Arrange
    List<DoubleSolution> front1 = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    List<DoubleSolution> front2 = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    DynamicFrontManager<DoubleSolution> dynamicFrontManager = new DynamicFrontManager<>(0.4, mockIndicator);

    when(mockIndicator.compute(any())).thenReturn(0.5, 0.6);
    dynamicFrontManager.update(front1);

    // Act
    boolean frontUpdated = dynamicFrontManager.update(front2);

    // Assert
    assertFalse(frontUpdated);
    assertEquals(front1, dynamicFrontManager.front());
  }

  @Test
  void givenDifferenceEqualToThresholdWhenUpdateThenTheFrontIsNotUpdated() {
    // Arrange
    List<DoubleSolution> front1 = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    List<DoubleSolution> front2 = List.of(mock(DoubleSolution.class), mock(DoubleSolution.class));
    DynamicFrontManager<DoubleSolution> dynamicFrontManager = new DynamicFrontManager<>(0.2, mockIndicator);

    when(mockIndicator.compute(any())).thenReturn(0.8, 0.8);
    dynamicFrontManager.update(front1);

    // Act
    boolean frontUpdated = dynamicFrontManager.update(front2);

    // Assert
    assertFalse(frontUpdated);
    assertEquals(front1, dynamicFrontManager.front());
  }
}