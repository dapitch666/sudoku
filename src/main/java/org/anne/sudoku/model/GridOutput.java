package org.anne.sudoku.model;

public class GridOutput {
    private final Grid grid;

    private static final String BAR = "+-------+-------+-------+";
    private static final String GAP = "      ";

    public GridOutput(Grid grid) {
        this.grid = grid;
    }

    public String printOne(boolean isPuzzle) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nPuzzle:\n").append(BAR);
        for (int r = 0; r < 9; ++r) {
            sb.append("\n").append(rowString(isPuzzle, r));
            if (r == 2 || r == 5 || r == 8) {
                sb.append("\n").append(BAR);
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public String printPuzzle() {
        return printOne(true);
    }

    public String printSolution() {
        return printOne(false);
    }

    public String printPuzzleAndSolution() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("\n%-26s%s%s", "Puzzle:", GAP, "Solution:"));
        sb.append(String.format("\n%-23s%s%s\n", BAR, GAP + " ", BAR));

        for (int r = 0; r < 9; ++r) {
            sb.append(String.format("%s%s%s\n", rowString(true, r), GAP, rowString(false, r)));
            if (r == 2 || r == 5 || r == 8) {
                sb.append(String.format("%s%s %s\n", BAR, GAP, BAR));
            }
        }
        return sb.toString();
    }

    private String rowString(boolean puzzle, int r) {
        StringBuilder row = new StringBuilder();
        for (int s = r * 9; s < (r + 1) * 9; ++s) {
            if (s % 9 == 0) {
                row.append("| ");
            }
            row.append(puzzle && !grid.isClue(s) ? "." : grid.get(s));
            row.append(s % 9 == 2 || s % 9 == 5 || s % 9 == 8 ? " | " : " ");
        }
        return row.toString();
    }

    public String output() {
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

    private String getCell(int i, int line) {
        Cell cell = grid.getCell(i);
        if (!cell.isSolved()) {
            return switch (line) {
                case 0 -> formatCandidates(cell, 1, 2, 3);
                case 1 -> formatCandidates(cell, 4, 5, 6);
                case 2 -> formatCandidates(cell, 7, 8, 9);
                default -> throw new IllegalStateException("Unexpected value: " + line);
            };
        } else {
            return switch (line) {
                case 1 -> String.format(" %d ", cell.value());
                case 0, 2 -> "   ";
                default -> throw new IllegalStateException("Unexpected value: " + line);
            };
        }
    }

    private String formatCandidates(Cell cell, int... candidates) {
        StringBuilder sb = new StringBuilder();
        for (int candidate : candidates) {
            sb.append(cell.hasCandidate(candidate) ? candidate : ".");
        }
        return sb.toString();
    }
}
