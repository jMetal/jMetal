package org.uma.jmetal.util.measure.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.uma.jmetal.util.measure.MeasureListener;
import org.uma.jmetal.util.measure.impl.LastEvaluationMeasure.Evaluation;

public class LastEvaluationMeasureTest {

	@Test
	public void testSpecializedPushActLikeParentPush() {
		var measure = new LastEvaluationMeasure<String, Integer>();
		@SuppressWarnings("unchecked")
		final Evaluation<String, Integer>[] lastEvaluation = new Evaluation[] { null };
		measure.register(evaluation -> lastEvaluation[0] = evaluation);

		var solution = "individual";
		var value = 3;
		measure.push(solution, value);
		assertNotNull(lastEvaluation[0]);
		assertEquals(solution, lastEvaluation[0].solution);
		assertEquals(value, (Object) lastEvaluation[0].value);

		lastEvaluation[0] = null;
		var evaluation = new Evaluation<String, Integer>();
		evaluation.solution = solution;
		evaluation.value = value;
		measure.push(evaluation);
		assertNotNull(lastEvaluation[0]);
		assertEquals(solution, lastEvaluation[0].solution);
		assertEquals(value, (Object) lastEvaluation[0].value);
	}

}
