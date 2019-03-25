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
public class DigitalCommandHandler extends ContentCommandHandler {
    
    private int count;
    
    public DigitalCommandHandler() {
        super("DigitalCommandParser");
    }
    
    public void parse(BufferedData cmd, ActionEvent e) {
        setBufferedData(cmd);
        String cmdValue = cmd.getFullContent();
        if (Character.isDigit(cmdValue.charAt(cmdValue.length() - 1))) {
            count += 1;
        } else {
            ActionFactory af = ActionFactory.getInstance();
            String[] commands = af.getCommands(DIGITAL_COMMAND);
            String rc = cmdValue.substring(count);  
            for (int i = 0, length = commands.length; i < length; i++) {
                String c = commands[i];
                if (c.startsWith(rc)) {
                    if (c.equals(rc)) {
                        CommandAction ca = af.getAction(DIGITAL_ACTION, c);
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

    @Override
    public void reset() {
        super.reset();
        count = 0;
    }
    
}
