package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.handler.action.CommandAction;
import start.module.myvim.state.Mode;

import static start.module.myvim.utilities.CommandUtils.*;
import static start.module.myvim.state.ActionType.*;
import static start.module.myvim.state.CommandType.*;
import static start.module.myvim.state.VIEXRepository.*;
import start.module.myvim.state.EditorState;

/**
 *
 * @author jker
 */
public class SlashCommandHandler extends ContentCommandHandler {
    
    public SlashCommandHandler() {
        super("SlashCommandParser");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        // remove end char if Backspace key is pressed.
        if (isEndWithBackspace(cmd.getFullContent())) {
            cmd.removeTailChar();
            cmd.removeTailChar();
            updateStatusBarInfo(cmd.getFullContent());
            if (cmd.getFullContent().length() <= 0) //immediate return when content length <= 0, preserve extractContent throw NullPointerException
                return ;
        }
        String data = cmd.getFullContent();
        setBufferedData(cmd);
        
        if (isEndWithEnter(data)) {
            setCleanBufferedData(true);
        } else {
            setCleanBufferedData(false);
        }
        CommandAction slashAction = getSlashAction(e);
        if (slashAction != null) {
            setAction(slashAction);
        }
        setComplete(true);
        String content = extractContent(cmd.getFullContent());
        setContent(content);
    }
    
    private String extractContent(String cmdValue) {
        return cmdValue.substring(1);
    }
    
    private CommandAction getSlashAction(ActionEvent e) {
        ActionFactory af = ActionFactory.getInstance();
        JTextComponent component = null;
        if (e.getSource() instanceof JTextComponent)
            component = (JTextComponent)e.getSource();
        else
            return null;
        EditorState estate = getRespository().getEditorState(component);
        if (estate == null)
            return null;
        Mode mode = estate.getCommandMode().currentMode();
        if (mode == Mode.MISC_MODE)
            return af.getAction(SLASH_ACTION, ActionFactory.SLASH_MISC_MODE);
        if (mode == Mode.COMMAND_MODE)
            return af.getAction(SLASH_ACTION, ActionFactory.SLASH_COMMAND_MODE);
        return null;
    }
}
