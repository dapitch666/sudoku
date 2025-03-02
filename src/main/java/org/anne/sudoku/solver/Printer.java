package org.anne.sudoku.solver;

public class Printer {
    public static void print(int[] puzzle, int[] solution) {
        String bar = "+-------+-------+-------+";
        String gap = "      ";
        if (solution == null) solution = new int[81];

        System.out.format("\n%-26s%s%s", "Puzzle:", gap, "Solution:");
        System.out.format("\n%-23s%s%s\n", bar, gap + " ", bar);

        for (int r = 0; r < 9; ++r) {
            System.out.println(rowString(puzzle, r) + gap + rowString(solution, r));
            if (r == 2 || r == 5 || r == 8) {
                System.out.println(bar + gap + " " + bar);
            }
        }
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

    public static void print(String puzzle, int[] mat) {
        int[] puzzleArray = new int[81];
        for (int i = 0; i < 81; i++) {
            puzzleArray[i] = puzzle.charAt(i) - '0';
        }
        print(puzzleArray, mat);
    }
}
