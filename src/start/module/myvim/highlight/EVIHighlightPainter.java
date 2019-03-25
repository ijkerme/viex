package start.module.myvim.highlight;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author jker
 */
public class EVIHighlightPainter implements Highlighter.HighlightPainter {
    
    private Color color;
    
    public EVIHighlightPainter(Color color) {
        this.color = color;
    }
    
    public void paint(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c) {
        Rectangle alloc = bounds.getBounds();
        try {
            // --- determine locations ---
            TextUI mapper = c.getUI();
            Rectangle p0 = mapper.modelToView(c, offs0);
            Rectangle p1 = mapper.modelToView(c, offs1);
            
            // --- render ---
            if (color == null) {
                g.setColor(c.getSelectionColor());
            } else {
                g.setColor(color);
            }
            if (p0.y == p1.y) {
                // same line, render a rectangle
                Rectangle r = p0.union(p1);
                g.fillRect(r.x, r.y, r.width, r.height);
            } else {
                // different lines
                int p0ToMarginWidth = alloc.x + alloc.width - p0.x;
                g.fillRect(p0.x, p0.y, p0ToMarginWidth, p0.height);
                if ((p0.y + p0.height) != p1.y) {
                    g.fillRect(alloc.x, p0.y + p0.height, alloc.width,
                            p1.y - (p0.y + p0.height));
                }
                g.fillRect(alloc.x, p1.y, (p1.x - alloc.x), p1.height);
            }
        } catch (BadLocationException e) {
            // can't render
        }
    }
}
