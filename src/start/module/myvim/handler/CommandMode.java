package start.module.myvim.handler;

import java.util.EventListener;
import javax.swing.event.EventListenerList;
import javax.swing.text.JTextComponent;
import start.module.myvim.state.Mode;
import start.module.myvim.event.CommandModeChangeEvent;
import start.module.myvim.event.CommandModeChangeListener;
import static start.module.myvim.utilities.VIEXBundle.*;


/**
 *
 *  @author jker
 */
public class CommandMode {
    
    private EventListenerList eventList = null;
    
    private String description;
    
    private Mode mode;
    private JTextComponent source;
    
    /**
     *
     * @param mode
     */
    public CommandMode(Mode mode) {
        this.mode = mode;
    }
    
    /**
     *
     * @return
     */
    public synchronized Mode currentMode(){
        return mode;
    }
    
    /**
     *
     * @param modem
     * @param sourcem
     */
    public synchronized void switchMode(Mode modem, JTextComponent sourcem){
        Mode oldMode = this.mode;
        this.mode = modem;
        this.source = sourcem;
        if (sourcem != null)
            fireCommandModeChangeEvent(sourcem, oldMode, mode);
    }
    
    /**
     *
     * @param sourcem
     * @param modem
     */
    protected void fireCommandModeChangeEvent(JTextComponent sourcem, Mode oldMode, Mode newMode) {
        //System.err.println("fire mode changed event");
        if (eventList != null) {
            EventListener[] lists = eventList.getListeners(CommandModeChangeListener.class);
            for (int i = 0, len = lists.length; i < len; i++) {
                CommandModeChangeEvent event = new CommandModeChangeEvent(sourcem, oldMode, newMode);
                CommandModeChangeListener li = (CommandModeChangeListener)lists[i];
                li.modeChanged(event);
            }
        }
    }
    
    /**
     *
     * @param cml
     */
    public void addCommandModeChangeListener(CommandModeChangeListener cml) {
        if (eventList == null)
            eventList = new EventListenerList();
        eventList.add(CommandModeChangeListener.class, cml);
    }
    
    /**
     *
     * @param cml
     */
    public void removeCommandModeChangeListener(CommandModeChangeListener cml) {
        eventList.remove(CommandModeChangeListener.class, cml);
    }
    
    /**
     *
     * @return
     */
    public synchronized String getDescription() {
        if (mode == null)
            return "";
        switch(mode) {
            case NORMAL_MODE:
                description = getMessage("NORMAL_MODE");
                break;
            case INSERT_MODE:
                description = getMessage("INSERT_MODE");
                break;
            case VISUAL_MODE:
                description = getMessage("VISUAL_MODE");
                break;
            case VISUAL_BLOCK_MODE:
                description = getMessage("VISUAL_BLOCK_MODE");
                break;
            case COMMAND_MODE:
                description = getMessage("COMMAND_MODE");
                break;
            case MISC_MODE:
                description = getMessage("MISC_MODE");
                break;
            default:
                break;
        }
        return description;
    }
    
}
