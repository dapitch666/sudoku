package org.anne.sudoku.model;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.function.Predicate;

public class Predicates {
    public static Predicate<Cell> solvedCells = Cell::isSolved;
    public static Predicate<Cell> unsolvedCells = cell -> !cell.isSolved();
    public static Predicate<Cell> justSolvedCells = Cell::justSolved;
    public static Predicate<Cell> biValueCells = Cell::isBiValue;

    public static Predicate<Cell> cellsWithNCandidates(int min, int max) {
        return cell -> cell.getCandidateCount() >= min && cell.getCandidateCount() <= max;
    }

    public static Predicate<Cell> isPeerOf(Cell cell) {
        return c -> c.isPeer(cell);
    }

    public static Predicate<Cell> containsCandidate(int digit) {
        return cell -> cell.hasCandidate(digit);
    }

    public static Predicate<Cell> inUnit(UnitType unitType, int unitIndex) {
        return cell -> cell.getUnitIndex(unitType) == unitIndex;
    }

    public static Predicate<Cell> containsAllCandidates(List<Integer> candidates) {
        return cell -> candidates.stream().allMatch(cell::hasCandidate);
    }

    public static Predicate<Cell> containsAllCandidates(BitSet candidates) {
        return cell -> candidates.stream().allMatch(cell::hasCandidate);
    }

    public static Predicate<Cell> intersectCandidates(BitSet candidates) {
        return cell -> cell.candidates().intersects(candidates);
    }

    public static Predicate<Cell> in(Cell[] cells) {
        return cell -> Arrays.asList(cells).contains(cell);
    }

    public static Predicate<Cell> inChute(UnitType unitType, Cell other) {
        if (unitType == UnitType.BOX) throw new IllegalArgumentException("UnitType must be ROW or COL");
        return cell -> unitType == UnitType.ROW ? cell.getHorizontalChute() == other.getHorizontalChute()
                : cell.getVerticalChute() == other.getVerticalChute();
    }

    public static Predicate<Cell> valueIs(int digit) {
        return cell -> cell.isSolved() && cell.getValue() == digit;
    }
}
