package org.anne.sudoku.pdf;

import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
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
import com.itextpdf.layout.Document;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.*;
import org.anne.sudoku.Sudoku;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PdfGenerator {
    private static final String DEST = "results/Sudoku.pdf";
    private static final int CELL_SIZE = 44;
    private static final Color lightBlueColor = new DeviceCmyk(0.14f, 0.11f, 0, 0);
    private static final Color blueColor = new DeviceRgb(0, 0, 255);
    private static final int NUMBER_OF_PUZZLES = 8;
    private static final ImageData LINK_TO_SOLUTION;
    public static final String SCRIPT = "src/main/resources/Kalam-Regular.ttf";

    static {
        try {
            LINK_TO_SOLUTION = ImageDataFactory.create("src/main/resources/Solutions.png");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        PdfFont textFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        File file = new File(DEST);
        if (file.getParentFile().mkdirs()) {
            System.out.println("Directory created");
        }
        PdfDocument pdf = new PdfDocument(new PdfWriter(DEST));
        PageSize ps = PageSize.A4;
        Document document = new Document(pdf, ps);

        addCover(document);

        document.setTopMargin(50f);

        List<Sudoku> sudokuList = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PUZZLES; i++) {
            Sudoku sudoku = new Sudoku();
            sudokuList.add(sudoku);
        }
        sudokuList.sort(Comparator.comparing(Sudoku::getGrade));

        List<Table> solutions = new ArrayList<>();
        for (Sudoku sudoku : sudokuList) {
            int index = sudokuList.indexOf(sudoku);
            document.add(new AreaBreak());
            // Add image with link to solution
            Image linkImage = new Image(LINK_TO_SOLUTION);
            linkImage.scaleAbsolute(40, 40)
                    .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                    .setMarginRight(40);
            // linkImage.setFixedPosition(150, 250);
            linkImage.setAction(PdfAction.createGoTo("solution" + (index + 1)));
            document.add(linkImage);

            Paragraph title = new Paragraph("Sudoku #" + (index + 1))
                    .setFont(textFont)
                    .setFontSize(36)
                    .setTextAlignment(TextAlignment.CENTER);

            title.setProperty(Property.DESTINATION, "grid" + (index + 1));

            document.add(title);

            Paragraph gridDescription = new Paragraph(sudoku.getGrade() + " Puzzle")
                    .setFont(textFont)
                    .setFontSize(24)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(50);

            document.add(gridDescription);
            Table grid = gridTable(sudoku, false);
            document.add(grid);

            solutions.add(gridTable(sudoku, true));
        }

        for (int i = 0; i < sudokuList.size(); i += 4) {
            List<Table> solutionTables = new ArrayList<>();
            solutionTables.add(solutions.get(i));
            if (i + 1 < sudokuList.size()) solutionTables.add(solutions.get(i + 1));
            if (i + 2 < sudokuList.size()) solutionTables.add(solutions.get(i + 2));
            if (i + 3 < sudokuList.size()) solutionTables.add(solutions.get(i + 3));

            document.add(new AreaBreak());

            Table table = new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                    .setHorizontalAlignment(HorizontalAlignment.CENTER)
                    .setMarginTop(50);

            for (Table solution : solutionTables) {
                int index = solutionTables.indexOf(solution);
                Paragraph title = new Paragraph(new Link("Solution to puzzle #" + (i + index + 1), PdfAction.createGoTo("grid" + (i + index + 1))))
                        .setFont(textFont)
                        .setFontSize(16)
                        .setMarginBottom(50)
                        .setTextAlignment(TextAlignment.CENTER);
                title.setProperty(Property.DESTINATION, "solution" + (i + index + 1));
                Cell cell = new Cell()
                        .setBorder(Border.NO_BORDER)
                        .setFont(textFont)
                        .setFontSize(16)
                        .setPaddingBottom(index < 2 ? 50 : 0)
                        .setPaddingRight(index % 2 == 0 ? 50 : 0)
                        .setPaddingTop(index > 1 ? 50 : 0)
                        .setTextAlignment(TextAlignment.CENTER);
                cell.add(title).add(solution);
                table.addCell(cell);
            }
            document.add(table);
        }

        document.close();
    }

    private static void addCover(Document document) {
        String imageFile = "src/main/resources/Cover.png";
        try {
            ImageData data = ImageDataFactory.create(imageFile);
            Image img = new Image(data);
            document.setMargins(0,0,0,0);
            document.add(img);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Table gridTable(Sudoku sudoku, boolean solution) throws IOException {
        float multiplier = solution ? 0.4f : 1;
        float cellSize = CELL_SIZE * multiplier;
        float borderSize = 0.8f * multiplier;
        PdfFont numberFont = PdfFontFactory.createFont(StandardFonts.COURIER);
        FontProgram fontProgram = FontProgramFactory.createFont(SCRIPT);
        PdfFont scriptFont = PdfFontFactory.createFont(fontProgram, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

        Style editableStyle = new Style()
                .setFont(scriptFont)
                .setFontSize(36 * multiplier)
                .setFontColor(ColorConstants.BLACK, 0.7f)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Style nonEditableStyle = new Style()
                .setFont(numberFont)
                .setFontSize(36 * multiplier)
                .setBackgroundColor(lightBlueColor)
                .setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Table grid = new Table(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setHorizontalAlignment(HorizontalAlignment.CENTER);

        String puzzle = solution ? sudoku.grid : sudoku.getPuzzle();

        for (int i = 0; i < puzzle.length(); i++) {
            char c = puzzle.charAt(i);
            boolean isEmpty = c == '.';

            Paragraph paragraph = new Paragraph(isEmpty ? "" : String.valueOf(c)).setWidth(cellSize).setHeight(cellSize);

            Cell cell = new Cell()
                    .setWidth(cellSize)
                    .setHeight(cellSize)
                    .setBorder(new SolidBorder(blueColor, borderSize))
                    .setMarginTop(0f)
                    .setMarginBottom(0f);
            if (!isEmpty) {
                cell.addStyle(sudoku.isEditable(i) ? editableStyle : nonEditableStyle);
            }
            cell.add(paragraph);
            grid.addCell(cell);
            paragraph.setProperty(Property.OVERFLOW_Y, OverflowPropertyValue.VISIBLE);
            if (solution) paragraph.setFixedLeading(18f);
            cell.setBorderBottom(getBorder(cell.getRow(), borderSize * 4));
            cell.setBorderRight(getBorder(cell.getCol(), borderSize * 4));
        }
        grid.setNextRenderer(new RoundedBorderTableRenderer(grid, borderSize * 4, borderSize * 2, blueColor));
        return grid;
    }

    private static Border getBorder(int position, float size) {
        if (position == 2 || position == 5) {
            return new SolidBorder(blueColor, size);
        }
        return Border.NO_BORDER;
    }
}
