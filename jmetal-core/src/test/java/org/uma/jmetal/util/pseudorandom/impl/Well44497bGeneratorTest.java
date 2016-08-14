package org.uma.jmetal.util.pseudorandom.impl;

import org.uma.jmetal.util.pseudorandom.AbstractRandomTest;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class Well44497bGeneratorTest extends AbstractRandomTest {

	@Override
	protected PseudoRandomGenerator getInstance() {
		return new Well44497bGenerator();
	}

}
