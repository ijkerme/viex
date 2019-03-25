package start.module.myvim.event;

import java.util.EventListener;

/**
 *
 * @author jker
 */
public interface CommandModeChangeListener extends EventListener {
    
    /**
     * 
     * @param e 
     */
    public void modeChanged(CommandModeChangeEvent e);
    
}
