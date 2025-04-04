package org.anne.sudoku.grader;

import java.util.*;
import java.util.stream.Collectors;

public class Grid {
    String puzzle;
    Cell[] cells;

    public Grid(String puzzle) {
        this.puzzle = puzzle;
        cells = new Cell[81];
        for (int i = 0; i < 81; i++) {
            int row = i / 9;
            int column = i % 9;
            cells[i] = new Cell(row, column);
            if (puzzle.charAt(i) != '.' && puzzle.charAt(i) != '0') {
                cells[i] = new Cell(row, column, puzzle.charAt(i) - '0');
            } else {
                cells[i] = new Cell(row, column);
            }
        }
    }

    boolean isSolved() {
        for (int i = 0; i < 81; i++) {
            if (cells[i].value == 0) {
                return false;
            }
        }
        return true;
    }

    String currentState() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            stringBuilder.append(cells[i].value);
        }
        return stringBuilder.toString();
    }

    Cell[] getRow(int row) {
        Cell[] rowCells = new Cell[9];
        System.arraycopy(cells, row * 9, rowCells, 0, 9);
        return rowCells;
    }

    Cell[] getCol(int col) {
        Cell[] colCells = new Cell[9];
        for (int i = 0; i < 9; i++) {
            colCells[i] = cells[i * 9 + col];
        }
        return colCells;
    }

    Cell[] getBox(int box) {
        Cell[] boxCells = new Cell[9];
        int row = box / 3;
        int col = box % 3;
        for (int i = 0; i < 9; i++) {
            boxCells[i] = cells[(row * 3 + i / 3) * 9 + col * 3 + i % 3];
        }
        return boxCells;
    }

    public Cell[] getCells(UnitType unitType, int unitIndex) {
        return switch (unitType) {
            case ROW -> getRow(unitIndex);
            case COL -> getCol(unitIndex);
            case BOX -> getBox(unitIndex);
        };
    }

    public Cell[] getUnsolvedCells() {
        return Arrays.stream(cells).filter(Cell::isNotSolved).toArray(Cell[]::new);
    }

    public Cell[] getPeers(Cell cell) {
        Set<Cell> peers = Arrays.stream(getRow(cell.getRow())).collect(Collectors.toSet());
        peers.addAll(Arrays.stream(getCol(cell.getCol())).collect(Collectors.toSet()));
        peers.addAll(Arrays.stream(getBox(cell.getBox())).collect(Collectors.toSet()));
        return peers.stream().filter(c -> c != cell).toArray(Cell[]::new);
    }

    public Cell[] getCellsInUnitWithCandidate(int candidate, UnitType unitType, int unitIndex) {
        return Arrays.stream(getCells(unitType, unitIndex)).filter(c -> c.isCandidate(candidate)).toArray(Cell[]::new);
    }

    public Cell[] getCellsWithNCandidates(int n) {
        return Arrays.stream(cells).filter(c -> c.getCandidateCount() == n).toArray(Cell[]::new);
    }

    public Cell[] getCellsWithCandidate(int digit) {
        return Arrays.stream(cells).filter(c -> c.isCandidate(digit)).toArray(Cell[]::new);
    }


    public Cell[] getBiValueCells() {
        return Arrays.stream(cells).filter(Cell::isBiValue).toArray(Cell[]::new);
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
            if (getCellsInUnitWithCandidate(digit, unitType, unitIndex).length == 2) {
                isStrong = true;
            }
        }
        return isStrong;
    }

    public List<Cell> getCommonPeersWithCandidate(Cell cell1, Cell cell2, int digit) {
        List<Cell> commonPeers = new ArrayList<>();
        for (Cell peer : getPeers(cell1)) {
            if (peer.isPeer(cell2) && peer.isCandidate(digit)) {
                commonPeers.add(peer);
            }
        }
        return commonPeers;
    }

    public Map<Cell, List<Cell>> findStrongLinks(int digit) {
        Map<Cell, List<Cell>> strongLinks = new HashMap<>();
        for (Cell cell : getCellsWithCandidate(digit)) {
            List<Cell> peers = Arrays.stream(getPeers(cell)).filter(c -> c.isCandidate(digit) && isStrongLink(cell, c, digit)).toList();
            if (!peers.isEmpty()) {
                strongLinks.put(cell, peers);
            }
        }
        return strongLinks;
    }

    public Map<Cell, List<Cell>> findWeakLinks(int digit) {
        Map<Cell, List<Cell>> weakLinks = new HashMap<>();
        for (Cell cell : getCellsWithCandidate(digit)) {
            List<Cell> peers = Arrays.stream(getPeers(cell)).filter(c -> c.isCandidate(digit)).toList();
            if (!peers.isEmpty()) {
                weakLinks.put(cell, peers);
            }
        }

        return weakLinks;
    }



    /* ************************************************** */

    public void checkForSolvedCells() {
        for (int i = 0; i < 81; i++) {
            if (cells[i].justSolved) {
                Cell cell = cells[i];
                int value = cell.value;
                for (Cell peer : getPeers(cell)) {
                    if (peer.isNotSolved()) {
                        peer.removeCandidate(value);
                    }
                }
                cell.justSolved = false;
            }
        }
    }

    public void showPossible() {
        for (Cell cell : getUnsolvedCells()) {
            Set<Integer> values = Arrays.stream(getRow(cell.getRow())).filter(Cell::isSolved).map(Cell::getValue).collect(Collectors.toSet());
            values.addAll(Arrays.stream(getCol(cell.getCol())).filter(Cell::isSolved).map(Cell::getValue).collect(Collectors.toSet()));
            values.addAll(Arrays.stream(getBox(cell.getBox())).filter(Cell::isSolved).map(Cell::getValue).collect(Collectors.toSet()));
            for (int value : values) {
                cell.removeCandidate(value);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("""
                     1   2   3     4   5   6     7   8   9
                  +-------------+-------------+-------------+
                  | %s %s %s | %s %s %s | %s %s %s |
                A | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  |             |             |             |
                  | %s %s %s | %s %s %s | %s %s %s |
                B | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  |             |             |             |
                  | %s %s %s | %s %s %s | %s %s %s |
                C | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  +-------------+-------------+-------------+
                  | %s %s %s | %s %s %s | %s %s %s |
                D | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  |             |             |             |
                  | %s %s %s | %s %s %s | %s %s %s |
                E | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  |             |             |             |
                  | %s %s %s | %s %s %s | %s %s %s |
                F | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  +-------------+-------------+-------------+
                  | %s %s %s | %s %s %s | %s %s %s |
                G | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  |             |             |             |
                  | %s %s %s | %s %s %s | %s %s %s |
                H | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  |             |             |             |
                  | %s %s %s | %s %s %s | %s %s %s |
                J | %s %s %s | %s %s %s | %s %s %s |
                  | %s %s %s | %s %s %s | %s %s %s |
                  +-------------+-------------+-------------+
                """, getCell(0, 0), getCell(1, 0), getCell(2, 0), getCell(3, 0), getCell(4, 0), getCell(5, 0), getCell(6, 0), getCell(7, 0), getCell(8, 0),
                getCell(0, 1), getCell(1, 1), getCell(2, 1), getCell(3, 1), getCell(4, 1), getCell(5, 1), getCell(6, 1), getCell(7, 1), getCell(8, 1),
                getCell(0, 2), getCell(1, 2), getCell(2, 2), getCell(3, 2), getCell(4, 2), getCell(5, 2), getCell(6, 2), getCell(7, 2), getCell(8, 2),
                getCell(9, 0), getCell(10, 0), getCell(11, 0), getCell(12, 0), getCell(13, 0), getCell(14, 0), getCell(15, 0), getCell(16, 0), getCell(17, 0),
                getCell(9, 1), getCell(10, 1), getCell(11, 1), getCell(12, 1), getCell(13, 1), getCell(14, 1), getCell(15, 1), getCell(16, 1), getCell(17, 1),
                getCell(9, 2), getCell(10, 2), getCell(11, 2), getCell(12, 2), getCell(13, 2), getCell(14, 2), getCell(15, 2), getCell(16, 2), getCell(17, 2),
                getCell(18, 0), getCell(19, 0), getCell(20, 0), getCell(21, 0), getCell(22, 0), getCell(23, 0), getCell(24, 0), getCell(25, 0), getCell(26, 0),
                getCell(18, 1), getCell(19, 1), getCell(20, 1), getCell(21, 1), getCell(22, 1), getCell(23, 1), getCell(24, 1), getCell(25, 1), getCell(26, 1),
                getCell(18, 2), getCell(19, 2), getCell(20, 2), getCell(21, 2), getCell(22, 2), getCell(23, 2), getCell(24, 2), getCell(25, 2), getCell(26, 2),
                getCell(27, 0), getCell(28, 0), getCell(29, 0), getCell(30, 0), getCell(31, 0), getCell(32, 0), getCell(33, 0), getCell(34, 0), getCell(35, 0),
                getCell(27, 1), getCell(28, 1), getCell(29, 1), getCell(30, 1), getCell(31, 1), getCell(32, 1), getCell(33, 1), getCell(34, 1), getCell(35, 1),
                getCell(27, 2), getCell(28, 2), getCell(29, 2), getCell(30, 2), getCell(31, 2), getCell(32, 2), getCell(33, 2), getCell(34, 2), getCell(35, 2),
                getCell(36, 0), getCell(37, 0), getCell(38, 0), getCell(39, 0), getCell(40, 0), getCell(41, 0), getCell(42, 0), getCell(43, 0), getCell(44, 0),
                getCell(36, 1), getCell(37, 1), getCell(38, 1), getCell(39, 1), getCell(40, 1), getCell(41, 1), getCell(42, 1), getCell(43, 1), getCell(44, 1),
                getCell(36, 2), getCell(37, 2), getCell(38, 2), getCell(39, 2), getCell(40, 2), getCell(41, 2), getCell(42, 2), getCell(43, 2), getCell(44, 2),
                getCell(45, 0), getCell(46, 0), getCell(47, 0), getCell(48, 0), getCell(49, 0), getCell(50, 0), getCell(51, 0), getCell(52, 0), getCell(53, 0),
                getCell(45, 1), getCell(46, 1), getCell(47, 1), getCell(48, 1), getCell(49, 1), getCell(50, 1), getCell(51, 1), getCell(52, 1), getCell(53, 1),
                getCell(45, 2), getCell(46, 2), getCell(47, 2), getCell(48, 2), getCell(49, 2), getCell(50, 2), getCell(51, 2), getCell(52, 2), getCell(53, 2),
                getCell(54, 0), getCell(55, 0), getCell(56, 0), getCell(57, 0), getCell(58, 0), getCell(59, 0), getCell(60, 0), getCell(61, 0), getCell(62, 0),
                getCell(54, 1), getCell(55, 1), getCell(56, 1), getCell(57, 1), getCell(58, 1), getCell(59, 1), getCell(60, 1), getCell(61, 1), getCell(62, 1),
                getCell(54, 2), getCell(55, 2), getCell(56, 2), getCell(57, 2), getCell(58, 2), getCell(59, 2), getCell(60, 2), getCell(61, 2), getCell(62, 2),
                getCell(63, 0), getCell(64, 0), getCell(65, 0), getCell(66, 0), getCell(67, 0), getCell(68, 0), getCell(69, 0), getCell(70, 0), getCell(71, 0),
                getCell(63, 1), getCell(64, 1), getCell(65, 1), getCell(66, 1), getCell(67, 1), getCell(68, 1), getCell(69, 1), getCell(70, 1), getCell(71, 1),
                getCell(63, 2), getCell(64, 2), getCell(65, 2), getCell(66, 2), getCell(67, 2), getCell(68, 2), getCell(69, 2), getCell(70, 2), getCell(71, 2),
                getCell(72, 0), getCell(73, 0), getCell(74, 0), getCell(75, 0), getCell(76, 0), getCell(77, 0), getCell(78, 0), getCell(79, 0), getCell(80, 0),
                getCell(72, 1), getCell(73, 1), getCell(74, 1), getCell(75, 1), getCell(76, 1), getCell(77, 1), getCell(78, 1), getCell(79, 1), getCell(80, 1),
                getCell(72, 2), getCell(73, 2), getCell(74, 2), getCell(75, 2), getCell(76, 2), getCell(77, 2), getCell(78, 2), getCell(79, 2), getCell(80, 2));
    }

    private String getCell (int i, int line) {
        Cell cell = cells[i];
        if (cell.isNotSolved()) {
            return switch (line) {
                case 0 -> formatCandidates(cell, 1, 2, 3);
                case 1 -> formatCandidates(cell, 4, 5, 6);
                case 2 -> formatCandidates(cell, 7, 8, 9);
                default -> throw new IllegalStateException("Unexpected value: " + line);
            };
        }
        else {
            return switch (line) {
                case 1 -> String.format(" %d ", cell.value);
                case 0, 2 -> "   ";
                default -> throw new IllegalStateException("Unexpected value: " + line);
            };
        }
    }

    private String formatCandidates(Cell cell, int... candidates) {
        StringBuilder sb = new StringBuilder();
        for (int candidate : candidates) {
            sb.append(cell.candidates.contains(candidate) ? candidate : ".");
        }
        return sb.toString();
    }
}
