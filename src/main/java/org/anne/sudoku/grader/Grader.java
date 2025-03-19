package org.anne.sudoku.grader;

import org.anne.sudoku.utils.PrintUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Grader {
    String puzzle;
    Cell[] cells;
    int nakedSingles = 0;
    int hiddenSingles = 0;
    int nakedPairs = 0;
    int nakedTriples = 0;
    int hiddenPairs = 0;
    int hiddenTriples = 0;

    public Grader(String puzzle) {
        this.puzzle = puzzle;
        this.cells = new Cell[81];
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

    public static void main(String[] args) {
        // Grader grader = new Grader("..9.2.3.56.5....8....4.8.....7...2.....3.6.....8...67.5..2.1....3......18.6.4....");
        Grader grader = new Grader("3........97..1....6..583...2.....9..5..621..3..8.....5...435..2....9..56........1");
        System.out.println(grader.currentState());
        grader.solve();
        System.out.println(grader.currentState());
        System.out.println("Naked singles: " + grader.nakedSingles);
        System.out.println("Hidden singles: " + grader.hiddenSingles);
        System.out.println("Naked pairs: " + grader.nakedPairs);
        System.out.println("Naked triples: " + grader.nakedTriples);
        System.out.println("Hidden pairs: " + grader.hiddenPairs);
        System.out.println("Hidden triples: " + grader.hiddenTriples);
    }

    private void solve() {
        int steps = 0;
        boolean changed;
        do {
            if (isSolved()) return;
            steps++;
            System.out.println("Step " + steps + ":");
            changed = false;
            if (nakedSingles()) {
                changed = true;
                System.out.println("Naked singles found");
                continue;
            }
            if (hiddenSingles()) {
                changed = true;
                System.out.println("Hidden singles found");
                continue;
            }
            if (nakedPairs()) {
                changed = true;
                System.out.println("Naked pairs found");
                continue;
            }
            if (nakedTriples()) {
                changed = true;
                System.out.println("Naked triples found");
                continue;
            }
            if (hiddenPairs()) {
                changed = true;
                System.out.println("Hidden pairs found");
                continue;
            }
            if (hiddenTriples()) {
                changed = true;
                System.out.println("Hidden triples found");
                continue;
            }
        } while (changed);
    }

    private boolean isSolved() {
        for (int i = 0; i < 81; i++) {
            if (cells[i].value == 0) {
                return false;
            }
        }
        return true;
    }

    private String currentState() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 81; i++) {
            stringBuilder.append(cells[i].value);
        }
        return PrintUtils.printOne(stringBuilder.toString());
    }

    private boolean nakedSingles() {
        boolean changed = false;
        for (int i = 0; i < 81; i++) {
            if (cells[i].value != 0) {
                for (int j = 0; j < 81; j++) {
                    if (cells[j].row == cells[i].row || cells[j].column == cells[i].column || cells[j].square == cells[i].square) {
                        if (cells[j].value == 0 && cells[j].isCandidate(cells[i].value)) {
                            cells[j].removeCandidate(cells[i].value);
                            if (cells[j].candidates.size() == 1) {
                                cells[j].value = cells[j].candidates.getFirst();
                                cells[j].candidates = List.of();
                                changed = true;
                                nakedSingles++;
                                System.out.println("Last candidate in " + cells[j].position + " is " + cells[j].value + ". Changed to solution.");
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    private boolean hiddenSingles() {
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            changed = changed || hiddenSingles(getRow(i));
            changed = changed || hiddenSingles(getCol(i));
            changed = changed || hiddenSingles(getSquare(i));
        }
        return changed;
    }

    private boolean hiddenSingles(Cell[] cells) {
        boolean changed = false;
        for (int i = 1; i <= 9; i++) {
            List<Cell> candidates = new ArrayList<>();
            for (Cell cell : cells) {
                if (cell.isCandidate(i)) {
                    candidates.add(cell);
                }
            }
            if (candidates.size() == 1) {
                candidates.getFirst().value = i;
                candidates.getFirst().candidates = List.of();
                changed = true;
                hiddenSingles++;
                System.out.println("Only candidate in row/col/square of " + candidates.getFirst().position + " is " + i + ". Changed to solution.");
            }
        }
        return changed;
    }

    private boolean nakedPairs() {
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            changed = changed || nakedPairs(getRow(i));
            changed = changed || nakedPairs(getCol(i));
            changed = changed || nakedPairs(getSquare(i));
        }
        return changed;
    }

    private boolean nakedPairs(Cell[] cells) {
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            if (cells[i].candidates.size() == 2) {
                for (int j = i + 1; j < 9; j++) {
                    if (cells[j].candidates.size() == 2 && cells[i].candidates.equals(cells[j].candidates)) {
                        for (int k = 0; k < 9; k++) {
                            if (k != i && k != j) {
                                if (cells[k].removeCandidate(cells[i].candidates.get(0)) || cells[k].removeCandidate(cells[i].candidates.get(1))) {
                                    changed = true;
                                    nakedPairs++;
                                    System.out.println("Naked pair in " + cells[i].position+ " and " + cells[j].position + ". Removed " + cells[k].value + " from " + cells[k].position);
                                }
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    private boolean nakedTriples() {
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            changed = changed || nakedTriples(getRow(i));
            changed = changed || nakedTriples(getCol(i));
            changed = changed || nakedTriples(getSquare(i));
        }
        return changed;
    }

    // Any group of three cells in the same unit that contain IN TOTAL three candidates is a Naked Triple.
    private boolean nakedTriples(Cell[] unit) {
        boolean changed = false;
        List<Cell> cells = Arrays.stream(unit).filter(cell -> cell.candidates.size() == 2 || cell.candidates.size() == 3).toList();
        for (int i = 0; i < cells.size(); i++) {
            for (int j = i + 1; j < cells.size(); j++) {
                for (int k = j + 1; k < cells.size(); k++) {
                    Set<Integer> triple = new HashSet<>();
                    triple.addAll(cells.get(i).candidates);
                    triple.addAll(cells.get(j).candidates);
                    triple.addAll(cells.get(k).candidates);
                    if (triple.size() == 3) {
                        for (Cell cell : unit) {
                            if (cell.isNotSolved() && cell != cells.get(i) && cell != cells.get(j) && cell != cells.get(k)) {
                                List<Integer> removed = new ArrayList<>();
                                for (int candidate : triple) {
                                    if (cell.removeCandidate(candidate)) {
                                        removed.add(candidate);
                                    }
                                }
                                if (!removed.isEmpty()) {
                                    changed = true;
                                    System.out.println("Naked triple in " + cells.get(i).position + ", " + cells.get(j).position + " and " + cells.get(k).position + " removed " + removed + " from " + cell.position);
                                }
                            }
                        }
                        if (changed) {
                            nakedTriples++;
                        }
                    }
                }
            }
        }
        return changed;
    }

    private boolean hiddenPairs() {
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            changed = changed || hiddenPairs(getRow(i));
            changed = changed || hiddenPairs(getCol(i));
            changed = changed || hiddenPairs(getSquare(i));
        }
        return changed;
    }

    private boolean hiddenPairs(Cell[] unit) {
        boolean changed = false;
        List<Cell> cells = Arrays.stream(unit).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            List<Cell> candidates = new ArrayList<>();
            for (Cell cell : cells) {
                if (cell.isCandidate(i)) {
                    candidates.add(cell);
                }
            }
            if (candidates.size() == 2) {
                map.put(i, candidates);
            }
        }
        for (int i = 1; i <= 9; i++) {
            if (map.containsKey(i)) {
                for (int j = i + 1; j <= 9; j++) {
                    if (map.containsKey(j) && map.get(i).equals(map.get(j))) {
                        for (Cell cell : map.get(i)) {
                            List<Integer> removed = cell.removeAllBut(List.of(i, j));
                            if (!removed.isEmpty()) {
                                changed = true;
                                System.out.println("Hidden pair (" + i + ", " + j + ") in " + map.get(i).get(0).position + " and " + map.get(i).get(1).position + ". Removed " + removed + " from " + cell.position);
                            }
                        }
                        if (changed) hiddenPairs++;
                    }
                }
            }
        }
        return changed;
    }

    private boolean hiddenTriples() {
        boolean changed = false;
        for (int i = 0; i < 9; i++) {
            changed = changed || hiddenTriples(getRow(i));
            changed = changed || hiddenTriples(getCol(i));
            changed = changed || hiddenTriples(getSquare(i));
        }
        return changed;
    }

    private boolean hiddenTriples(Cell[] unit) {
        boolean changed = false;
        List<Cell> cells = Arrays.stream(unit).filter(Cell::isNotSolved).toList();
        Map<Integer, List<Cell>> map = new HashMap<>();
        for (int i = 1; i <= 9; i++) {
            List<Cell> candidates = new ArrayList<>();
            for (Cell cell : cells) {
                if (cell.isCandidate(i)) {
                    candidates.add(cell);
                }
            }
            if (candidates.size() == 3 || candidates.size() == 2) {
                map.put(i, candidates);
            }
        }
        for (int i = 1; i <= 9; i++) {
            if (map.containsKey(i)) {
                for (int j = i + 1; j <= 9; j++) {
                    if (map.containsKey(j)) {
                        for (int k = j + 1; k <= 9; k++) {
                            if (map.containsKey(k)) {
                                Set<Cell> triple = new HashSet<>();
                                triple.addAll(map.get(i));
                                triple.addAll(map.get(j));
                                triple.addAll(map.get(k));
                                if (triple.size() == 3) {
                                    for (Cell cell : triple) {
                                        List<Integer> removed = cell.removeAllBut(List.of(i, j, k));
                                        if (!removed.isEmpty()) {
                                            changed = true;
                                            System.out.println("Hidden triple (" + i + ", " + j + ", " + k + ") in " + triple.stream().map(c -> c.position).collect(Collectors.joining(", ")) + ". Removed " + removed + " from " + cell.position);
                                        }
                                    }
                                    if (changed) hiddenTriples++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return changed;
    }

    private Cell[] getRow(int row) {
        Cell[] rowCells = new Cell[9];
        for (int i = 0; i < 9; i++) {
            rowCells[i] = cells[row * 9 + i];
        }
        return rowCells;
    }

    private Cell[] getCol(int col) {
        Cell[] colCells = new Cell[9];
        for (int i = 0; i < 9; i++) {
            colCells[i] = cells[i * 9 + col];
        }
        return colCells;
    }

    private Cell[] getSquare(int square) {
        Cell[] squareCells = new Cell[9];
        int row = square / 3;
        int col = square % 3;
        for (int i = 0; i < 9; i++) {
            squareCells[i] = cells[(row * 3 + i / 3) * 9 + col * 3 + i % 3];
        }
        return squareCells;
    }
}
