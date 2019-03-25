package start.module.myvim.state;

import javax.swing.text.JTextComponent;
import org.openide.util.NbBundle;
import start.module.myvim.exception.EVIException;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.state.CommandModeOperator;
import start.module.myvim.state.Mode;
import static start.module.myvim.state.Mode.*;
import static start.module.myvim.utilities.ComponentUtils.*;
import start.module.myvim.utilities.VIEXBundle;

/**
 *
 * @author jker
 */
public class CommandModeState {
    
    private static VIEXRepository repository = VIEXRepository.getRespository();
    
    public static boolean isCommandMode(JTextComponent component) {
        if (isNullMode(component))
            throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
        return (repository.getEditorState(component).getCommandMode().currentMode() == COMMAND_MODE);
    }
    
    public static boolean isInsertMode(JTextComponent component) {
        if (isNullMode(component))
            throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
        return (repository.getEditorState(component).getCommandMode().currentMode() == INSERT_MODE);
    }
    
    public static boolean isVisualMode(JTextComponent component) {
        if (isNullMode(component))
            throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
        return (repository.getEditorState(component).getCommandMode().currentMode() == VISUAL_MODE);
    }

    public static boolean isVisualBlockMode(JTextComponent component) {
        if (isNullMode(component))
            throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
        return (repository.getEditorState(component).getCommandMode().currentMode() == VISUAL_BLOCK_MODE);
    }

    public static boolean isMiscMode(JTextComponent component) {
        if (isNullMode(component))
            throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
        return (repository.getEditorState(component).getCommandMode().currentMode() == MISC_MODE);
    }
    
    public static void switchMode(Mode mode, JTextComponent component) {
        if (isNullMode(component))
            throw new EVIException(VIEXBundle.getMessage("MODE_ERROR"));
        repository.getEditorState(component).getCommandMode().switchMode(mode, component);
    }
}
