package org.anne.sudoku.model;

import org.anne.sudoku.grader.UnitType;

import java.util.*;

public class Cell {
    private final int row;
    private final int col;
    private final int box;
    int value;
    private final BitSet candidates = new BitSet(9);
    private boolean justSolved = false;
    private boolean isGiven;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.box = (row / 3) * 3 + col / 3;
        this.value = 0;
        this.isGiven = false;
        for (int i = 1; i <= 9; i++) {
            candidates.set(i);
        }
    }

    public Cell(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.box = (row / 3) * 3 + col / 3;
        setValue(value);
        this.isGiven = true;
    }

    public void setGiven() {
        this.isGiven = true;
    }

    public void resetGiven() {
        this.isGiven = false;
    }

    public boolean isGiven() {
        return this.isGiven;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getBox() {
        return box;
    }

    public int getValue() {
        return value;
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

    public void setValue(Integer value) {
        this.value = value;
        this.candidates.clear();
        this.justSolved = true;
    }

    public boolean hasCandidate(int digit) {
        return candidates.get(digit);
    }

    public int getCandidateCount() {
        return candidates.cardinality();
    }

    public int getFirstCandidate() {
        return candidates.nextSetBit(0);
    }

    public boolean removeCandidate(int digit) {
        if (candidates.get(digit)) {
            candidates.set(digit, false);
            return true;
        }
        return false;
    }

    public List<Integer> removeCandidates(List<Integer> digits) {
        List<Integer> removed = new ArrayList<>();
        for (int digit : digits) {
            if (removeCandidate(digit)) {
                removed.add(digit);
            }
        }
        return removed;
    }

    public void removeCandidates(BitSet valuesToRemove) {
        this.candidates.andNot(valuesToRemove);
    }

    public void clearCandidates() {
        this.candidates.clear();
    }

    public boolean isCandidate(int digit) {
        return candidates.get(digit);
    }

    public boolean isNotSolved() {
        return value == 0;
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

    public boolean isSolved() {
        return value != 0;
    }

    public boolean isPeer(Cell other) {
        return this != other && (row == other.row || col == other.col || box == other.box);
    }

    public List<UnitType> getCommonUnitType(Cell other) {
        List<UnitType> unitTypes = new ArrayList<>();
        if (this.row == other.row) unitTypes.add(UnitType.ROW);
        if (this.col == other.col) unitTypes.add(UnitType.COL);
        if (this.box == other.box) unitTypes.add(UnitType.BOX);
        return unitTypes;
    }

    @Override
    public String toString() {
        String LETTERS = "ABCDEFGHJ";
        return String.format("%s%s", LETTERS.charAt(row), col + 1);
    }

    public boolean isBiValue() {
        return candidates.cardinality() == 2;
    }

    public int getUnitIndex(UnitType unitType) {
        return switch (unitType) {
            case ROW -> row;
            case COL -> col;
            case BOX -> box;
        };
    }

    public int getHorizontalChute() {
        return getBox() / 3;
    }

    public int getVerticalChute() {
        return getBox() % 3;
    }

    public void unsetJustSolved() {
        this.justSolved = false;
    }

    public boolean isJustSolved() {
        return justSolved;
    }
}
