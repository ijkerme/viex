package start.module.myvim.quicklist;

import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 *
 * @author jker
 */
public interface VIEXQuickListItem<T> {
    
    public T value();

    public String getItemValue();
    
    public void defaultAction(JComponent component);

    public void showDescription();
    
    public boolean isImmediate();

    Component getListCellComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus);
    
}
