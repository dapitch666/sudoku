package org.anne.sudoku;

import org.anne.sudoku.crawler.SudokuWiki;
import org.anne.sudoku.generator.PuzzleGenerator;
import org.anne.sudoku.generator.SolutionGenerator;

public class Sudoku {
    public final String grid;
    public final String mask;
    private Grade grade;

    public Sudoku(String grid, String mask) {
        this.grid = grid;
        this.mask = mask;
    }

    public Sudoku() {
        this.grid = new SolutionGenerator().generate();
        this.mask = generateMaskFromPuzzle(new PuzzleGenerator(grid).generate());
    }

    private String generateMaskFromPuzzle(String puzzle) {
        StringBuilder mask = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            mask.append(puzzle.charAt(i) == '.' ? '0' : '1');
        }
        return mask.toString();
    }

    public String getPuzzle() {
        StringBuilder puzzle = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            puzzle.append(mask.charAt(i) == '0' ? '.' : grid.charAt(i));
        }
        return puzzle.toString();
    }

    public Grade getGrade() {
        if (grade == null) {
            grade = new SudokuWiki().getGrade(getPuzzle());
        }
        return grade;
    }

    public boolean isEditable(int position) {
        return mask.charAt(position) == '0';
    }

    public static void main(String[] args) {
        Sudoku sudoku = new Sudoku();
        System.out.println(sudoku.getPuzzle());
        System.out.println(sudoku.grid);
        System.out.println(sudoku.mask);
        System.out.println(sudoku.getGrade());
    }
}
