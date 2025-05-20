package org.anne.sudoku.solver.techniques;

import org.anne.sudoku.Grade;
import org.anne.sudoku.model.Cell;
import org.anne.sudoku.model.Grid;
import org.anne.sudoku.model.Predicates;

import java.util.*;
import java.util.function.Predicate;

/* Renamed from AlignedPairExclusion to SubsetExclusion
 * because the "base" cells are not necessarily aligned:
 *
 * Any two cells CANNOT contain
 * a pair of numbers that will break an Almost Locked Set they both entirely see
 */
public class SubsetExclusion extends SolvingTechnique {
    public SubsetExclusion() {
        super("Aligned Pair Exclusion", Grade.VERY_HARD);
    }

    private Grid grid;

    @Override
    public List<Cell> apply(Grid grid) {
        this.grid = grid;
        List<Cell> changed = new ArrayList<>();

        for (Cell cell1 : grid.getCells(Predicates.unsolvedCells)) {
            for (Cell cell2 : grid.getCells(Predicates.unsolvedCells)) {
                if (cell1 == cell2) continue;
                StringBuilder sb = new StringBuilder();
                var allowedCombinations = getAllAllowedCombinations(cell1, cell2, sb);
                // For all potentials of both base cells, test if the value is possible in at least one allowed combination
                for (int i = 0; i < 2; i++) {
                    Cell cell = (i == 0) ? cell1 : cell2;
                    int index = (i == 0) ? 0 : 1;
                    for (int digit : cell.getCandidates()) {
                        if (allowedCombinations.stream().noneMatch(combination -> combination[index] == digit)) {
                            cell.removeCandidate(digit);
                            log("Aligned Pair Exclusion found in %s%n%s- Removed {%d} from %s%n",
                                    List.of(cell1, cell2), sb.toString(), digit, cell);
                            changed.add(cell);
                            incrementCounter();
                            return changed;
                        }
                    }
                }
            }
        }
        return changed;
    }

    private List<int[]> getAllAllowedCombinations(Cell cell1, Cell cell2, StringBuilder sb) {
        List<int[]> allowedCombinations = new ArrayList<>();
        var amostLockedSets = getAlmostLockedSets((Predicates.isPeerOf(cell1).and(Predicates.isPeerOf(cell2))));
        for (int digit1 : cell1.getCandidates()) {
            for (int digit2 : cell2.getCandidates()) {
                if (digit1 == digit2) {
                    if (!cell1.isPeer(cell2)) {
                        // If the two cells are not isPeerOf, they can contain the same digit
                        allowedCombinations.add(new int[]{digit1, digit2});
                    }
                    continue;
                }
                boolean isAllowed = true;
                // Check if it exists any combination of isPeerOf of both cells that would be an almost locked set with these digits
                for (var als : amostLockedSets.entrySet()) {
                    if (als.getKey().get(digit1) && als.getKey().get(digit2)) {
                        isAllowed = false; // This combination is not allowed
                        sb.append(String.format("{%d,%d} found in ALS %s%n", digit1, digit2, als.getValue()));
                        break;
                    }
                }
                if (isAllowed) {
                    allowedCombinations.add(new int[]{digit1, digit2});
                }
            }
        }
        return allowedCombinations;
    }

    public HashMap<BitSet, List<Cell>> getAlmostLockedSets(Predicate<Cell> predicate) {
        HashMap<BitSet, List<Cell>> als = new HashMap<>();
        Cell[] cells = grid.getCells(Predicates.unsolvedCells.and(predicate));
        List<Cell[]> combinations = new ArrayList<>();

        // Generate all combinations of cells
        for (int size = 1; size <= cells.length; size++) {
            generateCombinations(cells, size, 0, new Cell[size], 0, combinations);
        }
        for (Cell[] combination : combinations) {
            if (isValidCombination(combination)) {
                BitSet combinedCandidates = new BitSet(9);
                for (Cell cell : combination) {
                    combinedCandidates.or(cell.candidates());
                }
                als.put(combinedCandidates, Arrays.asList(combination));
            }
        }
        return als;
    }

    private boolean isValidCombination(Cell[] combination) {
        for (Cell cell : combination) {
            if (Arrays.stream(combination).anyMatch(c -> c != cell && !c.isPeer(cell))) {
                return false;
            }
        }
        BitSet combinedCandidates = new BitSet(9);
        for (Cell cell : combination) {
            combinedCandidates.or(cell.candidates());
        }

        return combinedCandidates.cardinality() == combination.length + 1;
    }

    private void generateCombinations(Cell[] cells, int size, int start, Cell[] current, int depth, List<Cell[]> combinations) {
        if (depth == size) {
            combinations.add(current.clone());
            return;
        }

        for (int i = start; i <= cells.length - (size - depth); i++) {
            current[depth] = cells[i];
            generateCombinations(cells, size, i + 1, current, depth + 1, combinations);
        }
    }
}
