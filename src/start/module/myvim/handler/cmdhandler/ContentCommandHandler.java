package start.module.myvim.handler.cmdhandler;

import java.awt.event.ActionEvent;

/**
 *
 * @author jker
 */
public abstract class ContentCommandHandler extends AbstractCommandHandler {
    
    public ContentCommandHandler(String name) {
        super(name);
    }
    
    protected void execute(ActionEvent e) {
        if (getAction() != null) {
            getAction().execute(e, getContent());
        }
    }

}
