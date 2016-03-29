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

package org.uma.jmetal.util.archive.impl;

import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.BoundedArchive;

import java.util.List;

/**
 * @author Antonio J. Nebro <antonio@lcc.uma.es>
 * @param <S>
 */
@SuppressWarnings("serial")
public abstract class AbstractBoundedArchive<S extends Solution<?>> implements BoundedArchive<S> {
	protected NonDominatedSolutionListArchive<S> archive;
	protected int maxSize;

	public AbstractBoundedArchive(int maxSize) {
		this.maxSize = maxSize;
		this.archive = new NonDominatedSolutionListArchive<S>();
	}

	@Override
	public boolean add(S solution) {
		boolean success = archive.add(solution);
		if (success) {
			prune();
		}

		return success;
	}

	@Override
	public S get(int index) {
		return getSolutionList().get(index);
	}

	@Override
	public List<S> getSolutionList() {
		return archive.getSolutionList();
	}

	@Override
	public int size() {
		return archive.size();
	}

	@Override
	public int getMaxSize() {
		return maxSize;
	}

	public abstract void prune();
}
