package org.uma.jmetal;

import org.junit.runner.RunWith;

import com.googlecode.junittoolbox.WildcardPatternSuite;
import com.googlecode.junittoolbox.SuiteClasses;

/*
 * We use the WildcardPatternSuite to run one test after the other,
 * so time-sensible tests are not impacted by the resource consumptions
 * of other tests. If such tests happen to disappear from the unit tests,
 * one can use ParallelSuite instead to go faster.
 */
@RunWith(WildcardPatternSuite.class)
@SuiteClasses({ "**/*Test.class" })
/**
 * This suite allows for people who do not test through Maven to run only the
 * unit tests (not the integration tests) to save a significant amount of time.
 * 
 * @author Matthieu Vergne <matthieu.vergne@gmail.com>
 *
 */
public class UnitTestsOnlySuite {

}
