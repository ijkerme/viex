package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.handler.action.CommandAction;
import static start.module.myvim.state.CommandType.*;
import static start.module.myvim.state.ActionType.*;
import static start.module.myvim.utilities.CommandUtils.*;

/**
 *
 * @author jker
 */
public class MiscDigitalCommandHandler extends ContentCommandHandler {
    
    public MiscDigitalCommandHandler() {
        super("MiscDigitalCommandHandler");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        setBufferedData(cmd);
        String cmdValue = cmd.getFullContent();
        ActionFactory af = ActionFactory.getInstance();
        //setContent(cmdValue);
        char c = cmdValue.charAt(cmdValue.length() - 1);
        if (Character.isDigit(c)) {
            CommandAction action = af.getAction(MISC_ACTION, ActionFactory.MISC_DIGITAL);
            action.execute(null, cmdValue);
        } else if (isEndWithEnter(cmdValue)){
            af.getAction(MISC_ACTION, ActionFactory.MISC_DIGITAL).execute(null, cmdValue);            
            //af.getAction(MISC_ACTION, ActionFactory.CLOSE_POPUP).execute(null, null);
            reset();
        } else {
            reset();
        }
    }
    
    
}
