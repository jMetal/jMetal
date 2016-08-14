package org.uma.jmetal.util.pseudorandom.impl;

import org.uma.jmetal.util.pseudorandom.AbstractRandomTest;
import org.uma.jmetal.util.pseudorandom.PseudoRandomGenerator;

public class MersenneTwisterGeneratorTest extends AbstractRandomTest {

	@Override
	protected PseudoRandomGenerator getInstance() {
		return new MersenneTwisterGenerator();
	}

}
