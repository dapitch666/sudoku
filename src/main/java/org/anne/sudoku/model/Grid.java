package org.anne.sudoku.model;

import org.anne.sudoku.Grade;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Grid {
    private final int[] grid = new int[81];
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
        grid[index] = value;
        // cells[index] = new Cell(index, new BitSet(9));
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
        int value = grid[index];
        grid[index] = 0;
        rows[index / 9][value] = false;
        cols[index % 9][value] = false;
        boxes[(index / 9) / 3 * 3 + (index % 9) / 3][value] = false;
        mask.clear(index);
    }

    public int get(int index) {
        return grid[index];
    }

    public int get(int row, int col) {
        return grid[row * 9 + col];
    }

    public void backtrack(int index, int digit) {
        grid[index] = 0;
        rows[index / 9][digit] = false;
        cols[index % 9][digit] = false;
        boxes[(index / 9) / 3 * 3 + (index % 9) / 3][digit] = false;
    }

    public boolean isValidDigit(int index, int digit) {
        return !rows[index / 9][digit] && !cols[index % 9][digit] && !boxes[(index / 9) / 3 * 3 + (index % 9) / 3][digit];
    }

    public Cell getBestCell() {
        int minCandidates = 10;
        Cell bestCell = null;

        for (int index = 0; index < 81; index++) {
            if (grid[index] == 0) {
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
            if (grid[i] == 0) return false;
        }
        return true;
    }

    public String getPuzzle() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            sb.append(mask.get(i) ? grid[i] : ".");
        }
        return sb.toString();
    }

    public String getSolution() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            sb.append(grid[i]);
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

    public Cell[] getSolvedCells() {
        return Arrays.stream(cells).filter(Cell::isSolved).toArray(Cell[]::new);
    }

    public Cell[] getUnsolvedCells() {
        return Arrays.stream(cells).filter(cell -> !cell.isSolved()).toArray(Cell[]::new);
    }

    public Cell[] getCellsWithNCandidates(int n) {
        return Arrays.stream(cells).filter(cell -> cell.getCandidateCount() == n).toArray(Cell[]::new);
    }


    @Override
    public String toString() {
        return output.output();
    }

    public void checkForSolvedCells() {
        for (Cell cell : cells) {
            if (cell.justSolved()) {
                int digit = grid[cell.index()];
                for (Cell peer : getPeers(cell)) {
                    if (!peer.isSolved() && peer.hasCandidate(digit)) {
                        peer.candidates().clear(digit);
                    }
                }
                cell.unsetJustSolved();
            }
        }
    }

    public Cell[] getPeers(Cell cell) {
        return Arrays.stream(cells)
                .filter(c -> c != cell && (c.getRow() == cell.getRow() || c.getCol() == cell.getCol() || c.getBox() == cell.getBox()))
                .toArray(Cell[]::new);
    }

    public Cell[] getCellsInUnitWithCandidate(int digit, UnitType unitType, int unitIndex) {
        return Arrays.stream(cells)
                .filter(cell -> cell.getUnitIndex(unitType) == unitIndex && cell.hasCandidate(digit))
                .toArray(Cell[]::new);
    }

    public Cell[] getCells(UnitType unitType, int unitIndex) {
        return Arrays.stream(cells)
                .filter(cell -> cell.getUnitIndex(unitType) == unitIndex)
                .toArray(Cell[]::new);
    }

    public Map<Cell, List<Cell>> findStrongLinks(int digit) {
        Map<Cell, List<Cell>> strongLinks = new HashMap<>();
        for (Cell cell : getCells(c -> c.hasCandidate(digit))) {
            List<Cell> peers = Arrays.stream(getPeers(cell)).filter(c -> c.hasCandidate(digit) && isStrongLink(cell, c, digit)).toList();
            if (!peers.isEmpty()) {
                strongLinks.put(cell, peers);
            }
        }
        return strongLinks;
    }

    public Map<Cell, List<Cell>> findWeakLinks(int digit) {
        Map<Cell, List<Cell>> weakLinks = new HashMap<>();
        for (Cell cell : getCells(c -> c.hasCandidate(digit))) {
            List<Cell> peers = Arrays.stream(getPeers(cell)).filter(c -> c.hasCandidate(digit)).toList();
            if (!peers.isEmpty()) {
                weakLinks.put(cell, peers);
            }
        }

        return weakLinks;
    }


    public void showPossible() {
        for (Cell cell : cells) {
            if (!cell.isSolved()) {
                Set<Integer> values = Arrays.stream(getPeers(cell))
                        .filter(Cell::isSolved)
                        .mapToInt(c -> get(c.index()))
                        .boxed()
                        .collect(Collectors.toSet());
                for (int value : values) {
                    cell.candidates().clear(value);
                }
            }
        }
    }

    public Cell[] getCells(Predicate<Cell> predicate) {
        return Arrays.stream(cells)
                .filter(predicate)
                .toArray(Cell[]::new);
    }

    public List<Cell> getCommonPeersWithCandidate(Cell cell1, Cell cell2, int digit) {
        List<Cell> commonPeers = new ArrayList<>();
        for (Cell peer : getPeers(cell1)) {
            if (peer.isPeer(cell2) && peer.hasCandidate(digit)) {
                commonPeers.add(peer);
            }
        }
        return commonPeers;
    }

    public String currentState() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            if (cells[i].isSolved()) {
                sb.append(cells[i].getValue());
            } else {
                sb.append(".");
            }
        }
        return sb.toString();
    }

    public boolean isConjugatePair(Cell cell1, Cell cell2, int digit) {
        return cell1.hasCandidate(digit) && cell2.hasCandidate(digit) && getCommonPeersWithCandidate(cell1, cell2, digit).isEmpty();
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

    public Cell[] getBiValueCells() {
        return Arrays.stream(cells).filter(Cell::isBiValue).toArray(Cell[]::new);
    }

    public Cell findFourthCorner(Cell cell1, Cell cell2, Cell cell3) {
        int row4 = (cell1.getRow() == cell2.getRow()) ? cell3.getRow() : (cell1.getRow() == cell3.getRow()) ? cell2.getRow() : cell1.getRow();
        int col4 = (cell1.getCol() == cell2.getCol()) ? cell3.getCol() : (cell1.getCol() == cell3.getCol()) ? cell2.getCol() : cell1.getCol();
        return getCell(row4, col4);
    }

    public Cell[] getCellsInUnitWithCandidates(List<Integer> candidates, UnitType unitType, int unitIndex) {
        return Arrays.stream(getCells(unitType, unitIndex)).filter(c -> new HashSet<>(c.getCandidates()).containsAll(candidates)).toArray(Cell[]::new);
    }
}