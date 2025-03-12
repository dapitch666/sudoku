package org.anne.sudoku;

public class Utils {
    public static int[] stringToArray(String puzzle) {
        int[] puzzleArray = new int[81];
        for (int i = 0; i < 81; i++) {
            char c = puzzle.charAt(i);
            puzzleArray[i] = c == '.' ? 0 : c - '0';
        }
        return puzzleArray;
    }

    public static boolean isValidPuzzle(String input) {
        int[] puzzle = new int[81];
        for (int i = 0; i < input.length(); i++) {
            puzzle[i] = input.charAt(i) - '0';
            if (puzzle[i] < 0 || puzzle[i] > 9) {
                return false;
            }
        }
        // check if the numbers appear only once in each row, column and square
        for (int i = 0; i < 9; i++) {
            int[] row = new int[9];
            int[] col = new int[9];
            int[] square = new int[9];
            for (int j = 0; j < 9; j++) {
                row[j] = puzzle[i * 9 + j];
                col[j] = puzzle[j * 9 + i];
                square[j] = puzzle[(i / 3 * 3 + j / 3) * 9 + i % 3 * 3 + j % 3];
            }
            if (isNotValid(row) || isNotValid(col) || isNotValid(square)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNotValid(int[] array) {
        boolean[] seen = new boolean[10];
        for (int i : array) {
            if (i != 0) {
                if (seen[i]) {
                    return true;
                }
                seen[i] = true;
            }
        }
        return false;
    }

    public static boolean isAlreadySolved(String puzzle) {
        for (int i = 0; i < puzzle.length(); i++) {
            if (puzzle.charAt(i) == '0') {
                return false;
            }
        }
        return true;
    }

    public static String arrayToString(int[] puzzle) {
        StringBuilder sb = new StringBuilder();
        for (int i : puzzle) {
            sb.append(i == 0 ? "." : String.valueOf(i));
        }
        return sb.toString();
    }
}
