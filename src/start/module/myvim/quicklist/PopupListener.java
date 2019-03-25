package start.module.myvim.quicklist;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import start.module.myvim.handler.action.quicklist.QuickNavagatorWorker;
import start.module.myvim.handler.action.quicklist.QuickNavagatorFactory;

/**
 *
 * @author jker
 */
public class PopupListener implements FocusListener {
    
    public void focusGained(FocusEvent e) {
    }
    
    public void focusLost(FocusEvent e) {
        QuickNavagatorWorker work = QuickNavagatorFactory.getNavagatorFactory().getNavagatorWorker();
        if (work != null) {
            if (work.isRunning())
                work.cancel();
            else {
                VIEXPopup.getPopup().hide();
            }
        }
    }
    
    
}
