//  ParallelTask.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
////
//  Copyright (c) 2014 Antonio J. Nebro
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
//

package jmetal.util.parallel;

import java.util.concurrent.Callable;

/**
 * Created by Antonio J. Nebro on 20/02/14.
 * Abstract class for tasks to be executed by a SynchronousParallelRunner subclass
 */
abstract public class ParallelTask implements Callable<Object> {
  abstract public Object call() throws Exception;
}
