package start.module.myvim.state;

import java.util.HashMap;
import java.util.Map;
import javax.swing.text.JTextComponent;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.state.*;

/**
 *
 * @author jker
 */
public class CommandModeOperator {
    
    private static CommandModeOperator operator;
    
    private Map<JTextComponent, CommandMode> modes = new HashMap<JTextComponent, CommandMode>();
    
    private CommandModeOperator() {
        
    }
    
    /**
     * Get CommandModeOperator instance
     * 
     * @return 
     */
    public synchronized static CommandModeOperator getOperator() {
        if (operator == null) {
            operator = new CommandModeOperator();
        }
        return operator;
    }
    
    
    /**
     * Associates the CommandMode state with the specified Component.
     * 
     * @param o 
     */
    public synchronized void put(JTextComponent o, CommandMode cm) {
       modes.put(o, cm);
    }
    
    
    /**
     * Return the CommandMode state associtate with the specified Component.
     * 
     * @param o 
     * @return Return the CommandMode state associtated with the specified component,
     *         return <tt>null<tt> if no found associated with the specified component.
     */
    public synchronized CommandMode get(JTextComponent o) {
        return modes.get(o);
    }
    
    public synchronized void remove(JTextComponent o) {
        modes.remove(o);
    }

    /**
     *  Remove all mappings from the modes.
     */
    public synchronized void removeAll() {
        modes.clear();
    }
    
}
