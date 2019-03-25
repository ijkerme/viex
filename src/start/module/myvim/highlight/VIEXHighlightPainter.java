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
public class VIEXHighlightPainter implements Highlighter.HighlightPainter {
    private Color color;
    
    public VIEXHighlightPainter(Color color) {
        this.color = color;
    }
    
    public void paint(Graphics g, int p0, int p1, Shape bounds, JTextComponent c) {
        try{
            Rectangle alloc = bounds.getBounds();
            TextUI textUI = c.getUI();
            Rectangle rect0 = textUI.modelToView(c, p0);
            Rectangle rect1 = textUI.modelToView(c, p1);
            
            if (rect0 == null || rect1 == null) {
                return;
            }
            
            g.setColor(color);
            // Single line highlight
            if (rect0.y==rect1.y){
                Rectangle r = rect0.union(rect1);
                g.fillRect(r.x, r.y, r.width, r.height);
            }  else{
                // Multi line highlight
                int p0ToMarginWidth = alloc.x+alloc.width-rect0.x;
                // first line
                g.fillRect(rect0.x, rect0.y, p0ToMarginWidth, rect0.height);
                if((rect0.y+rect0.height)!=rect1.y){
                    // Second to penultimate lines highlight - left to right edge
                    g.fillRect(alloc.x, rect0.y+rect0.height, alloc.width,
                            rect1.y-(rect0.y+rect0.height));
                }
                // Last line highlight
                g.fillRect(alloc.x, rect1.y, (rect1.x-alloc.x), rect1.height);
            }
        }  catch (BadLocationException e){
            // can't render
        }
    }
}