package org.anne.sudoku.pdf;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.action.PdfAction;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.event.PdfDocumentEvent;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.anne.sudoku.solver.Generator;
import org.anne.sudoku.model.Grid;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class PdfGenerator {
    private static final String DEST = "results/Sudoku.pdf";
    private static final int CELL_SIZE = 44;
    private static final Color LIGHT_BLUE = new DeviceCmyk(0.14f, 0.11f, 0, 0);
    private static final Color BLUE = new DeviceRgb(0, 0, 255);
    private static final int NUMBER_OF_PUZZLES = 20;
    private static final ImageData LINK_TO_SOLUTION;
    public static final String SCRIPT = "src/main/resources/Kalam-Regular.ttf";

    static {
        try {
            LINK_TO_SOLUTION = ImageDataFactory.create("src/main/resources/Solutions.png");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        PdfFont textFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        File file = new File(DEST);
        if (file.getParentFile().mkdirs()) {
            System.out.println("Directory created");
        }
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        Document document = new Document(pdf, PageSize.A4);

        addCover(document);

        PdfCanvas canvas = new PdfCanvas(pdf.getFirstPage());

        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 6)
                .showText(String.format("Created on %s", java.time.LocalDate.now()))
                .endText()
                .stroke();

        document.setTopMargin(50f);

        List<Grid> sudokuList = generateSudokuList();
        List<Table> solutions = new ArrayList<>();

        for (Grid sudoku : sudokuList) {
            int index = sudokuList.indexOf(sudoku);
            document.add(new AreaBreak());
            addLinkImage(document, index);
            addTitle(document, textFont, index);
            addGridDescription(document, textFont, sudoku);
            document.add(gridTable(sudoku, false));
            solutions.add(gridTable(sudoku, true));
        }

        addSolutions(document, textFont, sudokuList, solutions);
        document.close();
    }

    private static void addCover(Document document) {
        try {
            Image img = new Image(ImageDataFactory.create("src/main/resources/Cover.png"));
            document.setMargins(0, 0, 0, 0);
            Paragraph paragraph = new Paragraph();
            paragraph.setFixedPosition(0, 0, PageSize.A4.getWidth())
                    .setHeight(PageSize.A4.getHeight())
                    .setWidth(PageSize.A4.getWidth())
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            document.add(img);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Grid> generateSudokuList() {
        Generator generator = new Generator();
        return IntStream.range(1, NUMBER_OF_PUZZLES + 1)
                .mapToObj(_ -> generator.generate())
                // .sorted(Comparator.comparingInt(grid -> grid.getGrade()))
                .toList();
    }

    private static void addLinkImage(Document document, int index) {
        Paragraph paragraph = new Paragraph();
        Image linkImage = new Image(LINK_TO_SOLUTION);
        linkImage.scaleAbsolute(40, 40);
        paragraph.setFixedPosition(450, 720, 40);
        linkImage.setAction(PdfAction.createGoTo("solution" + (index + 1)));
        paragraph.add(linkImage);
        document.add(paragraph);
    }

    private static void addTitle(Document document, PdfFont textFont, int index) {
        Paragraph title = new Paragraph("Sudoku #" + (index + 1))
                .setFont(textFont)
                .setFontSize(36)
                .setTextAlignment(TextAlignment.CENTER);
        title.setProperty(Property.DESTINATION, "grid" + (index + 1));
        document.add(title);
    }

    private static void addGridDescription(Document document, PdfFont textFont, Grid sudoku) {
        Paragraph gridDescription = new Paragraph(sudoku.getGrade() + " Puzzle")
                .setFont(textFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(50);
        document.add(gridDescription);
    }

    private static void addSolutions(Document document, PdfFont textFont, List<Grid> sudokuList, List<Table> solutions) {
        for (int i = 0; i < sudokuList.size(); i += 4) {
            List<Table> solutionTables = new ArrayList<>();
            for (int j = 0; j < 4 && i + j < sudokuList.size(); j++) {
                solutionTables.add(solutions.get(i + j));
            }

            document.add(new AreaBreak());
            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginTop(50);

            for (int j = 0; j < solutionTables.size(); j++) {
                Paragraph title = new Paragraph(new Link("Solution to puzzle #" + (i + j + 1), PdfAction.createGoTo("grid" + (i + j + 1))))
                        .setFont(textFont)
                        .setFontSize(16)
                        .setMarginBottom(50)
                        .setTextAlignment(TextAlignment.CENTER);
                title.setProperty(Property.DESTINATION, "solution" + (i + j + 1));
                Cell cell = new Cell()
                        .setBorder(Border.NO_BORDER)
                        .setFont(textFont)
                        .setFontSize(16)
                        .setPaddingBottom(j < 2 ? 50 : 0)
                        .setPaddingRight(j % 2 == 0 ? 50 : 0)
                        .setPaddingTop(j > 1 ? 50 : 0)
                        .setTextAlignment(TextAlignment.CENTER);
                cell.add(title).add(solutionTables.get(j));
                table.addCell(cell);
            }
            document.add(table);
        }
    }

    private static Table gridTable(Grid sudoku, boolean solution) throws IOException {
        float multiplier = solution ? 0.4f : 1;
        float cellSize = CELL_SIZE * multiplier;
        float borderSize = 0.8f * multiplier;
        PdfFont numberFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        PdfFont scriptFont = PdfFontFactory.createFont(SCRIPT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        Style editableStyle = new Style()
                .setFont(scriptFont)
                .setFontSize(36 * multiplier)
                .setFontColor(ColorConstants.BLACK, 0.7f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Style nonEditableStyle = new Style()
                .setFont(numberFont)
                .setFontSize(36 * multiplier)
                .setBackgroundColor(LIGHT_BLUE)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Table grid = new Table(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        String puzzle = solution ? sudoku.getSolution() : sudoku.getPuzzle();

        for (int i = 0; i < puzzle.length(); i++) {
            char c = puzzle.charAt(i);
            boolean isEmpty = c == '.';

            Paragraph paragraph = new Paragraph(isEmpty ? "" : String.valueOf(c)).setWidth(cellSize).setHeight(cellSize);

            Cell cell = new Cell()
                    .setWidth(cellSize)
                    .setHeight(cellSize)
                    .setBorder(new SolidBorder(BLUE, borderSize))
                    .setMarginTop(0f)
                    .setMarginBottom(0f);
            if (!isEmpty) {
                cell.addStyle(sudoku.isClue(i) ? nonEditableStyle : editableStyle);
            }
            cell.add(paragraph);
            grid.addCell(cell);
            paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            if (solution) paragraph.setFixedLeading(18f);
            cell.setBorderBottom(getBorder(cell.getRow(), borderSize * 4));
            cell.setBorderRight(getBorder(cell.getCol(), borderSize * 4));
        }
        grid.setNextRenderer(new RoundedBorderTableRenderer(grid, borderSize * 4, borderSize * 2, BLUE));
        return grid;
    }

    private static Border getBorder(int position, float size) {
        if (position == 2 || position == 5) {
            return new SolidBorder(BLUE, size);
        }
        return Border.NO_BORDER;
    }
}