package start.module.myvim.event;

import java.util.EventObject;
import start.module.myvim.state.Mode;

/**
 *
 * @author jker
 */
public class CommandModeChangeEvent extends EventObject {
    
    private Mode oldMode;
    private Mode newMode;
    
    public CommandModeChangeEvent(Object source, Mode oldMode, Mode newMode) {
        super(source);
        this.oldMode = oldMode;
        this.newMode = newMode;
    }

    public Mode getOldMode() {
        return oldMode;
    }

    public Mode getNewMode() {
        return newMode;
    }

}
