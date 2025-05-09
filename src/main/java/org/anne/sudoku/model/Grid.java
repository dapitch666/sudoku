package org.anne.sudoku.model;

import org.anne.sudoku.Grade;

import java.util.*;
import java.util.function.Predicate;

public class Grid {
    private final Cell[] cells = new Cell[81];
    private final BitSet mask = new BitSet(81);
    private final boolean[][] rows = new boolean[9][10];
    private final boolean[][] cols = new boolean[9][10];
    private final boolean[][] boxes = new boolean[9][10];
    private Grade grade = Grade.UNKNOWN;
    private final GridOutput output;

    public Grid() {
        for (int i = 0; i < 81; i++) {
            cells[i] = new Cell(i, new BitSet(9));
        }
        output = new GridOutput(this);
    }

    public Grid(String input) {
        this();
        if (input.length() != 81) {
            throw new IllegalArgumentException("Invalid puzzle length");
        }
        if (!input.matches("[0-9.]+")) {
            throw new IllegalArgumentException("Invalid puzzle format");
        }
        for (int i = 0; i < 81; i++) {
            char c = input.charAt(i);
            if (c > '0' && c <= '9') {
                set(i, c - '0', true);
            } else {
                set(i, 0, false);
            }
        }
    }

    public void set(int index, int value, boolean isClue) {
        if (value != 0) {
            rows[index / 9][value] = true;
            cols[index % 9][value] = true;
            boxes[(index / 9) / 3 * 3 + (index % 9) / 3][value] = true;
            cells[index].setValue(value);
            cells[index].candidates().clear();
        } else {
            cells[index].candidates().set(1, 10);
        }
        if (isClue) {
            mask.set(index);
        } else {
            mask.clear(index);
        }
    }

    public void clear(int index) {
        int value = get(index);
        cells[index].clear();
        rows[index / 9][value] = false;
        cols[index % 9][value] = false;
        boxes[(index / 9) / 3 * 3 + (index % 9) / 3][value] = false;
        mask.clear(index);
    }

    public int get(int index) {
        return cells[index].getValue();
    }

    public int get(int row, int col) {
        return get(row * 9 + col);
    }

    public boolean isValidDigit(int index, int digit) {
        return !rows[index / 9][digit] && !cols[index % 9][digit] && !boxes[(index / 9) / 3 * 3 + (index % 9) / 3][digit];
    }

    public Cell getBestCell() {
        int minCandidates = 10;
        Cell bestCell = null;

        for (int index = 0; index < 81; index++) {
            if (get(index) == 0) {
                BitSet candidateSet = new BitSet(9);
                for (int digit = 1; digit <= 9; digit++) {
                    if (isValidDigit(index, digit)) {
                        candidateSet.set(digit);
                    }
                }
                int options = candidateSet.cardinality();
                if (options < 2) return new Cell(index, candidateSet);
                if (options < minCandidates) {
                    minCandidates = options;
                    bestCell = new Cell(index, candidateSet);
                }
            }
        }
        return bestCell;
    }

    public boolean isSolved() {
        for (int i = 0; i < 81; i++) {
            if (get(i) == 0) return false;
        }
        return true;
    }

    public String getPuzzle() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            sb.append(mask.get(i) ? get(i) : ".");
        }
        return sb.toString();
    }

    public String getSolution() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            sb.append(get(i));
        }
        return sb.toString();
    }

    public int getClueCount() {
        return mask.cardinality();
    }

    public boolean isClue(int i) {
        return mask.get(i);
    }

    public Grade getGrade() {
        if (grade == Grade.UNKNOWN) {
            // TODO: Implement logic to determine the grade
        }
        return grade;
    }

    public Cell getCell(int i) {
        return cells[i];
    }

    public Cell getCell(int row, int col) {
        return cells[row * 9 + col];
    }

    public Cell[] getCells(Predicate<Cell> predicate) {
        return Arrays.stream(cells)
                .filter(predicate)
                .toArray(Cell[]::new);
    }

    @Override
    public String toString() {
        return output.output();
    }

    public void checkForSolvedCells() {
        for (Cell cell : getCells(Predicates.justSolvedCells)) {
            int digit = get(cell.index());
            for (Cell peer : getCells(Predicates.peers(cell).and(Predicates.unsolvedCells).and(Predicates.hasCandidate(digit)))) {
                peer.candidates().clear(digit);
            }
            cell.unsetJustSolved();
        }
    }

    public Map<Cell, List<Cell>> findLinks(int digit, boolean isStrong) {
        Map<Cell, List<Cell>> links = new HashMap<>();
        for (Cell cell : getCells(Predicates.hasCandidate(digit))) {
            List<Cell> peers = Arrays.stream(getCells(Predicates.peers(cell).and(Predicates.hasCandidate(digit))))
                    .filter(c -> !isStrong || isStrongLink(cell, c, digit))
                    .toList();
            if (!peers.isEmpty()) {
                links.put(cell, peers);
            }
        }
        return links;
    }

    public void showPossible() {
        for (Cell cell : getCells(Predicates.unsolvedCells)) {
            BitSet values = Arrays.stream(getCells(Predicates.peers(cell).and(Predicates.solvedCells)))
                    .mapToInt(c -> get(c.index()))
                    .collect(BitSet::new, BitSet::set, BitSet::or);
            cell.removeCandidates(values);
        }
    }

    public String currentState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            if (cells[i].isSolved()) {
                sb.append(get(i));
            } else {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public boolean isConjugatePair(Cell cell1, Cell cell2, int digit) {
        return cell1.hasCandidate(digit) && cell2.hasCandidate(digit)
                && getCells(Predicates.peers(cell1).and(Predicates.peers(cell2)).and(Predicates.hasCandidate(digit)))
                .length == 0;
    }

    public boolean isStrongLink(Cell cell1, Cell cell2, int digit) {
        if (!cell1.isPeer(cell2)) {
            return false;
        }
        boolean isStrong = false;
        for (UnitType unitType : cell1.getCommonUnitType(cell2)) {
            int unitIndex = switch (unitType) {
                case ROW -> cell1.getRow();
                case COL -> cell1.getCol();
                case BOX -> cell1.getBox();
            };
            if (getCells(c -> c.getUnitIndex(unitType) == unitIndex && c.hasCandidate(digit)).length == 2) {
                isStrong = true;
            }
        }
        return isStrong;
    }

    public Cell findFourthCorner(Cell cell1, Cell cell2, Cell cell3) {
        int row4 = (cell1.getRow() == cell2.getRow()) ? cell3.getRow() : (cell1.getRow() == cell3.getRow()) ? cell2.getRow() : cell1.getRow();
        int col4 = (cell1.getCol() == cell2.getCol()) ? cell3.getCol() : (cell1.getCol() == cell3.getCol()) ? cell2.getCol() : cell1.getCol();
        return getCell(row4, col4);
    }

}