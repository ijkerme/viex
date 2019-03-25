package start.module.myvim;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.text.JTextComponent;
import static start.module.myvim.state.CommandModeState.*;
import static start.module.myvim.state.Mode.*;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.state.VIEXRepository.*;

/**
 *  @author jker
 */
public class EVIKeyListener extends KeyAdapter{
    
    private static Logger logger = Logger.getLogger("EVIKeylistener");
    
    public void keyPressed(KeyEvent e) {
        process(e);
    }
    
    /**
     * 
     * @param e 
     */
    protected void process(KeyEvent e) {
        int code = e.getKeyCode();
        JTextComponent component = (JTextComponent)e.getSource();
        if (component != null) {
            if (code == KeyEvent.VK_ESCAPE) {
                cancelSelection(component);
                if (isVisualMode(component)) {
                    //CommandModeOperator.getOperator().get(component).switchMode(COMMAND_MODE, component);//
                    getRespository().getEditorState(component).getCommandMode().switchMode(COMMAND_MODE, component);
                    return ;
                }
                if (isCommandMode(component)) {
                    return ;
                }

                addEVIKeymap(component);
            }
        }
    }
    
    private void cancelSelection(JTextComponent component) {
        if (component.getSelectedText() != null) {
            int position = component.getCaretPosition();
            component.setCaretPosition(position);
        }
        removeHighlights(component);
    }

}
