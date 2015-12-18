package org.uma.jmetal.measure.impl;

import org.junit.Test;
import org.uma.jmetal.measure.MeasureListener;
import org.uma.jmetal.measure.impl.LastEvaluationMeasure.Evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LastEvaluationMeasureTest {

	@Test
	public void testSpecializedPushActLikeParentPush() {
		LastEvaluationMeasure<String, Integer> measure = new LastEvaluationMeasure<>();
		@SuppressWarnings("unchecked")
		final Evaluation<String, Integer>[] lastEvaluation = new Evaluation[] { null };
		measure.register(new MeasureListener<Evaluation<String, Integer>>() {

			@Override
			public void measureGenerated(Evaluation<String, Integer> evaluation) {
				lastEvaluation[0] = evaluation;
			}
		});

		String solution = "individual";
		int value = 3;
		measure.push(solution, value);
		assertNotNull(lastEvaluation[0]);
		assertEquals(solution, lastEvaluation[0].solution);
		assertEquals(value, (Object) lastEvaluation[0].value);

		lastEvaluation[0] = null;
		Evaluation<String, Integer> evaluation = new Evaluation<>();
		evaluation.solution = solution;
		evaluation.value = value;
		measure.push(evaluation);
		assertNotNull(lastEvaluation[0]);
		assertEquals(solution, lastEvaluation[0].solution);
		assertEquals(value, (Object) lastEvaluation[0].value);
	}

}
