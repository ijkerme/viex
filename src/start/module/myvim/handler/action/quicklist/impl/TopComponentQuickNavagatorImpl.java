package start.module.myvim.handler.action.quicklist.impl;

import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import start.module.myvim.exception.EVIException;
import start.module.myvim.handler.action.quicklist.AbstractQuickNavagatorWorker;
import start.module.myvim.quicklist.TopComponentQuickListItem;
import start.module.myvim.quicklist.VIEXPopup;
import start.module.myvim.quicklist.VIEXQuickListScrollPane;
import start.module.myvim.utilities.VIEXBundle;

/**
 *
 * @author jker
 */
public class TopComponentQuickNavagatorImpl extends AbstractQuickNavagatorWorker {
    
    private static TopComponentQuickNavagatorImpl tcqn = new TopComponentQuickNavagatorImpl();
    private TopComponent[] topComponents;
    private TopComponent activeComponent;
    
    private TopComponentQuickNavagatorImpl(){
    }
    
    public static TopComponentQuickNavagatorImpl getInstance() {
        return tcqn;
    }
    
    public void openQuickList() {
        if (null == target)
            throw new EVIException("component cann't be null which used show popup");
        activeComponent = TopComponent.getRegistry().getActivated();
        if (null == activeComponent)
            return ;
        Mode mode = WindowManager.getDefault().findMode(activeComponent);
        if (null == mode)
            return ;
        topComponents = mode.getTopComponents();
        if (null == topComponents)
            return ;
        setRunning(true);
        new Thread(this).start();
    }

    @Override
    public void run() {
        runTask(topComponents);
        setRunning(false);
    }
    
    private void runTask(TopComponent[] topComponents) {
        results.clear();
        for (int i = 0, counts = topComponents.length; i < counts; i++) {
            if (topComponents[i].isValid() && topComponents[i] != activeComponent)
                results.add(new TopComponentQuickListItem(topComponents[i]));
        }
        if (0 == results.size())
            return ;
        sortResults(results);
        if (isRunning()) {
            VIEXPopup pop = VIEXPopup.getPopup();
            pop.setEditorComponent(target);
            VIEXQuickListScrollPane spane = new VIEXQuickListScrollPane();
            spane.setData(results, VIEXBundle.getMessage("OPENED_EDITOR_LIST"), 0);
            pop.setContentComponent(spane);
            pop.show();
        } else {
            results.clear();
            popup.hide();
        }
    }
    
}
