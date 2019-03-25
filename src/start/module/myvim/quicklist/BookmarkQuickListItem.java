package start.module.myvim.quicklist;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Utilities;
//import org.netbeans.lib.editor.bookmarks.api.Bookmark;
import start.module.myvim.utilities.ComponentUtils;

/**
 *
 * @author jker
 */
public class BookmarkQuickListItem implements VIEXQuickListItem<Object>{
    
    private static final Logger logger = Logger.getLogger("BookmarkQuickListItem");
    
    //private Bookmark bookmark;
    private boolean immediate;
    
    public BookmarkQuickListItem(Object bookmark) {
        // this.bookmark = bookmark;
    }
    
    public Object value() {
        //return this.bookmark;
        return null;
    }
    
    public void defaultAction(JComponent component) {
        //logger.info("execute bookmark quick list item action...");
        if (component instanceof JTextComponent) {
            JTextComponent target = (JTextComponent)component;
            //target.getCaret().setDot(bookmark.getOffset());
        }
    }

    public void showDescription() {
        
    }
   
    public String getItemValue() {
       /* if (null == bookmark)
            throw new NullPointerException("Bookmark can't be null");
        int i = bookmark.getOffset();
        JTextComponent target = ComponentUtils.getMostActiveComponent();
        if (target != null) {
            try {
                int start = Utilities.getRowStart(target, i);
                int end = Utilities.getRowEnd(target, i);
                return ":" + target.getDocument().getText(start, end - start);
            } catch (BadLocationException ex) {
            }
        }*/
        return "";
    }

    public boolean isImmediate() {
        return this.immediate;
    }
    
    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }
    
    public Component getListCellComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
        return new JPanel() {
            protected void paintComponent(Graphics g) {
                Font font = new Font("Serif", Font.PLAIN, 10);
                String text = font.getFamily();
                FontMetrics fm = g.getFontMetrics(font);
                g.setColor(isSelected ? list.getSelectionBackground() : list.getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(isSelected ? list.getSelectionForeground() : list.getForeground());
                //g.setFont(font);
                g.drawString(index + getItemValue(), 0, fm.getAscent());
            }
        };
    }

}
