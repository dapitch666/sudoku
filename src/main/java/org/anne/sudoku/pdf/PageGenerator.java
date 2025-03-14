package org.anne.sudoku.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import org.anne.sudoku.solver.Sudoku;

import java.io.IOException;
import java.net.URISyntaxException;

public class PageGenerator {

    public static final String DEST = "results/Sudoku.pdf";
    public static final Sudoku DEFAULT_PUZZLE = new Sudoku("5.4.6.......2...8...81....9.395...67....7.91.47...1.....13....5.....92.3.8.7.....");
    private static final int CELL_SIZE = 45;
    Color lightBlueColor = new DeviceCmyk(0.14f, 0.11f, 0, 0);
    Color blueColor = new DeviceRgb(0, 0, 255);

    public static void main(String[] args) throws IOException, URISyntaxException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        PageSize ps = PageSize.A4;
        Document document = new Document(pdf, ps);
        document.setTopMargin(90f);
        new PageGenerator().createPdf(document, DEFAULT_PUZZLE);
        document.close();
    }

    void createPdf(Document document, Sudoku sudoku) throws IOException {
        PdfFont textFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont numberFont = PdfFontFactory.createFont(StandardFonts.COURIER);

        Paragraph title = new Paragraph("Sudoku")
                .setFont(textFont)
                .setFontSize(36)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(title);

        Paragraph gridDescription = new Paragraph("Difficult Puzzle")
                .setFont(textFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(50);

        document.add(gridDescription);

        Table grid = new Table(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        for (int i = 0; i < sudoku.puzzle.length(); i++) {
            char c = sudoku.puzzle.charAt(i);
            boolean isEmpty = c == '0';
            Cell cell = new Cell().add(new Paragraph(isEmpty ? "" : String.valueOf(c)))
                    .setWidth(CELL_SIZE)
                    .setHeight(CELL_SIZE)
                    .setBorder(new SolidBorder(blueColor, 0.75f));
            if (!isEmpty) {
                cell.setBackgroundColor(lightBlueColor)
                        .setFont(numberFont)
                        .setFontSize(36)
                        .setTextAlignment(TextAlignment.CENTER)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE);
            }
            grid.addCell(cell);
            cell.setBorderBottom(getBorderBottom(cell.getRow()));
            cell.setBorderRight(getBorderBottom(cell.getCol()));

        }

        grid.setNextRenderer(new RoundedBorderTableRenderer(grid, 3f, 2f, blueColor));
        document.add(grid);
    }

    private Border getBorderBottom(int row) {
        if (row == 2 || row == 5) {
            return new SolidBorder(blueColor, 1.5f);
        }
        return Border.NO_BORDER;
    }
}
