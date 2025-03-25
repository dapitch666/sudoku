package org.anne.sudoku.grader;

import java.util.ArrayList;
import java.util.List;

public class Cell {
    int row;
    int column;
    int square;
    int value;
    List<Integer> candidates;
    String position;
    boolean justSolved = false;

    public Cell(int row, int column) {
        String LETTERS = "ABCDEFGHJ";
        this.row = row;
        this.column = column;
        this.square = (row / 3) * 3 + column / 3;
        this.value = 0;
        this.candidates = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        this.position = String.format("%s%s", LETTERS.charAt(row), column + 1);
    }

    public Cell(int row, int column, int value) {
        this.row = row;
        this.column = column;
        this.square = (row / 3) * 3 + column / 3;
        setValue(value);
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int getSquare() {
        return square;
    }

    public int getValue() {
        return value;
    }

    public List<Integer> getCandidates() {
        return candidates;
    }

    public String getPosition() {
        return position;
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

    public boolean isSolved() {
        return value != 0;
    }
}
