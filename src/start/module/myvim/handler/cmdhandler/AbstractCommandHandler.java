package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import java.util.logging.Logger;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.CommandAction;
import start.module.myvim.utilities.ComponentUtils;

/**
 *
 * @author jker
 */
public abstract class AbstractCommandHandler implements CommandHandler {
    
    protected static final Logger logger = Logger.getLogger("CommandHandler");
    
    //
    private CommandAction action;
    
    //
    private boolean complete;
    

    private BufferedData command;
    
    //flag whether if reset command handler when after executed action 
    private boolean reset = true;
    
    //
    //flag whether if empty BufferedData when after executed action
    private boolean cleanbd = true;
    
    private String content;
    
    //�����������
    private String name;
    
    public AbstractCommandHandler() {
        
    }
    
    public AbstractCommandHandler(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setAction(CommandAction action) {
        this.action = action;
    }
    
    public CommandAction getAction() {
        return action;
    }
    
    public boolean isComplete() {
        return complete;
    }
    
    
    public void setComplete(boolean complete) {
        this.complete = complete;
    }
    
    public void setBufferedData(BufferedData cmd) {
        this.command = cmd;
    }
    
    public BufferedData getBufferedData() {
        return this.command;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    protected void setReset(boolean reset) {
        this.reset = reset;
    }
    
    protected boolean isReset() {
        return this.reset;
    }
    
    protected void setCleanBufferedData(boolean clean) {
        this.cleanbd = clean;
    }
    
    protected boolean isCleanBufferedData() {
        return this.cleanbd;
    }
    
    public void executeAction(ActionEvent e) {
        // Updates this status bar info
        if (isReset()) {
            updateStatusBarInfo("");
        }
        // Executes this Action
        execute(e);
        // Resets this buferred data
        if (isReset()) {
            reset();
        }
    }
    
    //////////////
    public void updateStatusBarInfo(String info) {
        ComponentUtils.updateStatusBarInfo(info);
    }
    
    public void reset() {
        if (isCleanBufferedData()) {
            if (getBufferedData() != null)
                getBufferedData().clean();
        }
        setAction(null);
        setComplete(false);
        setContent("");
        //updateStatuBarInfo("");
        if (getBufferedData() != null) {
            updateStatusBarInfo(getBufferedData().getFullContent());
        } else {
            updateStatusBarInfo("");
        }
    }
    
    protected abstract void execute(ActionEvent e);
    
    //public abstract void reset();
    
    @Override
    public String toString() {
        return "Handler:" + this.name;
    }
    
}
