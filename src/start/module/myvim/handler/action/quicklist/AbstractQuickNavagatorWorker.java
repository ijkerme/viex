package start.module.myvim.handler.action.quicklist;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JList;
import javax.swing.text.JTextComponent;
import start.module.myvim.exception.EVIException;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.quicklist.VIEXQuickListItem;

/**
 *
 * @author jker
 */
public abstract class AbstractQuickNavagatorWorker implements QuickNavagatorWorker {

    protected JTextComponent target;
    protected String content;
    
    protected VIEXPopup popup = VIEXPopup.getPopup();
    protected List<VIEXQuickListItem> results = new LinkedList();
    protected int start = 0;
    protected String key = "";
    private boolean running = false;
    
    protected AbstractQuickNavagatorWorker() {
    }
    
    public void up() {
        if (popup.isVisible())
            popup.getContentComponent().up();
    }
    
    public void down() {
        if (popup.isVisible())
            popup.getContentComponent().down();
    }
    
    public void first() {
    }
    
    public void last() {
    }
    
    public void pageDown() {
        if (popup.isVisible())
            popup.getContentComponent().pageDown();
    }
    
    public void pageUp() {
        if (popup.isVisible())
            popup.getContentComponent().pageUp();
    }

    public void run() {
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public synchronized boolean isRunning() {
        return running;
    }
    
    public synchronized void cancel() {
        running = false;
    }
    
    public void prefixMatch(String prefix) {
        if (null == prefix)
            throw new EVIException("prefix cann't be null");
        int i = 0;
        if (key.length() != 0) {
            if (prefix.startsWith(key)) {
                i = start;
            } else {
                start = 0;
                key = "";
            }
        }
        for (int counts = results.size(); i < counts; i++) {
            // compare, insensitive
            if ((results.get(i)).getItemValue().toLowerCase().startsWith(prefix.toLowerCase())) {
                //System.out.println(((FileObjectQuickListItem)results.get(i)).value().getNameExt());
                if (popup.isVisible()) {
                    JList view = popup.getContentComponent().getView();
                    view.setSelectedIndex(i);
                    view.ensureIndexIsVisible(i);
                    key = prefix;
                    start = i;
                    return ;
                }
            }
        }
    }
    
    public void reverseList() {
        if (popup.isVisible()) {
            Collections.reverse(results);
            popup.getContentComponent().getView().updateUI();
        }
    }
    
    public JTextComponent getComponent() {
        return this.target;
    }
    
    public void setComponent(JTextComponent component) {
        this.target = component;
    }
    
    protected void sortResults(List results) {
        Collections.sort(results, new Comparator() {
            public int compare(Object o1, Object o2) {
                VIEXQuickListItem item1 = (VIEXQuickListItem)o1;
                VIEXQuickListItem item2 = (VIEXQuickListItem)o2;
                return item1.getItemValue().compareToIgnoreCase(item2.getItemValue());
            }
            
            public boolean equals(Object obj) {
                boolean retValue;
                retValue = super.equals(obj);
                return retValue;
            }
            
        });
    }
}
