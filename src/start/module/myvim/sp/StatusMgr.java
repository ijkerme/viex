package start.module.myvim.sp;

import java.util.Collection;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.Lookup;

/**
 *
 * @author xiaomj
 */
public class StatusMgr {
    
    public void updateCMD(final JLabel label, final String cmd) {
        if (SwingUtilities.isEventDispatchThread()) {
            label.setText(cmd);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    label.setText(cmd);
                }
            });
        }
    }
    
    public static StatusLineElementProviderImpl getStatusLine() {
        Collection<? extends StatusLineElementProvider> all = Lookup.getDefault().lookupAll(StatusLineElementProvider.class);
        for (StatusLineElementProvider a : all) {
            if (a instanceof StatusLineElementProviderImpl) {
                //JLabel ele = (JLabel) ((VIEXStatuLine) a).getStatusLineElement();
                //ele.doUpdate(); // or whatever method you need to call
                return (StatusLineElementProviderImpl) a;
            }
        }        
        return null;
    }    
}
