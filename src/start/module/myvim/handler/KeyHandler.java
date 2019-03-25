package start.module.myvim.handler;

import javax.swing.text.JTextComponent;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import start.module.myvim.handler.cmdhandler.CommandHandler;
import start.module.myvim.handler.cmdhandler.CommandHandlerFactory;
import start.module.myvim.state.EditorState;
import static start.module.myvim.utilities.ComponentUtils.*;
import static start.module.myvim.state.VIEXRepository.*;


/**
 *
 *  @author jker
 */
public class KeyHandler extends AbstractAction {
    
    private static final Logger logger = Logger.getLogger(KeyHandler.class.getName());
    
    private static final KeyHandler keyHandler = new KeyHandler();
    
    private CommandHandler parser;
    
    //private BufferedData command = BufferedData.getInstance();
    
    private KeyHandler() {
    }
    
    public static KeyHandler getKeyHandler() {
        return keyHandler;
    }
    
    public void actionPerformed(ActionEvent e) {
        process(e);
    }
    
    protected void process(ActionEvent e) {
        String actionCommand = e.getActionCommand();
        if ((actionCommand != null) && (actionCommand.length() > 0)) {
            //BufferedCommand command = BufferedCommand.getInstance();
            JTextComponent component = null;
            if (e.getSource() instanceof JTextComponent) 
                component = (JTextComponent)e.getSource();
            else
                return ;
            EditorState estate = getRespository().getEditorState(component);
            if (estate == null)
                return ;
            BufferedData command = estate.getBufferedData();
            char c = actionCommand.charAt(0);
            if (c != KeyEvent.VK_ESCAPE) {            //VK_ESCAPE == 0x1B\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD\uFFFD
                if (command.getFullContent().length() == 0) {
                    if (e != null) {
                        if (e.getSource() instanceof JTextComponent)
                            parser = CommandHandlerFactory.getCommandHandler(String.valueOf(c), (JTextComponent)e.getSource());
                    }
                    //logger.info("" + parser);
                }
                if (parser != null) {
                    String cmdString = command.assembleTypedChar(c);
                    //logger.info("Command:" + cmdString);
                    updateStatusBarInfo(cmdString);
                    parser.parse(command, e);
                    if (parser.isComplete()) {
                        parser.executeAction(e);
                    }
                }
            } else {
                if (parser != null)
                    parser.reset();
                command.clean();
                updateStatusBarInfo("");
            }
        }
    }
    
}