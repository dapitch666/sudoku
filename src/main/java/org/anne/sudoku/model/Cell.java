package org.anne.sudoku.model;

import java.util.*;
import java.util.stream.Collectors;

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

    public void clear() {
        this.value = 0;
        this.candidates.set(1, 10);
        this.justSolved = false;
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
        return candidates.stream().boxed().collect(Collectors.toList());
    }

    public int getValue() {
        return value;
    }

    public boolean hasCandidate(int digit) {
        return candidates.get(digit);
    }

    public boolean hasCandidates(BitSet digits) {
        return digits.stream().allMatch(candidates::get);
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

    public int getOtherCandidate(int digit) {
        if (candidates.cardinality() != 2) {
            throw new IllegalStateException("Cell does not have exactly two candidates");
        }
        if (!candidates.get(digit)) {
            throw new IllegalArgumentException("Digit is not a candidate in this cell");
        }
        int otherCandidate = candidates.nextSetBit(digit + 1);
        return otherCandidate == -1 ? candidates.nextSetBit(0) : otherCandidate;
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

    public BitSet removeAllBut(BitSet valuesToKeep) {
        BitSet removed = (BitSet) this.candidates.clone();
        removed.andNot(valuesToKeep);
        this.candidates.and(valuesToKeep);
        return removed;
    }

    public BitSet removeAllBut(List<Integer> digits) {
        BitSet valuesToKeep = digits.stream()
                .mapToInt(i -> i)
                .collect(BitSet::new, BitSet::set, BitSet::or);
        return removeAllBut(valuesToKeep);
    }

    public BitSet removeCandidates(BitSet valuesToRemove) {
        BitSet removed = (BitSet) this.candidates.clone();
        removed.and(valuesToRemove);
        this.candidates.andNot(valuesToRemove);
        return removed;
    }

    public BitSet removeCandidates(List<Integer> digits) {
        BitSet valuesToRemove = digits.stream()
                .mapToInt(i -> i)
                .collect(BitSet::new, BitSet::set, BitSet::or);
        return removeCandidates(valuesToRemove);
    }

    public boolean removeCandidate(int digit) {
        if (!candidates.get(digit)) return false;
        candidates.clear(digit);
        return true;
    }

    public List<UnitType> getCommonUnitType(Cell other) {
        List<UnitType> unitTypes = new ArrayList<>();
        if (this.getRow() == other.getRow()) unitTypes.add(UnitType.ROW);
        if (this.getCol() == other.getCol()) unitTypes.add(UnitType.COL);
        if (this.getBox() == other.getBox()) unitTypes.add(UnitType.BOX);
        return unitTypes;
    }

    @Override
    public String toString() {
        String LETTERS = "ABCDEFGHJ";
        return String.format("%s%s", LETTERS.charAt(getRow()), getCol() + 1);
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
}
