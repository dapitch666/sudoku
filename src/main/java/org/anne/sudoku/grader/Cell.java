package org.anne.sudoku.grader;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    int row;
    int col;
    int box;
    int value;
    List<Integer> candidates;
    boolean justSolved = false;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.box = (row / 3) * 3 + col / 3;
        this.value = 0;
        this.candidates = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
    }

    public Cell(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.box = (row / 3) * 3 + col / 3;
        setValue(value);
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
        return candidates;
    }

    public void setValue(Integer value) {
        this.value = value;
        this.candidates = List.of();
        this.justSolved = true;
    }

    public int getCandidateCount() {
        return candidates.size();
    }

    public int getFirstCandidate() {
        return candidates.getFirst();
    }

    public boolean removeCandidate(int candidate) {
        if (isCandidate(candidate)) {
            return candidates.remove((Integer) candidate);
        }
        return false;
    }

    public List<Integer> removeCandidates(List<Integer> candidatesToRemove) {
        List<Integer> removed = new ArrayList<>();
        for (int candidate : candidatesToRemove) {
            if (removeCandidate(candidate)) {
                removed.add(candidate);
            }
        }
        return removed;
    }

    public boolean isCandidate(int i) {
        return candidates.contains(i);
    }

    public boolean isNotSolved() {
        return value == 0;
    }

    public List<Integer> removeAllBut(List<Integer> i) {
        List<Integer> removed = candidates.stream().filter(c -> !i.contains(c)).toList();
        for (int candidate : removed) {
            removeCandidate(candidate);
        }
        return removed;
    }

    public List<Integer> removeAllBut(int i) {
        List<Integer> removed = candidates.stream().filter(c -> c != i).toList();
        for (int candidate : removed) {
            removeCandidate(candidate);
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
        return candidates.size() == 2;
    }

    public int getUnitIndex(UnitType unitType) {
        return switch (unitType) {
            case ROW -> row;
            case COL -> col;
            case BOX -> box;
        };
    }
}
