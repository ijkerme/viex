package start.module.myvim.utilities;

import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.plaf.TextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;

public class OverwriteCaret extends DefaultCaret {
    
    public OverwriteCaret() {
        //setBlinkRate(10);
    }
    
    protected synchronized void damage(Rectangle r) {
        if (r != null) {
            try {
                JTextComponent comp = getComponent();
                TextUI mapper = comp .getUI() ;
                Rectangle r2 = mapper.modelToView(comp, getDot() + 1);
                if (r2 != null) {
                    int width = r2.x - r.x;
                }
                //if ((width == 0) || (width == 1)) {
                if (width == 0) {
                    width = MIN_WIDTH;
                }
                comp.repaint(r.x , r.y, width, r.height);
                // Swing 1.1 beta 2 compat
                this.x = r.x;
                this.y = r.y;
                this.width = width;
                this.height = r.height;
            } catch (BadLocationException e) {
            }
        }
    }
    
    public void paint(Graphics g) {
        if (isVisible( ) ) {
            try {
                JTextComponent comp = getComponent();
                TextUI mapper = comp.getUI();
                Rectangle r1 = mapper.modelToView(comp, getDot());
                Rectangle r2 = mapper.modelToView(comp, getDot() + 1);
                g = g.create();
                g.setColor(comp.getForeground());
                g.setXORMode(comp.getBackground());
                if (r2 != null) {
                    int width = r2.x - r1.x;
                }
                //if ((width == 0) || (width == 1)) {
                if (width ==0) {
                    width = MIN_WIDTH;
                }
                g.fillRect(r1.x, r1.y, width, r1.height);
                g.dispose();
            } catch (BadLocationException e) {
            }
        }
    }
    
    protected static final int MIN_WIDTH = 8;
}
