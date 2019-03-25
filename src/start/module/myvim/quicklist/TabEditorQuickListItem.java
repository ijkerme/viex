package start.module.myvim.quicklist;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;

/**
 *
 * @author jker
 */
public class TabEditorQuickListItem implements VIEXQuickListItem <Object> {
    
    private static final Logger logger = Logger.getLogger("TabEditorQuickListItem");
    
    private Object tabData;
    private boolean immediate;
    
    public TabEditorQuickListItem(Object tabData) {
        this.tabData = tabData;
    }
    
    public Object value() {
        return this.tabData;
    }
    
    public String getItemValue() {
        //return ":" + tabData.getComponent().getName();
        return "";
    }
    
    public void defaultAction(JComponent component) {
        //logger.info("execute tab editor quick list item action...");
        /*if (tabData.getComponent() instanceof TopComponent) {
            VIEXPopup popup = VIEXPopup.getPopup();
            if (popup.isVisible())
                popup.hide();
            TopComponent top = (TopComponent) tabData.getComponent();
            top.requestActive();
        }*/
    }

    public void showDescription() {
        
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
