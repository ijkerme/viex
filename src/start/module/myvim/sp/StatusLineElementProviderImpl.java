package start.module.myvim.sp;

import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import org.openide.awt.StatusLineElementProvider;

/**
 *
 * @author jker
 */
public class StatusLineElementProviderImpl implements StatusLineElementProvider {

    private static JLabel label;

    @Override
    public Component getStatusLineElement() {
        if (label == null) {
            label = new JLabel("-VIEX-");
            label.setVisible(false);
        }
        return label;
    }
    
    public void updateStatus(final String status) {
        if (SwingUtilities.isEventDispatchThread()) {
            label.setText(status);
        } else {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    label.setText(status);
                }
            });
        }
    }

}
