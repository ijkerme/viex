package start.module.myvim.state;

import java.awt.event.KeyEvent;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.KeyStroke;
import javax.swing.text.JTextComponent;
import javax.swing.text.Keymap;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.CommandMode;
import start.module.myvim.handler.KeyHandler;
import start.module.myvim.handler.action.NBActionFacade;
import start.module.myvim.handler.action.HistorySearch;
import start.module.myvim.highlight.HighlightLineQueue;
import start.module.myvim.utilities.EVIConstant;

/**
 *
 * @author jker
 */
public class EditorState {

    private static Logger logger = Logger.getLogger("EditorState");
    
    private BufferedData bufferedData;
    
    private HistorySearch historySearch;
    
    private CommandMode commandMode;
    
    private HighlightLineQueue highlightQueue;
    
    private Keymap keymap;
    
    public BufferedData getBufferedData() {
        if (bufferedData != null)
            return bufferedData;
        logger.fine("new BufferedData instance");
        bufferedData = new BufferedData();
        return bufferedData;
    }
    
    public CommandMode getCommandMode() {
        if (commandMode != null)
            return commandMode;
        logger.fine("new CommandMode instance");
        commandMode = new CommandMode(Mode.INSERT_MODE);
        return commandMode;
    }
    
    public HistorySearch getHistorySearch() {
        if (historySearch != null)
            return historySearch;
        logger.fine("new HistorySearch instance");
        historySearch = new HistorySearch();
        return historySearch;
    }
    
    public HighlightLineQueue getHighlightLineQueue() {
        if (highlightQueue != null)
            return highlightQueue;
        logger.fine("new HighlightLineQueue instance");
        highlightQueue = new HighlightLineQueue();
        return highlightQueue;
    }
    
    public Keymap getKeymap(Keymap parent) {
        if (keymap != null)
            return keymap;
        logger.fine("new Keymap instance");
        keymap = JTextComponent.addKeymap(EVIConstant.EVI_KEYMAP, parent);
        keymap.setResolveParent(parent);
        addEVIKeyAction(keymap);
        keymap.setDefaultAction(KeyHandler.getKeyHandler());
        return keymap;
    }
    
    private static void addEVIKeyAction(Keymap newmap) {
        KeyStroke ekey = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        KeyStroke ikey = KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0);
        KeyStroke bkey = KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0);
        KeyStroke tabKey = KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0);
        KeyStroke pageUpKey = KeyStroke.getKeyStroke(KeyEvent.VK_U, KeyEvent.CTRL_MASK);
        KeyStroke pageDownKey = KeyStroke.getKeyStroke(KeyEvent.VK_D, KeyEvent.CTRL_MASK);
        KeyStroke scrollUpKey = KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_MASK);
        KeyStroke scrollDownKey = KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_MASK);

        Action a = new NBActionFacade.NopAction();
        Action pageUpAction = new NBActionFacade.PageUpAction("PAGE_UP");
        Action pageDownAction = new NBActionFacade.PageDownAction("PAGE_DOWN");
        Action scrollUpAction = new NBActionFacade.ScrollUpAction("SCROLL_UP");
        Action scrollDownAction = new NBActionFacade.ScrollDownAction("SCROLL_DOWN");

        newmap.addActionForKeyStroke(ekey, a);      //added VK_ENTER empty action
        newmap.addActionForKeyStroke(ikey, a);      //added VK_INSERT empty action
        newmap.addActionForKeyStroke(bkey, a);      //added VK_BACK_SPACE empty action
        newmap.addActionForKeyStroke(tabKey, a);    //added VK_TAB empty action 
        newmap.addActionForKeyStroke(pageUpKey, pageUpAction);
        newmap.addActionForKeyStroke(pageDownKey, pageDownAction);
        newmap.addActionForKeyStroke(scrollUpKey, scrollUpAction);
        newmap.addActionForKeyStroke(scrollDownKey, scrollDownAction);
    }
    
}
