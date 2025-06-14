package org.anne.sudoku.solver;

import org.anne.sudoku.model.Grid;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BruteForceParameterizedTest {

    /**
     * Test the BruteForce solver with multiple Sudoku puzzles.
     * Each test case consists of a Sudoku puzzle string and its expected solution.
     * <p>
     * input            The Sudoku puzzle string.
     * expectedSolution The expected solution string.
     */

    BruteForce solver = new BruteForce();

    @DisplayName("Multiple Sudoku puzzles")
    @ParameterizedTest(name = "[{index}] {0} -> {1}")
    @CsvSource({
            "85...24..72......9..4.........1.7..23.5...9...4...........8..7..17..........36.4., 859612437723854169164379528986147352375268914241593786432981675617425893598736241",
            "..53.....8......2..7..1.5..4....53...1..7...6..32...8..6.5....9..4....3......97.., 145327698839654127672918543496185372218473956753296481367542819984761235521839764",
            "..7........1....3..5..4.6...4....7...6...3........1..2.....7.......6..8...2.....1, 697538124421796835358142679143829756265473918879651342586217493914365287732984561",
            "8.346712..798..3.44.1..2..792.63.715..51.46..716.29.436..2..4.15.2..693..389452.6, 853467129279851364461392587924638715385174692716529843697283451542716938138945276",
            ".97.6183226.9.751.43.258...7.4...18381.374.95923...7.6...196.58.895.2.7114578.62., 597461832268937514431258967754629183816374295923815746372196458689542371145783629",
            ".2764..83.81.23469..485971234.9..2.61.24.83.57.9..2.482951876..87653.92.41..9685., 927641583581723469634859712348915276162478395759362148295187634876534921413296857",
            "6..59.2.89478..1565..4..37.4.975.6...6.1.9.3...5.248.7.96..5..2351..89642.4.16..3, 613597248947832156528461379489753621762189435135624897896345712351278964274916583",
            "5.368.19.7.6.91..38195..4.6.5..6.8.43687.29514.7.5..3.1.5..67486..83.2.9.82.173.5, 523684197746291583819573426251369874368742951497158632135926748674835219982417365",
            "327.9...496..87.518..24697.5964..81.47.862.39.38..1746.53619..864.73..951...2.367, 327195684964387251815246973596473812471862539238951746753619428642738195189524367",
            "97.84.25.5.27..436.1.526978.619.4.25.4736518.35.2.876.695137.4.134..25.7.28.59.13, 976843251582791436413526978861974325247365189359218764695137842134682597728459613",
    })
    void testParameterizedInput(String input, String expectedSolution) {
        Grid grid = new Grid(input);
        if (solver.solve(grid)) {
            assertEquals(expectedSolution, grid.getSolution());
        }
    }
}
