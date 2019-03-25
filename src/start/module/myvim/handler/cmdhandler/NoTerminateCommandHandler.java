package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.handler.action.CommandAction;

import static start.module.myvim.state.ActionType.*;
import static start.module.myvim.state.CommandType.*;

/**
 *
 * @author jker
 */
public class NoTerminateCommandHandler extends ContentCommandHandler{
    
    private boolean terminate;
    
    public NoTerminateCommandHandler() {
        super("NoTerminateCommandParser");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        setBufferedData(cmd);
        String cmdValue = cmd.getFullContent();
        ActionFactory af = ActionFactory.getInstance();
        String[] dcs = af.getCommands(DEFAULT_COMMAND);
        if (!terminate) { //
            for (int i = 0, length = dcs.length; i < length; i++) {
                String c = dcs[i];
                if (c.startsWith(cmdValue)) {
                    if (c.equals(cmdValue)) {
                        CommandAction ca = af.getAction(DEFAULT_ACTION, c);
                        if (ca != null) {
                            setComplete(true);
                            setAction(ca);
                            break;
                        } else {
                            terminate = false;
                            getBufferedData().clean();
                        }
                    }
                    break;
                } else if((i + 1) == length){
                    terminate = true;
                }
            }
        } 
        if (terminate) {
            String [] ncs = af.getCommands(NO_TERMINATE_COMMAND);
            for (int i = 0, length = ncs.length; i < length; i++) {
                String c = ncs[i];
                if (cmdValue.startsWith(c)) {
                    if (cmdValue.length() >  c.length()) {
                        CommandAction ca = af.getAction(NO_TERMINATE_ACTION, c);
                        if (ca != null) {
                            setComplete(true);
                            setAction(ca);
                            setContent(cmdValue.substring(c.length()));
                            terminate = false;
                            break;
                        } else {
                            terminate = false;
                            reset();
                        }
                    }
                    break;
                } else if ((i + 1) == length) {
                    terminate = false;
                    reset();
                }
            }
        }
    }
    
}
