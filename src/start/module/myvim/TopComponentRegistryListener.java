package start.module.myvim;

import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import start.module.myvim.state.EditorState;
import start.module.myvim.state.VIEXRepository;
import start.module.myvim.utilities.ComponentUtils;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.state.CommandModeState.*;
import start.module.myvim.utilities.EVIConstant;

/**
 *
 * @author jker
 */
public class TopComponentRegistryListener implements PropertyChangeListener {
    
    private static final Logger logger = Logger.getLogger("TopComponentRegistryListener");
    
    /*
     * make sure viex's keymap is installed to component, perhaps has added to no right's
     * source code editor that viex keymap.
     *
     */
    public void propertyChange(PropertyChangeEvent evt) {
        //logger.info("occur property change event: " + evt.getPropertyName());
        if (!VIEXInitialSetting.ISATTACHED)
            return ;
        JTextComponent component = getMostActiveComponent();
        if (component == null)
            return ;
        //CommandMode cm = CommandModeOperator.getOperator().get(component);
        EditorState estate = VIEXRepository.getRespository().getEditorState(component);
        if (estate == null) {
            addEVIKeymap(component);
            //eviStatusBarChange(component);
            ComponentUtils.updateStatusBarInfo("");
            KeyListener[] kls = component.getKeyListeners();
            for (int f = 0; f < kls.length; f++) {
                if (kls[f] instanceof EVIKeyListener) {
                    return ; //see whether if has EVIKeyListeners component that has registried EVIKeyListeners's.
                }
            }
            component.addKeyListener(VIEXInitialSetting.getKeyListener());
            component.addFocusListener(VIEXInitialSetting.getPopupListener());
        } else {
            boolean ok = component.getKeymap().getName().equals(EVIConstant.EVI_KEYMAP);
            if (isInsertMode(component) && ok) {
                removeKeymap(component);
            } else if (!isInsertMode(component) && !ok) {
                addKeymap(component);
            } else {
                //
            }
            ComponentUtils.updateStatusBarInfo("");
        }
    }
    
}
