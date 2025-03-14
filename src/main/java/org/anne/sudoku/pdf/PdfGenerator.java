package org.anne.sudoku.pdf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import org.anne.sudoku.generator.PuzzleGenerator;
import org.anne.sudoku.generator.SolutionGenerator;
import org.anne.sudoku.solver.Sudoku;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class PdfGenerator {
    public static final String DEST = "results/Sudoku.pdf";

    public static void main(String[] args) throws IOException, URISyntaxException {
        File file = new File(DEST);
        if (file.getParentFile().mkdirs()) {
            System.out.println("Directory created");
        }
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        PageSize ps = PageSize.A4;
        Document document = new Document(pdf, ps);
        document.setTopMargin(90f);
        for (int i = 0; i < 10; i++) {
            Sudoku sudoku = new Sudoku(new PuzzleGenerator(new SolutionGenerator().generate()).generate());
            new PageGenerator().createPdf(document, sudoku);
            document.add(new AreaBreak());
        }
        document.close();
    }
}
