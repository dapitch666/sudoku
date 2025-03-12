package org.anne.sudoku.solver;

import org.anne.sudoku.Utils;

public class PrintUtils {
    private static final String BAR = "+-------+-------+-------+";
    private static final String GAP = "      ";

    public static String printOne(int[] puzzle) {
        StringBuilder sb = new StringBuilder();

        sb.append("\nPuzzle:\n").append(BAR);
        for (int r = 0; r < 9; ++r) {
            sb.append("\n").append(rowString(puzzle, r));
            if (r == 2 || r == 5 || r == 8) {
                sb.append("\n").append(BAR);
            }
        }
        sb.append("\n");
        return sb.toString();
    }

    public static String printOne(String puzzle) {
        return printOne(Utils.stringToArray(puzzle));
    }

    public static String printBoth(int[] puzzle, int[] solution) {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("\n%-26s%s%s", "Puzzle:", GAP, "Solution:"));
        sb.append(String.format("\n%-23s%s%s\n", BAR, GAP + " ", BAR));

        for (int r = 0; r < 9; ++r) {
            sb.append(String.format("%s%s%s\n", rowString(puzzle, r), GAP, rowString(solution, r)));
            if (r == 2 || r == 5 || r == 8) {
                sb.append(String.format("%s%s %s\n", BAR, GAP, BAR));
            }
        }
        return sb.toString();
    }

    public static String printBoth(String puzzle, int[] solution) {
        return printBoth(Utils.stringToArray(puzzle), solution);
    }

    public static String printBoth(String puzzle, String solution) {
        return printBoth(Utils.stringToArray(puzzle), Utils.stringToArray(solution));
    }

    private static String rowString(int[] grid, int r) {
        StringBuilder row = new StringBuilder();
        for (int s = r * 9; s < (r + 1) * 9; ++s) {
            if (s % 9 == 0) {
                row.append("| ");
            }
            row.append(grid[s] == 0 ? "." : grid[s]);
            row.append(s % 9 == 2 || s % 9 == 5 || s % 9 == 8 ? " | " : " ");
        }
        return row.toString();
    }
}
