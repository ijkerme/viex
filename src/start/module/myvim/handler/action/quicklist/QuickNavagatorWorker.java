package start.module.myvim.handler.action.quicklist;

import javax.swing.text.JTextComponent;

/**
 *
 * @author jker
 */
public interface QuickNavagatorWorker extends Runnable {

    /*
     *  component of used show popup,  component cann't be null
     */
    public void setComponent(JTextComponent component);
    
    public JTextComponent getComponent();
    
    public void openQuickList();

    public void prefixMatch(String prefix);
    
    public void reverseList();

    public void up();
    
    public void down();
    
    public void pageUp();
    
    public void pageDown();
    
    public void last();
    
    public void first();

    /**
     *  setting executing task
     */
    public void setRunning(boolean running);
    
    /**
     * whether if QuickNavagatorWorker is executing task
     */
    public boolean isRunning();
    
    /**
     *  cancel show popup that it quick navagator list 
     */
    public void cancel();
    
}
