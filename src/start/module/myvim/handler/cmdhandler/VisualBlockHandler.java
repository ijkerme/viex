package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.handler.action.CommandAction;
import static start.module.myvim.state.CommandType.*;
import static start.module.myvim.state.ActionType.*;

/**
 *
 * @author jker
 */
public class VisualBlockHandler extends ContentCommandHandler {
    
    public VisualBlockHandler() {
        super("VisualBlockParser");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        setBufferedData(cmd);
        String cmdValue = cmd.getFullContent();
        ActionFactory af = ActionFactory.getInstance();
        String[] commands = af.getCommands(VISUAL_BLOCK_COMMAND);
        for (int i = 0, length = commands.length; i < length; i++) {
            String c = commands[i];
            if (c.startsWith(cmdValue)) {
                if (c.equals(cmdValue)) {
                    CommandAction ca = af.getAction(VISUAL_BLOCK_ACTION, c);
                    if (ca != null) {
                        setComplete(true);
                        setAction(ca);
                        setContent(null);
                        break;
                    } else {
                        reset();
                    }
                }
                break;
            } else if ((i + 1) == length) {
                reset();
            }
        }
    }
    
}
