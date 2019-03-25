package start.module.myvim.handler.action;

import java.awt.event.ActionEvent;


/**
 *  @author jker
 */
public interface CommandAction {
    public void execute(ActionEvent e, String content);
}