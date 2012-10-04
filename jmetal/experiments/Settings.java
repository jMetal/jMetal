//  Settings.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jmetal.experiments;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Properties;

import jmetal.core.Algorithm;
import jmetal.core.Operator;
import jmetal.core.Problem;
import jmetal.encodings.solutionType.ArrayRealSolutionType;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.BinarySolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.operators.crossover.Crossover;
import jmetal.operators.crossover.CrossoverFactory;
import jmetal.operators.mutation.Mutation;
import jmetal.operators.mutation.MutationFactory;
import jmetal.util.JMException;
import jmetal.util.Configuration.*;

/**
 * Class representing Settings objects. 
 */
public abstract class Settings {
	protected Problem problem_ ;
	protected String problemName_ ;
	protected String paretoFrontFile_ ;

	/**
	 * Constructor
	 */
	public Settings() {
	} // Constructor

	/**
	 * Constructor
	 */
	public Settings (String problemName) {
		problemName_ = problemName ;
	} // Constructor

	/**
	 * Default configure method
	 * @return A problem with the default configuration
	 * @throws jmetal.util.JMException
	 */
	abstract public Algorithm configure() throws JMException ;

	/**
	 * Configure method. Change the default configuration
	 * @param settings
	 * @return A problem with the settings indicated as argument
	 * @throws jmetal.util.JMException
	 * @throws ClassNotFoundException 
	 */
	public final Algorithm configure(HashMap settings) throws JMException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
		if (settings != null) {
			Field [] fields = this.getClass().getFields();
			for (int i=0; i < fields.length; i++) {
				if (fields[i].getName().endsWith("_")) { // it is a configuration field      
					// The configuration field is an integer
					if (fields[i].getType().equals(int.class) ||
							fields[i].getType().equals(Integer.class)) {
						if (settings.containsKey(fields[i].getName())) {
							Integer value = (Integer)settings.get(fields[i].getName());
							fields[i].setInt(this, value.intValue());
						}
					}  
					else if (fields[i].getType().equals(double.class) ||
							fields[i].getType().equals(Double.class)) {
						// The configuration field is a double
						//double value = Double.parseDouble(settings.getProperty(fields[i].getName(),""+fields[i].getDouble(this)));
						Double value = (Double)settings.get(fields[i].getName());

						if (settings.containsKey(fields[i].getName())) {
							if (fields[i].getName().equals("mutationProbability_") &&
									value == null) {
								if ((problem_.getSolutionType().getClass() == RealSolutionType.class) ||
										(problem_.getSolutionType().getClass() == ArrayRealSolutionType.class)) {
									value = 1.0 / problem_.getNumberOfVariables();
								} else if (problem_.getSolutionType().getClass() == BinarySolutionType.class ||
										problem_.getSolutionType().getClass() == BinaryRealSolutionType.class) {
									int length = problem_.getNumberOfBits();
									value = 1.0 / length;
								} else {
									int length = 0;
									for (int j = 0; j < problem_.getNumberOfVariables(); j++) {
										length+= problem_.getLength(j);
									}
									value = 1.0 / length;
								}
								fields[i].setDouble(this, value);
							} // if
							else {
								fields[i].setDouble(this, value);
							}
						}
					} else {
						//Object value = settings.getProperty(fields[i].getName(), null) ;
						Object value = settings.get(fields[i].getName());
						if (value!=null) {
							if (fields[i].getType().equals(Crossover.class)) {
								Object value2 = CrossoverFactory.getCrossoverOperator((String)value,settings);
								value = value2;
							}

							if (fields[i].getType().equals(Mutation.class)) {
								Object value2 = MutationFactory.getMutationOperator((String)value, settings);
								value = value2;
							}

							fields[i].set(this, value);
						}
					}
				}
			} // for

			// At this point all the fields have been read from the properties
			// parameter. Those fields representing crossover and mutations should also
			// be initialized. However, there is still mandatory to configure them
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].getType().equals(Crossover.class) ||
						fields[i].getType().equals(Mutation.class)) {
					Operator operator = (Operator) fields[i].get(this);
					// This field stores a crossover operator
					String tmp = fields[i].getName();
					String aux = fields[i].getName().substring(0, tmp.length()-1);

					for (int j = 0; j < fields.length; j++) {
						if (i != j) {
							if (fields[j].getName().startsWith(aux)) {
								// The field is a configuration parameter of the crossover
								tmp = fields[j].getName().substring(aux.length(), fields[j].getName().length()-1);

								if (
										(fields[j].get(this)!=null)) {
									if (fields[j].getType().equals(int.class) ||
											fields[j].getType().equals(Integer.class)) {
										operator.setParameter(tmp, fields[j].getInt(this));
									} else if (fields[j].getType().equals(double.class) ||
											fields[j].getType().equals(Double.class)) {
										operator.setParameter(tmp, fields[j].getDouble(this));
									}
								}
							}
						}
					}
				}
			}
			//At this point, we should compare if the pareto front have been added
			paretoFrontFile_ = (String)settings.get("paretoFrontFile_");
		}

		return configure();
	} // configure

	/**
	 * Changes the problem to solve
	 * @param problem
	 */
	void setProblem(Problem problem) {
		problem_ = problem ;
	} // setProblem

	/**
	 * Returns the problem
	 */
	Problem getProblem() {
		return problem_ ;
	} // getProblem
} // Settings