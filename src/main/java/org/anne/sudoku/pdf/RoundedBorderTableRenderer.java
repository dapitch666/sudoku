package org.anne.sudoku.pdf;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.renderer.DrawContext;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.layout.renderer.TableRenderer;

public class RoundedBorderTableRenderer extends TableRenderer {
    private final float lineWidth;
    private final float radius;
    private final Color color;

    public RoundedBorderTableRenderer(Table modelElement, float lineWidth, float radius, Color color) {
        super(modelElement);
        this.lineWidth = lineWidth;
        this.radius = radius;
        this.color = color;
    }

    @Override
    public IRenderer getNextRenderer() {
        return new RoundedBorderTableRenderer((Table)getModelElement(), lineWidth, radius, color);
    }

    @Override
    protected void drawBorders(DrawContext drawContext) {
        Rectangle rect = getOccupiedAreaBBox();
        PdfPage currentPage = drawContext
                .getDocument()
                .getPage(getOccupiedArea().getPageNumber());

        PdfCanvas aboveCanvas = new PdfCanvas(currentPage.newContentStreamAfter(), currentPage.getResources(), drawContext.getDocument());

        aboveCanvas
                .saveState()
                .setLineWidth(lineWidth)
                .setStrokeColor(color)
                .roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), radius)
                .stroke()
                .restoreState();

        super.drawBorders(drawContext);
    }

    @Override
    public void drawChildren(DrawContext drawContext) {
        Rectangle rect = getOccupiedAreaBBox();

        PdfCanvas canvas = drawContext
                .getCanvas()
                .saveState()
                .roundRectangle(rect.getLeft(), rect.getBottom(), rect.getWidth(), rect.getHeight(), radius)
                .clip()
                .endPath();

        super.drawChildren(drawContext);
        canvas.restoreState();
    }

}