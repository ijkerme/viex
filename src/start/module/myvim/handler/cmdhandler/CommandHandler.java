package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;
import start.module.myvim.handler.BufferedData;
import start.module.myvim.handler.action.CommandAction;


/**
 *
 *  @author jker
 */
public interface CommandHandler {
    
    /**
     *  ��ȡ����������
     */
    public String getName();
    
    public void parse(BufferedData cmd, ActionEvent e);
    
    public void setComplete(boolean complete);
    
    public boolean isComplete();
    
    public void setAction(CommandAction action);
    
    
    //public String getContent();
    
    //public void setContent(String content);
    
    public CommandAction getAction();
    
    public void setBufferedData(BufferedData cmd);
    
    public BufferedData getBufferedData();
    
    public void reset();
    
    public void executeAction(ActionEvent e);
    
}