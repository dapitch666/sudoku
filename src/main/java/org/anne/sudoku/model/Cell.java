package org.anne.sudoku.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;

public class Cell {
    private final int index;
    private int value = 0;
    private final BitSet candidates = new BitSet(9);
    private boolean justSolved;

    public Cell(int index, int value) {
        this.index = index;
        this.value = value;
        if (value == 0) this.candidates.set(1, 10);
        this.justSolved = value != 0;
    }

    public Cell(int index, BitSet candidates) {
        this.index = index;
        this.candidates.or(candidates);
    }

    public void setValue(int value) {
        this.value = value;
        this.candidates.clear();
        this.justSolved = true;
    }

    public int getRow() {
        return index / 9;
    }

    public int getCol() {
        return index % 9;
    }

    public int getBox() {
        return (index / 9) / 3 * 3 + (index % 9) / 3;
    }

    public int getHorizontalChute() {
        return getBox() / 3;
    }

    public int getVerticalChute() {
        return getBox() % 3;
    }


    public List<Integer> getCandidates() {
        List<Integer> candidateList = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (candidates.get(i)) {
                candidateList.add(i);
            }
        }
        return candidateList;
    }

    public int getValue() {
        return value;
    }

    public boolean hasCandidate(int digit) {
        return candidates.get(digit);
    }

    public boolean hasCandidates(List<Integer> digits) {
        for (int digit : digits) {
            if (!candidates.get(digit)) {
                return false;
            }
        }
        return true;
    }

    public int getCandidateCount() {
        return candidates.cardinality();
    }

    public boolean isBiValue() {
        return candidates.cardinality() == 2;
    }

    public int getFirstCandidate() {
        return candidates.nextSetBit(0);
    }

    public boolean isPeer(Cell other) {
        return this != other && (this.getRow() == other.getRow() || this.getCol() == other.getCol() || this.getBox() == other.getBox());
    }

    public boolean isSolved() {
        return candidates.cardinality() == 0;
    }

    public void unsetJustSolved() {
        this.justSolved = false;
    }


    @Override
    public String toString() {
        String LETTERS = "ABCDEFGHJ";
        return String.format("%s%s", LETTERS.charAt(getRow()), getCol() + 1);
    }

    public int index() {
        return index;
    }

    public int value() {
        return value;
    }

    public BitSet candidates() {
        return candidates;
    }

    public boolean justSolved() {
        return justSolved;
    }

    public int getUnitIndex(UnitType unitType) {
        return switch (unitType) {
            case ROW -> getRow();
            case COL -> getCol();
            case BOX -> getBox();
        };
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Cell) obj;
        return this.index == that.index &&
                Objects.equals(this.value, that.value) &&
                Objects.equals(this.candidates, that.candidates) &&
                this.justSolved == that.justSolved;
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, value, candidates, justSolved);
    }

    public List<Integer> removeAllBut(List<Integer> digits) {
        List<Integer> removed = candidates.stream().filter(i -> !digits.contains(i)).boxed().toList();
        for (int candidate : removed) {
            removeCandidate(candidate);
        }
        return removed;
    }


    public List<Integer> removeAllBut(int digit) {
        List<Integer> removed = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            if (i != digit) {
                removed.add(i);
                removeCandidate(i);
            }
        }
        return removed;
    }

    public void removeCandidates(BitSet valuesToRemove) {
        this.candidates.andNot(valuesToRemove);
    }

    public List<Integer> removeCandidates(List<Integer> valuesToRemove) {
        List<Integer> removed = new ArrayList<>();
        for (int value : valuesToRemove) {
            if (candidates.get(value)) {
                this.candidates.clear(value);
                removed.add(value);
            }
        }
        return removed;
    }

    public boolean removeCandidate(int digit) {
        if (candidates.get(digit)) {
            candidates.clear(digit);
            return true;
        }
        return false;
    }

    public List<UnitType> getCommonUnitType(Cell other) {
        List<UnitType> unitTypes = new ArrayList<>();
        if (this.getRow() == other.getRow()) unitTypes.add(UnitType.ROW);
        if (this.getCol() == other.getCol()) unitTypes.add(UnitType.COL);
        if (this.getBox() == other.getBox()) unitTypes.add(UnitType.BOX);
        return unitTypes;
    }
}
