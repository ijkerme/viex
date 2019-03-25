package start.module.myvim.handler.cmdhandler;

import javax.swing.text.JTextComponent;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.state.VIEXRepository;

import static start.module.myvim.utilities.CommandUtils.*;
import static start.module.myvim.state.Mode.*;

/**
 *
 * @author jker
 */
public abstract class CommandHandlerFactory {
    
    private static final CommandHandler COLON_PARSER = new ColonCommandHandler();
    private static final CommandHandler SLASH_PARSER = new SlashCommandHandler();
    private static final CommandHandler VISUAL_PARSER = new VisualCommandHandler();
    private static final CommandHandler NO_TERMINAL_PARSER = new NoTerminateCommandHandler();
    private static final CommandHandler DIGITAL_PARSER = new DigitalCommandHandler();
    private static final CommandHandler VISUAL_BLOCK_PARSER = new VisualBlockHandler();
    private static final CommandHandler MISC_PARSER = new MiscCommandHandler();
    private static final CommandHandler MISC_DIGITAL_PARSER = new MiscDigitalCommandHandler();
    
    public static CommandHandler getCommandHandler(String cmdString, JTextComponent component) {
        //CommandMode mode = CommandModeOperator.getOperator().get(component);
        CommandMode mode = VIEXRepository.getRespository().getEditorState(component).getCommandMode();
        if (mode.currentMode() == VISUAL_BLOCK_MODE)
            return VISUAL_BLOCK_PARSER;

        else if (mode.currentMode() == VISUAL_MODE) 
            return VISUAL_PARSER;
            
        else if (mode.currentMode() == COMMAND_MODE) {
            if (isStartWithColon(cmdString)) {                
                return COLON_PARSER;
                
            } else if (isStartWithSlash(cmdString)) {                
                return SLASH_PARSER;
                
            } else if (isVFirstLetter(cmdString)) {                
                mode.switchMode(VISUAL_MODE, null);
                return VISUAL_PARSER;
                
            } else if (isDigitalFirstLetter(cmdString) && !cmdString.equals("0")) {                
                return DIGITAL_PARSER;
                
            } else {                
                return NO_TERMINAL_PARSER;
                
            }

        } else if (mode.currentMode() == MISC_MODE) {
            if (isDigitalFirstLetter(cmdString) && VIEXPopup.getPopup().isVisible() && !cmdString.equals(("0"))) {
                return MISC_DIGITAL_PARSER;
            } else if (isStartWithSlash(cmdString) && VIEXPopup.getPopup().isVisible()) { 
                return SLASH_PARSER;
            } else {
                return MISC_PARSER;
            }

        } else {
            return null;
        }
    }

}
