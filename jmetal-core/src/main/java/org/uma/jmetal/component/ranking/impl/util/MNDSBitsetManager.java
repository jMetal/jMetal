package org.uma.jmetal.component.ranking.impl.util;

/**
 *
 * This class implements a simple bitset adapted to the Merge Non-dominated Sorting (MNDS) algorithm
 * Please, note that in MNDS the size of a bitset can only be reduced or remain the same
 *
 * @author Javier Moreno <javier.morenom@edu.uah.es>
 */
public class MNDSBitsetManager {
	private final static int FIRST_WORD_RANGE = 0;
	private final static int LAST_WORD_RANGE = 1;
	private final static int N_BIT_ADDR = 6;
	private final static int WORD_SIZE = 1 << N_BIT_ADDR;
	private static final long WORD_MASK = 0xffffffffffffffffL;
	private long[][] bitsets; //N bitsets. Each solution has a bitset
	private int[][] bsRanges; //N ranges [first sol - last sol]. Each of each bitset of each solution
	private int[] wordRanking;//Ranking of each bitset word. A bitset word contains 64 solutions.
	private int[] ranking, ranking0;
	private int maxRank;
	private long[] incrementalBitset;
	private int incBsFstWord, incBsLstWord;

	public void freeMem() {
		incrementalBitset = null;
		bitsets = null;
		bsRanges = null;
		wordRanking = null;
		ranking = null;
		ranking0 = null;
	}

	public int[] getRanking() {
		return ranking0;
	}

	public boolean updateSolutionDominance(int solutionId) {
		int fw = bsRanges[solutionId][FIRST_WORD_RANGE];
		int lw = bsRanges[solutionId][LAST_WORD_RANGE];
		if (lw > incBsLstWord)
			lw = incBsLstWord;
		if (fw < incBsFstWord)
			fw = incBsFstWord;

		while (fw <= lw && 0 == (bitsets[solutionId][fw] & incrementalBitset[fw]))
			fw++;
		while (fw <= lw && 0 == (bitsets[solutionId][lw] & incrementalBitset[lw]))
			lw--;
		bsRanges[solutionId][FIRST_WORD_RANGE] = fw;
		bsRanges[solutionId][LAST_WORD_RANGE] = lw;
		if (fw > lw)
			return false;
		for (; fw <= lw; fw++)
			bitsets[solutionId][fw] &= incrementalBitset[fw];
		return true;
	}

	public void computeSolutionRanking(int solutionId, int initSolId) {
		int fw = bsRanges[solutionId][FIRST_WORD_RANGE];
		int lw = bsRanges[solutionId][LAST_WORD_RANGE];
		if (lw > incBsLstWord)
			lw = incBsLstWord;
		if (fw < incBsFstWord)
			fw = incBsFstWord;
		if (fw > lw)
			return;
		long word;
		int i = 0, rank = 0, offset;
		for (; fw <= lw; fw++) {
			word = bitsets[solutionId][fw] & incrementalBitset[fw];
			if (word != 0) {
				i = Long.numberOfTrailingZeros(word);
				offset = fw * WORD_SIZE;
				do {
					if (ranking[offset + i] >= rank)
						rank = ranking[offset + i] + 1;
					i++;
					i += Long.numberOfTrailingZeros(word >> i);
				} while (i < WORD_SIZE && rank <= wordRanking[fw]);
				if (rank > maxRank) {
					maxRank = rank;
					break;
				}
			}
		}
		ranking[solutionId] = rank;
		ranking0[initSolId] = rank;
		i = solutionId >> N_BIT_ADDR;
		if (rank > wordRanking[i])
			wordRanking[i] = rank;
	}

	public void updateIncrementalBitset(int solutionId) {
		int wordIndex = solutionId >> N_BIT_ADDR;
		incrementalBitset[wordIndex] |= (1L << solutionId);
		if (incBsLstWord < wordIndex)
			incBsLstWord = wordIndex;
		if (incBsFstWord > wordIndex)
			incBsFstWord = wordIndex;
	}

	public boolean initializeSolutionBitset(int solutionId) {
		int wordIndex = solutionId >> N_BIT_ADDR;
		if (wordIndex < incBsFstWord || 0 == solutionId) {
			bsRanges[solutionId][FIRST_WORD_RANGE] = Integer.MAX_VALUE;
			return false;
		} else if (wordIndex == incBsFstWord) { //only 1 word in common
			bitsets[solutionId] = new long[wordIndex + 1];
			long intersection = incrementalBitset[incBsFstWord] & ~(WORD_MASK << solutionId);
			if (intersection != 0) {
				bsRanges[solutionId][FIRST_WORD_RANGE] = wordIndex;
				bsRanges[solutionId][LAST_WORD_RANGE] = wordIndex;
				bitsets[solutionId][wordIndex] = intersection;
			}
			return intersection != 0;
		}
		//more than one word in common
		int lw = incBsLstWord < wordIndex ? incBsLstWord : wordIndex;
		bsRanges[solutionId][FIRST_WORD_RANGE] = incBsFstWord;
		bsRanges[solutionId][LAST_WORD_RANGE] = lw;
		bitsets[solutionId] = new long[lw + 1];
		System.arraycopy(incrementalBitset, incBsFstWord, bitsets[solutionId], incBsFstWord, lw - incBsFstWord + 1);
		if (incBsLstWord >= wordIndex) { // update (compute intersection) the last word
			bitsets[solutionId][lw] = incrementalBitset[lw] & ~(WORD_MASK << solutionId);
			if (bitsets[solutionId][lw] == 0) {
				bsRanges[solutionId][LAST_WORD_RANGE]--;
			}
		}
		return true;
	}

	public void clearIncrementalBitset() {
		incrementalBitset = new long[incrementalBitset.length];
		incBsLstWord = 0;
		incBsFstWord = Integer.MAX_VALUE;
	}

	public MNDSBitsetManager(int nSolutions) {
		int n = nSolutions - 1;
		int wordIndex = n >> N_BIT_ADDR;
		ranking = new int[nSolutions];
		ranking0 = new int[nSolutions];
		wordRanking = new int[nSolutions];
		bitsets = new long[nSolutions][];
		bsRanges = new int[nSolutions][2];
		incrementalBitset = new long[wordIndex + 1];
		incBsLstWord = 0;
		incBsFstWord = Integer.MAX_VALUE;
	}
}
