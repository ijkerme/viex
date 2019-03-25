package start.module.myvim;

import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.state.VIEXRepository;
import start.module.myvim.utilities.EVIConstant;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.state.CommandModeState.*;

/**
 *
 * @author jker
 */
public class EditorPropertyChangeListener implements PropertyChangeListener {
    
    private static final Logger logger = Logger.getLogger("EditorPropertyChangeListener");

    public void propertyChange(PropertyChangeEvent evt) {
        if (!VIEXInitialSetting.ISATTACHED)
            return ;
        JTextComponent component = getMostActiveComponent();
        if (component == null)
            return ;
        //CommandMode cm = CommandModeOperator.getOperator().get(component);
        CommandMode cm = VIEXRepository.getRespository().getEditorState(component).getCommandMode();
        if (cm == null) {
            addEVIKeymap(component);
            //eviStatusBarChange(component);
            KeyListener[] kls = component.getKeyListeners();
            for (int f = 0; f < kls.length; f++) {
                if (kls[f] instanceof EVIKeyListener) {
                    return ; //see whether if has EVIKeyListeners component that has registried EVIKeyListeners's.
                }
            }
            component.addKeyListener(VIEXInitialSetting.getKeyListener());
            //component.addPropertyChangeListener(VIEXInitialSetting.getPropertyChangeListener());
        } else {
            //logger.info(" " + component.getKeymap());
            /*System.out.println("----------------" + component.getKeymap());
            System.out.println("----------------" + evt);
            Keymap km = component.getKeymap(EVIConstant.EVI_KEYMAP);
            if (isInsertMode(component) && km != null) {
                removeKeymap(component);
            } else if (!isInsertMode(component) && km == null){
                addKeymap(component);
            } else {
                //
            }*/
            boolean ok = component.getKeymap().getName().equals(EVIConstant.EVI_KEYMAP);
            if (isInsertMode(component) && ok) {
                removeKeymap(component);
            } else if (!isInsertMode(component) && !ok) {
                addKeymap(component);
            } else {
                //
            }
        }
    }
    
}
