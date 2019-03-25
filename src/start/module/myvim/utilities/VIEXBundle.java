package start.module.myvim.utilities;

import java.util.MissingResourceException;
import org.openide.util.NbBundle;
import start.module.myvim.EVIEditor;

/**
 *
 * @author jker
 */
public class VIEXBundle {
    
    public static String getMessage(String mkey) {
        return NbBundle.getMessage(EVIEditor.class, mkey);
    }
    
    public static String getMessage(Class mclass, String mkey) {
        return NbBundle.getMessage(mclass, mkey);
    }
    
}
