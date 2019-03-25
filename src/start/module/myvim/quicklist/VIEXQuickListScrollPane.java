package start.module.myvim.quicklist;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author jker
 */
public class VIEXQuickListScrollPane extends JScrollPane {
    
    private VIEXJList view;
    
    private List<VIEXQuickListItem> listData;
    
    private JLabel banner;
    
    public VIEXQuickListScrollPane() {
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        view = new VIEXJList(25);
        setViewportView(view);
        view.addListSelectionListener(new VIEXListSelectionListener());
    }

    public void emptyListData() {
        listData = null;
    }
    
    public JList getView() {
        return this.view;
    }

    public void setData(List<VIEXQuickListItem> data, String title, int selectedIndex) {
        listData = data;
        setTitle(title);
        view.setData(data, selectedIndex);
        Point p = view.indexToLocation(selectedIndex);
        if (p != null)
            view.scrollRectToVisible(new Rectangle(p));
        setViewportView(getViewport().getView());
    }

    public void up() {
        int size = getModel().getSize();
        if (size > 0) {
            int idx = (view.getSelectedIndex() - 1 + size) % size;
            while(idx > 0 && getModel().getElementAt(idx) == null)
                idx--;
            view.setSelectedIndex(idx);
            view.ensureIndexIsVisible(idx);
        }
    }

    public void down() {
        int size = getModel().getSize();
        if (size > 0) {
            int idx = (view.getSelectedIndex() + 1) % size;
            while(idx < size && getModel().getElementAt(idx) == null)
                idx++;
            if (idx == size)
                idx = 0;
            view.setSelectedIndex(idx);
            view.ensureIndexIsVisible(idx);
        }
    }

    public void pageUp() {
        if (getModel().getSize() > 0) {
            int pageSize = Math.max(view.getLastVisibleIndex() - view.getFirstVisibleIndex(), 0);
            int idx = Math.max(view.getSelectedIndex() - pageSize, 0);
            while(idx > 0 && getModel().getElementAt(idx) == null)
                idx--;
            view.setSelectedIndex(idx);
            view.ensureIndexIsVisible(idx);
        }
    }

    public void pageDown() {
        int size = getModel().getSize();
        if (size > 0) {
            int pageSize = Math.max(view.getLastVisibleIndex() - view.getFirstVisibleIndex(), 0);
            int idx = Math.min(view.getSelectedIndex() + pageSize, size - 1);
            while(idx < size && getModel().getElementAt(idx) == null)
                idx++;
            if (idx == size) {
                idx = Math.min(view.getSelectedIndex() + pageSize, size - 1);
                while(idx > 0 && getModel().getElementAt(idx) == null)
                    idx--;
            }
            view.setSelectedIndex(idx);
            view.ensureIndexIsVisible(idx);
        }
    }

    public void begin() {
        if (getModel().getSize() > 0) {
            view.setSelectedIndex(0);
            view.ensureIndexIsVisible(0);
        }
    }

    public void end() {
        int size = getModel().getSize();
        if (size > 0) {
            int idx = size - 1;
            while(idx > 0 && getModel().getElementAt(idx) == null)
                idx--;
            view.setSelectedIndex(idx);
            view.ensureIndexIsVisible(idx);
        }
    }

    public ListModel getModel() {
        return view.getModel();
    }
    
    public void setTitle(String title) {
        if (title == null) {
            if (banner != null) {
                setColumnHeader(null);
                banner = null;
            }
        } else {
            if (banner != null) {
                banner.setText(title);
            } else {
                banner = new JLabel(title);
                banner.setForeground(Color.blue);
                banner.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
                setColumnHeaderView(banner);
            }
        }
    }
    
    private static class VIEXListSelectionListener implements ListSelectionListener {
    
        public void valueChanged(ListSelectionEvent e) {
            if (e.getSource() instanceof JList) {
                JList list = (JList)e.getSource();
                if (list.getSelectedValue() instanceof VIEXQuickListItem) {
                    VIEXQuickListItem item = (VIEXQuickListItem)list.getSelectedValue();
                    item.showDescription();
                }
            }
        }
        
    }

}
