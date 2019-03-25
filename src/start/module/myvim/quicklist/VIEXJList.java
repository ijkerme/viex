package start.module.myvim.quicklist;

import java.awt.Component;
import java.awt.Graphics;
import java.util.List;
import javax.swing.AbstractListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

/**
 *
 * @author jker
 */
public class VIEXJList extends JList{

    private static final int QUICK_LIST_ITEM_HEIGHT = 16;
    private static final int QUICK_LIST_ITEM_WIDTH = 300;
    public static final int DARKER_COLOR_COMPONENT = 10;
    
    private List<VIEXQuickListItem> items;

    private int maxVisiableRows;
    
    public VIEXJList(int maxVisiableRows) {
        this.maxVisiableRows = maxVisiableRows;
        setFocusable(false);
        setLayoutOrientation(JList.VERTICAL);
        setFixedCellHeight(Math.max(QUICK_LIST_ITEM_HEIGHT, getFontMetrics(getFont()).getHeight()));
        setCellRenderer(new VIEXListCellRenderer());
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public void setData(List<VIEXQuickListItem> items, int selectedIndex) {
        this.items = items;
        setModel(new VIEXListMode(items));
        setSelectedIndex(selectedIndex);
        setVisibleRowCount(Math.min(items.size(), maxVisiableRows));
        setFixedCellWidth(300);
    }
    
    private class VIEXListMode extends AbstractListModel {
        
        private List<VIEXQuickListItem> items;
        
        public VIEXListMode(List<VIEXQuickListItem> items) {
            this.items = items;
        }
        
        public int getSize() {
            return items == null ? 0 : items.size();
        }
        
        public Object getElementAt(int index) {
            return items == null ? null : items.get(index);
        }
        
    }
    
    private class VIEXListCellRenderer implements ListCellRenderer {
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = new JLabel();
            label.setText(value.toString());
            label.setOpaque(true);
            label.setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            VIEXQuickListItem item = (VIEXQuickListItem)value;
            return item.getListCellComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}