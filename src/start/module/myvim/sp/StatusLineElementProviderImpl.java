package start.module.myvim.sp;

import java.awt.Component;
import javax.swing.JLabel;
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

}
