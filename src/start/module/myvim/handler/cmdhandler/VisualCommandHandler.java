package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.ActionFactory;
import start.module.myvim.handler.action.CommandAction;
import static start.module.myvim.utilities.CommandUtils.*;
import static start.module.myvim.state.CommandType.*;
import static start.module.myvim.state.ActionType.*;

/**
 *
 * @author jker
 */
public class VisualCommandHandler extends ContentCommandHandler {
    
    private int count;
    
    public VisualCommandHandler(){
        super("VisualCommandParser");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        setBufferedData(cmd);
        String cmdValue = cmd.getFullContent();
        if (Character.isDigit(cmdValue.charAt(cmdValue.length() - 1))) {
            count += 1;
        } else if (cmdValue.startsWith("'")) {   // insert a pair chars
            if (isEndWithEnter(cmdValue)) {
                ActionFactory af = ActionFactory.getInstance();
                CommandAction ca = af.getInsertCharsPairAction();
                setComplete(true);
                setAction(ca);
                setContent(cmdValue.substring(1, cmdValue.length() - 1));
            }
        } else {
            ActionFactory af = ActionFactory.getInstance();
            String[] commands = af.getCommands(VISUAL_COMMAND);
            String rc = null;
            if (count > 0) {
                rc = cmdValue.substring(count) + "0";
            } else {
                rc = cmdValue;
            }
            for (int i = 0, length = commands.length; i < length; i++) {
                String c = commands[i];
                if (c.startsWith(rc)) {
                    if (c.equals(rc)) {
                        CommandAction ca = af.getAction(VISUAL_ACTION, c);
                        if (ca != null) {
                            setComplete(true);
                            setAction(ca);
                            setContent(cmdValue.substring(0, count));
                            count = 0;
                            break;
                        } else {
                            count = 0;
                            reset();
                        }
                    }
                    break;
                } else if ((i + 1) == length) {
                    count = 0;
                    reset();
                }
            }
        }
    }
    
}
